package systems.reformcloud.reformcloud2.executor.api.common.utility.system;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public static void rename(File file, String newName) {
        Conditions.isTrue(file.renameTo(new File(newName)));
    }

    public static void createDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void doCopy(String from, String target) {
        try (FileInputStream fileInputStream = new FileInputStream(from); FileOutputStream fileOutputStream = new FileOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteDirectory(Path path) {
        final File[] files = path.toFile().listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file.toPath());
            } else {
                deleteFile(file);
            }
        }

        deleteFile(path.toFile());
    }

    public static void recreateDirectory(Path path) {
        if (Files.exists(path)) {
            if (path.toFile().isDirectory()) {
                deleteDirectory(path);
            } else {
                deleteFile(path.toFile());
            }
        }

        createDirectory(path);
    }

    public static void doInternalCopy(ClassLoader classLoader, String file, String target) {
        if (Files.exists(Paths.get(target))) {
            return;
        }

        try (InputStream inputStream = classLoader.getResourceAsStream(file)) {
            doCopy(inputStream, Paths.get(target));
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void copyDirectory(Path path, String target) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = Paths.get(target, path.relativize(file).toString());
                    Path parent = targetFile.getParent();

                    if (parent != null && !Files.exists(parent)) {
                        Files.createDirectories(parent);
                    }

                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    static void doCopy(InputStream inputStream, Path path, CopyOption... options) {
        try {
            Files.copy(inputStream, path, options);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void unZip(File zippedPath, String destinationPath) {
        try {
            File destDir = new File(destinationPath);
            if (!destDir.exists()) {
                createDirectory(destDir.toPath());
            }

            if (destDir.isDirectory()) {
                deleteDirectory(destDir.toPath());
            } else {
                deleteFile(destDir);
            }

            byte[] buffer = new byte[0x1FFF];
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zippedPath));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destinationPath + "/" + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    createDirectory(newFile.toPath());
                } else {
                    createFile(newFile.toPath());

                    try (OutputStream outputStream = Files.newOutputStream(newFile.toPath())) {
                        int length;
                        while ((length = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }

                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

}
