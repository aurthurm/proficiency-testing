package zw.org.nmrl.ept.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.config.ApplicationProperties;

/** Safe, read-only access to the checksum-verified legacy file tree. */
@Service
@ConditionalOnProperty(prefix = "application.legacy-files", name = "enabled", havingValue = "true")
public class LegacyFileStore {

    private final Path root;

    public LegacyFileStore(ApplicationProperties properties) {
        String configuredRoot = properties.getLegacyFiles().getRoot();
        if (configuredRoot == null || configuredRoot.isBlank()) {
            throw new IllegalStateException("EPT_LEGACY_FILE_STORE_ROOT must not be blank");
        }
        this.root = Path.of(configuredRoot).toAbsolutePath().normalize();
    }

    public InputStream open(String portableReference) throws IOException {
        return Files.newInputStream(resolve(portableReference));
    }

    public Path resolve(String portableReference) throws IOException {
        if (portableReference == null || portableReference.isBlank()) {
            throw new IllegalArgumentException("Legacy file reference is required");
        }
        String normalizedReference = portableReference.replace('\\', '/');
        while (normalizedReference.startsWith("/")) {
            normalizedReference = normalizedReference.substring(1);
        }
        Path candidate = root.resolve(normalizedReference).normalize();
        if (!candidate.startsWith(root)) {
            throw new IllegalArgumentException("Legacy file reference escapes the configured root: " + portableReference);
        }
        if (!Files.exists(candidate, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Legacy file does not exist: " + portableReference);
        }
        Path realRoot = root.toRealPath();
        Path realCandidate = candidate.toRealPath();
        if (!realCandidate.startsWith(realRoot) || !Files.isRegularFile(realCandidate)) {
            throw new IOException("Legacy file reference resolves outside the configured root: " + portableReference);
        }
        return realCandidate;
    }
}
