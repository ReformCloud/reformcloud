/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.backends.sftp;

import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.LoggerFactory;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.FileMode;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;
import systems.reformcloud.base.Conditions;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.group.template.Template;
import systems.reformcloud.group.template.backend.TemplateBackend;
import systems.reformcloud.node.template.TemplateBackendManager;
import systems.reformcloud.task.Task;
import systems.reformcloud.utility.MoreCollections;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public final class SFTPTemplateBackend implements TemplateBackend {

  private final SFTPConfig config;

  private SSHClient sshClient;
  private SFTPClient sftpClient;

  private SFTPTemplateBackend(SFTPConfig config) {
    this.config = config;
    this.ensureConnected();
  }

  public static void load(Path configPath) {
    if (Files.notExists(configPath)) {
      JsonConfiguration.newJsonConfiguration().add("config", new SFTPConfig(
        false, "127.0.0.1", 22, "rc", "password", "rc/templates"
      )).write(configPath);
    }

    SFTPConfig config = JsonConfiguration.newJsonConfiguration(configPath).get("config", SFTPConfig.class);
    if (config == null || !config.isEnabled()) {
      return;
    }

    config.validate();
    TemplateBackendManager.registerBackend(new SFTPTemplateBackend(config));
  }

  public static void unload() {
    TemplateBackendManager.unregisterBackend("SFTP");
  }

  @Override
  public boolean existsTemplate(@NotNull String group, @NotNull String template) {
    this.ensureConnected();

    try {
      FileAttributes fileAttributes = this.sftpClient.statExistence(this.config.getBaseDirectory() + group + "/" + template);
      return fileAttributes != null && fileAttributes.getType() != null && fileAttributes.getType() == FileMode.Type.DIRECTORY;
    } catch (IOException exception) {
      return false;
    }
  }

  @Override
  public void createTemplate(@NotNull String group, @NotNull String template) {
    Task.runAsync(() -> {
      this.ensureConnected();
      this.executeSilently(() -> this.sftpClient.mkdirs(this.config.getBaseDirectory() + group + "/" + template));
    });
  }

  @Override
  public @NotNull Task<Void> loadTemplate(@NotNull String group, @NotNull String template, @NotNull Path target) {
    return this.executeTask(() -> this.downloadDirectory(this.config.getBaseDirectory() + group + "/" + template, target.toString()));
  }

  @Override
  public @NotNull Task<Void> loadGlobalTemplates(@NotNull ProcessGroup group, @NotNull Path target) {
    Collection<Task<Void>> tasks = new ArrayList<>();
    for (Template template : group.getTemplates()) {
      if (template.isGlobal()) {
        tasks.add(this.loadTemplate(group.getName(), template.getName(), target));
      }
    }

    return Task.supply(() -> {
      while (MoreCollections.hasMatch(tasks, t -> !t.isDone())) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException exception) {
          break;
        }
      }

      return null;
    });
  }

  @Override
  public @NotNull Task<Void> loadPath(@NotNull String path, @NotNull Path target) {
    return this.executeTask(() -> this.downloadDirectory(this.config.getBaseDirectory() + path, target.toString()));
  }

  @Override
  public void deployTemplate(@NotNull String group, @NotNull String template, @NotNull Path current, @NotNull Collection<String> excluded) {
    Task.runAsync(() -> {
      this.executeSilently(() -> this.deleteDirectory(this.config.getBaseDirectory() + group + "/" + template));
      this.executeSilently(() -> this.uploadDirectory(this.config.getBaseDirectory() + group + "/" + template, current.toString(), excluded));
    });
  }

  @Override
  public void deleteTemplate(@NotNull String group, @NotNull String template) {
    Task.runAsync(() -> {
      this.ensureConnected();
      this.executeSilently(() -> this.deleteDirectory(this.config.getBaseDirectory() + group + "/" + template));
    });
  }

  @Override
  public @NotNull String getName() {
    return "SFTP";
  }

  protected @NotNull Task<Void> executeTask(@NotNull ExceptionRunnable runnable) {
    return Task.supply(() -> {
      this.ensureConnected();
      runnable.run();
      return null;
    });
  }

  protected boolean isReady() {
    return this.sshClient != null && this.sftpClient != null && this.sshClient.isConnected() && this.sshClient.isAuthenticated();
  }

  protected void ensureConnected() {
    if (!this.isReady()) {
      this.connect();
      Conditions.isTrue(this.isReady());
    }
  }

  protected void downloadDirectory(String remoteDir, String localDir) throws IOException {
    if (!remoteDir.endsWith("/")) {
      remoteDir += "/";
    }

    if (!localDir.endsWith("/")) {
      localDir += "/";
    }

    Path local = Paths.get(localDir);
    Files.createDirectories(local);

    for (RemoteResourceInfo resourceInfo : this.sftpClient.ls(remoteDir)) {
      if (resourceInfo.isDirectory()) {
        this.downloadDirectory(remoteDir + resourceInfo.getName(), localDir + resourceInfo.getName());
        continue;
      }

      Files.createFile(local.resolve(resourceInfo.getName()));
      this.sftpClient.get(remoteDir + resourceInfo.getName(), localDir + resourceInfo.getName());
    }
  }

  protected void deleteDirectory(String remoteDir) throws IOException {
    if (!remoteDir.endsWith("/")) {
      remoteDir += "/";
    }

    for (RemoteResourceInfo resourceInfo : this.sftpClient.ls(remoteDir)) {
      if (resourceInfo.isDirectory()) {
        this.deleteDirectory(remoteDir + resourceInfo.getName());
        continue;
      }

      this.sftpClient.rm(remoteDir + resourceInfo.getName());
    }

    this.sftpClient.rmdir(remoteDir);
  }

  protected void uploadDirectory(String remoteDir, String localDir, Collection<String> excluded) throws IOException {
    if (!remoteDir.endsWith("/")) {
      remoteDir += "/";
    }

    if (!localDir.endsWith("/")) {
      localDir += "/";
    }

    try {
      this.sftpClient.mkdir(remoteDir);
    } catch (SFTPException ignored) {
      // discard silently
    }

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(localDir))) {
      for (Path path : stream) {
        Path fileName = path.getFileName();
        if (fileName == null || excluded.contains(fileName.toString())) {
          continue;
        }

        if (Files.isDirectory(path)) {
          this.uploadDirectory(remoteDir + path.getFileName(), path.toString(), excluded);
          continue;
        }

        this.sftpClient.put(path.toString(), remoteDir + fileName);
      }
    }
  }

  protected void connect() {
    this.sshClient = new SSHClient(new InternalConfig());
    this.sshClient.setConnectTimeout(3000);
    this.sshClient.setTimeout(3000);
    this.sshClient.setRemoteCharset(StandardCharsets.UTF_8);

    if (this.config.getKnownHostsFile() == null) {
      this.executeSilently(() -> this.sshClient.loadKnownHosts());
      this.sshClient.addHostKeyVerifier(new PromiscuousVerifier());
    } else {
      try {
        this.sshClient.loadKnownHosts(new File(this.config.getKnownHostsFile()));
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }

    try {
      this.sshClient.connect(this.config.getHost(), this.config.getPort());
    } catch (IOException exception) {
      throw new RuntimeException("Unable to connect to remote ssh host", exception);
    }

    try {
      if (this.config.getPrivateKeyFile() == null) {
        this.sshClient.authPassword(this.config.getUser(), this.config.getPassword());
      } else {
        this.sshClient.authPublickey(this.config.getUser(), this.config.getPrivateKeyFile());
      }
    } catch (UserAuthException exception) {
      throw new RuntimeException("Unable to authenticate with remote host", exception);
    } catch (TransportException exception) {
      throw new RuntimeException("Transportation exception while authenticating with remote host", exception);
    }

    try {
      this.sftpClient = this.sshClient.newSFTPClient();
    } catch (IOException exception) {
      throw new RuntimeException("Exception starting the sftp sub system", exception);
    }
  }

  protected void executeSilently(@NotNull ExceptionRunnable runnable) {
    try {
      runnable.run();
    } catch (Exception ignored) {
    }
  }

  @FunctionalInterface
  private interface ExceptionRunnable {

    void run() throws Exception;
  }

  private static class InternalConfig extends DefaultConfig {

    @Override
    public LoggerFactory getLoggerFactory() {
      return InternalLoggingFactory.NOP_FACTORY;
    }
  }

  private static class InternalLoggingFactory implements LoggerFactory {

    private static final LoggerFactory NOP_FACTORY = new InternalLoggingFactory();

    @Override
    public Logger getLogger(String name) {
      return NOPLogger.NOP_LOGGER;
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
      return NOPLogger.NOP_LOGGER;
    }
  }
}
