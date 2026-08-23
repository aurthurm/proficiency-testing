package zw.org.nmrl.ept.migration;

import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.config.ApplicationProperties;

@Service
@ConditionalOnProperty(prefix = "application.migration.files", name = "enabled", havingValue = "true")
public class LegacyFileMigrationService {

    private static final Logger LOG = LoggerFactory.getLogger(LegacyFileMigrationService.class);

    private final MigrationTarget target;
    private final ApplicationProperties.Migration.Files properties;

    public LegacyFileMigrationService(MigrationTarget target, ApplicationProperties applicationProperties) {
        this.target = target;
        this.properties = applicationProperties.getMigration().getFiles();
    }

    public LegacyFileMigrationSummary migrate(UUID batchId) throws Exception {
        Path sourceRoot = requiredRoot(properties.getSourceRoot(), "EPT_LEGACY_FILES_ROOT");
        Path targetRoot = requiredRoot(properties.getTargetRoot(), "EPT_TARGET_FILES_ROOT");
        if (!Files.isDirectory(sourceRoot, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalArgumentException("Legacy file root is not a directory: " + sourceRoot);
        }
        if (targetRoot.startsWith(sourceRoot)) {
            throw new IllegalArgumentException("Target file root must not be inside the legacy file root");
        }
        Files.createDirectories(targetRoot);

        List<Path> files;
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            files = paths
                .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(path))
                .sorted()
                .toList();
        }

        long copied = 0;
        long unchanged = 0;
        long failed = 0;
        for (Path source : files) {
            String relativePath = portablePath(sourceRoot.relativize(source));
            try {
                String status = Files.isSymbolicLink(source)
                    ? migrateSymbolicLink(sourceRoot, targetRoot, source)
                    : migrateFile(sourceRoot, targetRoot, source);
                if ("COPIED".equals(status)) {
                    copied++;
                } else {
                    unchanged++;
                }
                record(batchId, relativePath, source, targetRoot.resolve(sourceRoot.relativize(source)), status, null);
            } catch (Exception e) {
                failed++;
                LOG.error("Failed to migrate legacy file {}", relativePath, e);
                record(batchId, relativePath, source, null, "FAILED", e.getMessage());
            }
        }
        return new LegacyFileMigrationSummary(files.size(), copied, unchanged, failed);
    }

    private String migrateFile(Path sourceRoot, Path targetRoot, Path source) throws Exception {
        Path target = targetRoot.resolve(sourceRoot.relativize(source)).normalize();
        if (!target.startsWith(targetRoot)) {
            throw new IllegalArgumentException("Legacy path escapes the configured target root: " + source);
        }
        Files.createDirectories(target.getParent());
        String sourceChecksum = sha256(source);
        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
                throw new IllegalStateException("Target exists but is not a regular file: " + target);
            }
            if (sourceChecksum.equals(sha256(target))) {
                return "UNCHANGED";
            }
            if (!properties.isOverwrite()) {
                throw new IllegalStateException("Target differs and overwrite is disabled: " + target);
            }
        }

        Path temporary = target.resolveSibling(target.getFileName() + ".migration-" + UUID.randomUUID() + ".tmp");
        try {
            Files.copy(source, temporary, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            if (!sourceChecksum.equals(sha256(temporary))) {
                throw new IllegalStateException("Checksum mismatch while copying " + source);
            }
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
        return "COPIED";
    }

    private String migrateSymbolicLink(Path sourceRoot, Path targetRoot, Path source) throws Exception {
        Path target = targetRoot.resolve(sourceRoot.relativize(source)).normalize();
        if (!target.startsWith(targetRoot)) {
            throw new IllegalArgumentException("Legacy symbolic-link path escapes the configured target root: " + source);
        }
        Path linkValue = Files.readSymbolicLink(source);
        Files.createDirectories(target.getParent());
        if (Files.isSymbolicLink(target) && linkValue.equals(Files.readSymbolicLink(target))) {
            return "UNCHANGED";
        }
        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS) && !properties.isOverwrite()) {
            throw new IllegalStateException("Target differs and overwrite is disabled: " + target);
        }
        Files.deleteIfExists(target);
        Files.createSymbolicLink(target, linkValue);
        return "COPIED";
    }

    private void record(UUID batchId, String relativePath, Path source, Path migratedTarget, String status, String error) {
        Long sourceSize = sizeOrNull(source);
        Long targetSize = sizeOrNull(migratedTarget);
        String sourceChecksum = checksumOrNull(source);
        String targetChecksum = checksumOrNull(migratedTarget);
        target.write(() ->
            target
                .jdbc()
                .update(
                    "INSERT INTO migration_file_result " +
                        "(batch_id, relative_path, status, source_size, target_size, source_checksum, target_checksum, error_message, migrated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    batchId,
                    relativePath,
                    status,
                    sourceSize,
                    targetSize,
                    sourceChecksum,
                    targetChecksum,
                    error,
                    Timestamp.from(Instant.now())
                )
        );
    }

    private Path requiredRoot(String value, String environmentVariable) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(environmentVariable + " is required when legacy file migration is enabled");
        }
        return Path.of(value).toAbsolutePath().normalize();
    }

    private String portablePath(Path relative) {
        return relative.toString().replace(relative.getFileSystem().getSeparator(), "/");
    }

    private Long sizeOrNull(Path path) {
        try {
            if (path == null) {
                return null;
            }
            if (Files.isSymbolicLink(path)) {
                return (long) Files.readSymbolicLink(path).toString().getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
            }
            return !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) ? null : Files.size(path);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String checksumOrNull(Path path) {
        try {
            if (path == null) {
                return null;
            }
            if (Files.isSymbolicLink(path)) {
                return sha256(Files.readSymbolicLink(path).toString());
            }
            return !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) ? null : sha256(path);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String sha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream input = Files.newInputStream(path); DigestInputStream digestInput = new DigestInputStream(input, digest)) {
            digestInput.transferTo(OutputStreamSink.INSTANCE);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private String sha256(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    }

    private static final class OutputStreamSink extends java.io.OutputStream {

        private static final OutputStreamSink INSTANCE = new OutputStreamSink();

        @Override
        public void write(int ignored) {}

        @Override
        public void write(byte[] bytes, int offset, int length) {}
    }
}
