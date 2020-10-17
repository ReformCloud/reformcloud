package systems.reformcloud.reformcloud2.shared.dependency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import systems.reformcloud.reformcloud2.executor.api.dependency.Dependency;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

class DependencyFileLoaderTest {

    @Test
    void testCollectDependenciesFromFile() {
        String fileLines = "central=>https://repo1.maven.org/ org.jetbrains.annotations:annotations:20.1.0\n"
            + " \n"
            + "cloud=>https://repo.reformcloud.systems systems.reformcloud.reformcloud2:reformcloud2-shared:2.1.0\n"
            + "\n";
        try (InputStream inputStream = new ByteArrayInputStream(fileLines.getBytes(StandardCharsets.UTF_8))) {
            Collection<Dependency> dependencies = DependencyFileLoader.collectDependenciesFromFile(inputStream);
            Assertions.assertEquals(2, dependencies.size());

            // Check if the line 'central=>https://repo1.maven.org/ org.jetbrains.annotations:annotations:20.1.0' was parsed correctly
            Assertions.assertTrue(dependencies.stream().anyMatch(
                // check the dependency information
                d -> d.groupId().equals("org.jetbrains.annotations") && d.artifactId().equals("annotations") && d.version().equals("20.1.0")
                    // Checks the repository information
                    && d.repository().id().equals("central") && d.repository().url().equals("https://repo1.maven.org/")
            ));
            // Check if the line 'cloud=>https://repo.reformcloud.systems systems.reformcloud.reformcloud2:reformcloud2-shared:2.1.0' was parsed correctly
            Assertions.assertTrue(dependencies.stream().anyMatch(
                // check the dependency information
                d -> d.groupId().equals("systems.reformcloud.reformcloud2") && d.artifactId().equals("reformcloud2-shared") && d.version().equals("2.1.0")
                    // Checks the repository information
                    && d.repository().id().equals("cloud") && d.repository().url().equals("https://repo.reformcloud.systems")
            ));
        } catch (IOException exception) {
            Assertions.fail(exception);
        }
    }
}
