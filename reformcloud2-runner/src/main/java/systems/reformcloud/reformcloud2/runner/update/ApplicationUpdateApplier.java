package systems.reformcloud.reformcloud2.runner.update;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public final class ApplicationUpdateApplier {

    private ApplicationUpdateApplier() {
        throw new UnsupportedOperationException();
    }

    private static final File APP_FOLDER = new File("reformcloud/applications");

    public static void applyUpdates() {
        if (Files.notExists(Paths.get("reformcloud/.update/apps")) || Files.notExists(APP_FOLDER.toPath())) {
            return;
        }

        try {
            Files.walkFileTree(Paths.get("reformcloud/.update/apps"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (findAndDeleteOther(file.toFile().getName())) {
                        Files.copy(file, Paths.get("reformcloud/applications", file.toFile().getName()));
                        delete(file.toFile());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private static boolean findAndDeleteOther(String newFileName) {
        File[] files = APP_FOLDER.listFiles();
        if (files == null) {
            return false;
        }

        String[] split = newFileName.split("-");
        String name = newFileName
                .replace("-" + split[split.length - 1], "")
                .replace(".jar", "");

        for (File file : files) {
            if (file.getName().startsWith(name) && !file.isDirectory() && file.getName().endsWith(".jar")) {
                delete(file);
                return true;
            }
        }

        return false;
    }

    private static void delete(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
