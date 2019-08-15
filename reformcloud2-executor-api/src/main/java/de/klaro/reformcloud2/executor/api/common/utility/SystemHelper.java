package de.klaro.reformcloud2.executor.api.common.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class SystemHelper {

    public static void deleteFile(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
