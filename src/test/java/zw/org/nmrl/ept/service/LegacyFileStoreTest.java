package zw.org.nmrl.ept.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import zw.org.nmrl.ept.config.ApplicationProperties;

class LegacyFileStoreTest {

    @TempDir
    Path root;

    @Test
    void resolvesPortableReferencesInsideConfiguredRoot() throws Exception {
        Path file = root.resolve("reports/accepted.pdf");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "accepted");

        LegacyFileStore store = store(root);

        assertThat(store.resolve("reports/accepted.pdf")).isEqualTo(file.toRealPath());
        try (InputStream input = store.open("reports/accepted.pdf")) {
            assertThat(new String(input.readAllBytes())).isEqualTo("accepted");
        }
    }

    @Test
    void rejectsPathTraversal() {
        LegacyFileStore store = store(root);

        assertThatThrownBy(() -> store.resolve("../secret.txt"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("escapes");
    }

    private LegacyFileStore store(Path root) {
        ApplicationProperties properties = new ApplicationProperties();
        properties.getLegacyFiles().setRoot(root.toString());
        return new LegacyFileStore(properties);
    }
}
