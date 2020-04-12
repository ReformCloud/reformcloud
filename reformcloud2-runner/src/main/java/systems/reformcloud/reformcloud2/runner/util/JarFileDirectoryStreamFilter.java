package systems.reformcloud.reformcloud2.runner.util;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a filter for directory streams which checks if a file is a jar file
 */
public final class JarFileDirectoryStreamFilter implements DirectoryStream.Filter<Path> {

    @Override
    public boolean accept(Path entry) {
        return Files.isRegularFile(entry) && entry.getFileName().toString().endsWith(".jar");
    }
}
