/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.shared.io;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@ApiStatus.Internal
public final class IOUtils {

  private IOUtils() {
    throw new UnsupportedOperationException();
  }

  public static void deleteFile(String path) {
    deleteFile(Paths.get(path));
  }

  public static void deleteFile(Path file) {
    try {
      Files.deleteIfExists(file);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static void rename(Path file, String newName) {
    try {
      Files.move(file, file.resolveSibling(newName));
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static void createDirectory(@Nullable Path path) {
    if (path != null) {
      try {
        Files.createDirectories(path);
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }
  }

  public static void copy(String from, Path target) {
    copy(Paths.get(from), target);
  }

  public static void copy(Path from, Path target) {
    IOUtils.createDirectory(target.getParent());
    try (OutputStream outputStream = Files.newOutputStream(target)) {
      Files.copy(from, outputStream);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static void deleteAllFilesInDirectory(Path dirPath) {
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirPath)) {
      for (Path path : directoryStream) {
        if (!Files.isDirectory(path)) {
          deleteFile(path);
        }
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static void deleteDirectory(Path dirPath) {
    try {
      doDeleteDirectory(dirPath);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static void deleteDirectorySilently(Path dirPath) {
    try {
      doDeleteDirectory(dirPath);
    } catch (IOException ignored) {
    }
  }

  private static void doDeleteDirectory(Path dirPath) throws IOException {
    Collection<IOException> exceptions = new ArrayList<>();
    deleteDirectoryChecked(dirPath, exceptions);

    if (!exceptions.isEmpty()) {
      throw new IOException("Caught " + exceptions.size() + " exceptions: " + exceptions.stream().map(IOException::getMessage).collect(Collectors.joining(", ")));
    }
  }

  private static void deleteDirectoryChecked(Path dir, Collection<IOException> exceptions) {
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
      for (Path path : directoryStream) {
        if (Files.isDirectory(path)) {
          deleteDirectoryChecked(path, exceptions);
        } else {
          try {
            Files.delete(path);
          } catch (IOException exception) {
            exceptions.add(exception);
          }
        }
      }
    } catch (IOException exception) {
      exceptions.add(exception);
    }

    try {
      Files.delete(dir);
    } catch (IOException exception) {
      exceptions.add(exception);
    }
  }

  public static void recreateDirectory(Path path) {
    if (Files.exists(path)) {
      if (path.toFile().isDirectory()) {
        deleteDirectory(path);
      } else {
        deleteFile(path);
      }
    }

    createDirectory(path);
  }

  public static void copyCompiledFile(ClassLoader classLoader, String file, String target) {
    try (InputStream inputStream = classLoader.getResourceAsStream(file)) {
      Files.copy(Objects.requireNonNull(inputStream), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException exception) {
      exception.printStackTrace();
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

          if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
          }

          Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  static void copy(InputStream inputStream, Path path, CopyOption... options) {
    try {
      Files.copy(inputStream, path, options);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static void unZip(Path zippedPath, Path destinationPath) {
    if (Files.notExists(destinationPath)) {
      createDirectory(destinationPath);
    } else if (!Files.isDirectory(destinationPath)) {
      throw new UnsupportedOperationException("Cannot unzip to non-directory target");
    }

    byte[] buffer = new byte[8191];
    try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zippedPath), StandardCharsets.UTF_8)) {
      ZipEntry zipEntry;
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        Path target = destinationPath.resolve(zipEntry.getName());
        if (zipEntry.isDirectory()) {
          createDirectory(target);
        } else {
          try (OutputStream outputStream = Files.newOutputStream(target)) {
            int length;
            while ((length = zipInputStream.read(buffer)) != -1) {
              outputStream.write(buffer, 0, length);
            }
          }
        }

        zipInputStream.closeEntry();
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
