package systems.reformcloud.reformcloud2.runner.instrumentation;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.util.JarFileDirectoryStreamFilter;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

public final class RunnerPremain {

    public static void premain(@NotNull String agentArgs, @NotNull Instrumentation instrumentation) {
        if (System.getProperty("reformcloud.lib.path") == null || System.getProperty("reformcloud.process.path") == null) {
            return;
        }

        Path path = Paths.get(System.getProperty("reformcloud.lib.path") + "/reformcloud/.bin/libs/");
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new RuntimeException("Unable to parse runtime libs path");
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, new JarFileDirectoryStreamFilter())) {
            instrumentation.appendToSystemClassLoaderSearch(new JarFile(System.getProperty("reformcloud.process.path")));

            for (Path value : stream) {
                instrumentation.appendToSystemClassLoaderSearch(new JarFile(value.toFile()));
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
