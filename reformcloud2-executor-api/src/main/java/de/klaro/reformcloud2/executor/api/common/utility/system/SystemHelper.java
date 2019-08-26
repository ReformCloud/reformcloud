package de.klaro.reformcloud2.executor.api.common.utility.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SystemHelper {

    public static void deleteFile(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createFile(Path path) {
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                try {
                    Files.createDirectories(parent);
                    Files.createFile(path);
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void createDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void doCopy(InputStream inputStream, Path path, CopyOption... options) {
        try {
            Files.copy(inputStream, path, options);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
