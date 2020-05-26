/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.io;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@ApiStatus.Internal
public final class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException();
    }

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

    public static void createDirectory(@Nullable Path path) {
        if (path == null) {
            return;
        }

        try {
            Files.createDirectories(path);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void doCopy(String from, String target) {
        try (FileInputStream fileInputStream = new FileInputStream(from);
             FileOutputStream fileOutputStream = new FileOutputStream(target)) {
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

    public static void copyDirectory(Path path, Path target) {
        copyDirectory(path, target, new ArrayList<>());
    }

    public static void copyDirectory(Path path, Path target, Collection<String> excludedFiles) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (excludedFiles.stream().anyMatch(e -> e.equals(file.toFile().getName()))) {
                        return FileVisitResult.CONTINUE;
                    }

                    Path targetFile = Paths.get(target.toString(), path.relativize(file).toString());
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
        byte[] buffer = new byte[0x1FFF];
        File destDir = new File(destinationPath);
        if (!destDir.exists()) {
            createDirectory(destDir.toPath());
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zippedPath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
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
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
