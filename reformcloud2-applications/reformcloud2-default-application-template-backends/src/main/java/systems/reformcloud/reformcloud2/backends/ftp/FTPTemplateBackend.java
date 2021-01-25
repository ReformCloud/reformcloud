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
package systems.reformcloud.reformcloud2.backends.ftp;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;
import systems.reformcloud.reformcloud2.node.template.TemplateBackendManager;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public final class FTPTemplateBackend implements TemplateBackend {

  private static final Executor EXECUTOR = Executors.newCachedThreadPool();
  private static final BlockingDeque<Runnable> TASKS = new LinkedBlockingDeque<>();
  private final FTPClient ftpClient;
  private final FTPConfig config;

  private FTPTemplateBackend(FTPConfig ftpConfig) {
    this.config = ftpConfig;
    this.ftpClient = ftpConfig.isSslEnabled() ? new FTPSClient() : new FTPClient();
    this.open(ftpConfig);

    EXECUTOR.execute(() -> {
      while (!Thread.interrupted()) {
        try {
          Runnable runnable = TASKS.poll(20, TimeUnit.SECONDS);
          boolean available = this.ftpClient.isAvailable();

          if (runnable == null) {
            if (!available) {
              continue;
            }

            try {
              this.ftpClient.disconnect();
            } catch (final Throwable ignored) {
            }

            continue;
          }

          if (!available) {
            this.open(this.config);
          }

          runnable.run();
        } catch (final InterruptedException ignored) {
        }
      }
    });
  }

  public static void load(Path configPath) {
    if (Files.notExists(configPath)) {
      new JsonConfiguration().add("config", new FTPConfig(
        false, false, "127.0.0.1", 21, "rc", "password", "rc/templates"
      )).write(configPath);
    }

    FTPConfig config = JsonConfiguration.read(configPath).get("config", new TypeToken<>() {
    });
    if (config == null || !config.isEnabled()) {
      return;
    }

    TemplateBackendManager.registerBackend(new FTPTemplateBackend(config));
  }

  public static void unload() {
    TemplateBackendManager.unregisterBackend("FTP");
  }

  private static Task<Void> future(@NotNull Runnable runnable) {
    Task<Void> completableFuture = new DefaultTask<>();
    Runnable newRunnable = () -> {
      runnable.run();
      completableFuture.complete(null);
    };
    TASKS.offerLast(newRunnable);
    return completableFuture;
  }

  @Override
  public boolean existsTemplate(@NotNull String group, @NotNull String template) {
    if (this.ftpClient == null) {
      return false;
    }

    try {
      return this.ftpClient.listFiles(group + "/" + template).length > 0;
    } catch (final IOException ex) {
      return false;
    }
  }

  @Override
  public void createTemplate(@NotNull String group, @NotNull String template) {
    if (this.ftpClient == null) {
      return;
    }

    future(() -> {
      try {
        this.makeDirectory(group + "/" + template);
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
    });
  }

  @NotNull
  @Override
  public Task<Void> loadTemplate(@NotNull String group, @NotNull String template, @NotNull Path target) {
    if (this.ftpClient == null) {
      return Task.completedTask(null);
    }

    return future(() -> {
      try {
        FTPFile[] files = this.ftpClient.listFiles(group + "/" + template);
        if (files == null || files.length == 0) {
          return;
        }

        for (FTPFile file : files) {
          this.loadFiles(file, group + "/" + template + "/" + file.getName(), Path.of(target.toString(), file.getName()));
        }
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
    });
  }

  private void loadFiles(FTPFile file, String path, Path target) throws IOException {
    if (file.isDirectory()) {
      FTPFile[] files = this.ftpClient.listFiles(path);
      if (files == null || files.length == 0) {
        return;
      }

      for (FTPFile ftpFile : files) {
        this.loadFiles(ftpFile, path + "/" + ftpFile.getName(), Path.of(target.toString(), ftpFile.getName()));
      }
    } else if (file.isFile()) {
      IOUtils.createDirectory(target.getParent());
      if (Files.notExists(target)) {
        Files.createFile(target);
      }

      try (OutputStream outputStream = Files.newOutputStream(target)) {
        this.ftpClient.retrieveFile(path, outputStream);
      }
    }

    this.ftpClient.changeWorkingDirectory(this.config.getBaseDirectory());
  }

  @NotNull
  @Override
  public Task<Void> loadGlobalTemplates(@NotNull ProcessGroup group, @NotNull Path target) {
    if (this.ftpClient == null) {
      return Task.completedTask(null);
    }

    return future(() ->
      MoreCollections.allOf(group.getTemplates(), e -> e.getBackend().equals(this.getName())
        && e.isGlobal()).forEach(e -> this.loadTemplate(group.getName(), e.getName(), target))
    );
  }

  @NotNull
  @Override
  public Task<Void> loadPath(@NotNull String path, @NotNull Path target) {
    if (this.ftpClient == null) {
      return Task.completedTask(null);
    }

    return future(() -> {
      try {
        FTPFile[] files = this.ftpClient.listFiles(path);
        if (files == null || files.length == 0) {
          this.makeDirectory(path);
          return;
        }

        for (FTPFile file : files) {
          this.loadFiles(file, path + "/" + file.getName(), Path.of(target.toString(), file.getName()));
        }
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
    });
  }

  @Override
  public void deployTemplate(@NotNull String group, @NotNull String template, @NotNull Path current, @NotNull Collection<String> collection) {
    if (this.ftpClient == null) {
      return;
    }

    future(() -> {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(current, path -> !MoreCollections.hasMatch(collection, path::endsWith))) {
        for (Path path : stream) {
          this.writeFile(group + "/" + template, path, collection);
        }
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    });
  }

  private void writeFile(String path, Path local, Collection<String> collection) throws IOException {
    String remotePath = path + "/" + local.getFileName().toString();
    if (Files.isDirectory(local)) {
      this.makeDirectory(remotePath);
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(local, p -> !MoreCollections.hasMatch(collection, p::endsWith))) {
        for (Path file : stream) {
          this.writeFile(remotePath, file, collection);
        }
      }
    } else {
      try (InputStream inputStream = Files.newInputStream(local)) {
        this.ftpClient.storeFile(remotePath, inputStream);
      }
    }

    this.ftpClient.changeWorkingDirectory("/" + this.config.getBaseDirectory());
  }

  private void makeDirectory(String path) throws IOException {
    for (String s : path.split("/")) {
      if (!this.ftpClient.changeWorkingDirectory(s)) {
        this.ftpClient.makeDirectory(s);
      }

      this.ftpClient.changeWorkingDirectory(s);
    }

    this.ftpClient.changeWorkingDirectory(this.config.getBaseDirectory());
  }

  @Override
  public void deleteTemplate(@NotNull String group, @NotNull String template) {
    if (this.ftpClient == null) {
      return;
    }

    TASKS.offerLast(() -> {
      try {
        FTPFile[] files = this.ftpClient.mlistDir(group + "/" + template);
        if (files == null || files.length == 0) {
          return;
        }

        for (FTPFile file : files) {
          this.deleteAll(group + "/" + template, file);
        }
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
    });
  }

  private void deleteAll(String path, FTPFile file) throws IOException {
    String filePath = path + "/" + file.getName();

    if (file.isDirectory()) {
      FTPFile[] files = this.ftpClient.listFiles(filePath);
      if (files == null || files.length == 0) {
        return;
      }

      for (FTPFile ftpFile : files) {
        this.deleteAll(filePath, ftpFile);
      }
    } else {
      this.ftpClient.deleteFile(filePath);
    }
  }

  private void open(FTPConfig ftpConfig) {
    try {
      this.ftpClient.setAutodetectUTF8(true);

      this.ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
      this.ftpClient.login(ftpConfig.getUser(), ftpConfig.getPassword());

      this.ftpClient.sendNoOp();
      this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      this.ftpClient.setControlKeepAliveTimeout(60);

      this.makeDirectory(ftpConfig.getBaseDirectory());
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }

  @NotNull
  @Override
  public String getName() {
    return "FTP";
  }
}
