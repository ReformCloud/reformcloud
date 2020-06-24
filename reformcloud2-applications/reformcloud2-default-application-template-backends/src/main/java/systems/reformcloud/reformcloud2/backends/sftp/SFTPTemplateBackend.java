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
package systems.reformcloud.reformcloud2.backends.sftp;

import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.*;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public final class SFTPTemplateBackend implements TemplateBackend {

    private static final BlockingDeque<Runnable> TASKS = new LinkedBlockingDeque<>();
    private final SFTPConfig config;
    private Session session;
    private ChannelSftp channel;

    private SFTPTemplateBackend(SFTPConfig config) {
        this.config = config;
        this.open();

        NetworkUtil.EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                try {
                    Runnable runnable = TASKS.poll(20, TimeUnit.SECONDS);
                    boolean available = !this.isDisconnected();

                    if (runnable == null) {
                        if (available) {
                            this.channel.disconnect();
                        }

                        continue;
                    }

                    if (!available) {
                        this.open();
                    }

                    runnable.run();
                } catch (final InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void load(String baseDirectory) {
        if (Files.notExists(Paths.get(baseDirectory, "sftp.json"))) {
            new JsonConfiguration()
                    .add("config", new SFTPConfig(
                            false, "127.0.0.1", 22, "rc", "password", "/home/templates/"
                    )).write(Paths.get(baseDirectory, "sftp.json"));
        }

        SFTPConfig config = JsonConfiguration.read(Paths.get(baseDirectory, "sftp.json")).get("config", new TypeToken<SFTPConfig>() {
        });
        if (config == null || !config.isEnabled()) {
            return;
        }

        TemplateBackendManager.registerBackend(new SFTPTemplateBackend(config));
    }

    public static void unload() {
        TemplateBackendManager.unregisterBackend("SFTP");
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

    private boolean isDisconnected() {
        return this.session == null || !this.session.isConnected() || this.channel == null || !this.channel.isConnected();
    }

    @Override
    public boolean existsTemplate(@NotNull String group, @NotNull String template) {
        if (this.isDisconnected()) {
            return false;
        }

        try {
            SftpATTRS attrs = this.channel.stat(this.config.getBaseDirectory() + group + "/" + template);
            return attrs != null && attrs.isDir();
        } catch (final SftpException ex) {
            return false;
        }
    }

    @Override
    public void createTemplate(@NotNull String group, @NotNull String template) {
        if (this.isDisconnected()) {
            return;
        }

        future(() -> this.makeDirectory(this.config.getBaseDirectory() + group + "/" + template));
    }

    @NotNull
    @Override
    public Task<Void> loadTemplate(@NotNull String group, @NotNull String template, @NotNull Path target) {
        if (this.isDisconnected()) {
            return Task.completedTask(null);
        }

        return future(() -> this.downloadDirectory(this.config.getBaseDirectory() + group + "/" + template, target.toString()));
    }

    public void downloadDirectory(String remotePath, String localPath) {
        try {
            Collection<ChannelSftp.LsEntry> entries = this.listFiles(remotePath);
            if (entries == null) {
                return;
            }

            Path dir = Paths.get(localPath);
            IOUtils.recreateDirectory(dir);
            if (!localPath.endsWith("/")) {
                localPath += "/";
            }

            if (!remotePath.endsWith("/")) {
                remotePath += "/";
            }

            for (ChannelSftp.LsEntry entry : entries) {
                if (entry.getAttrs().isDir()) {
                    this.downloadDirectory(remotePath + entry.getFilename(), localPath + entry.getFilename());
                } else {
                    try (OutputStream outputStream = Files.newOutputStream(Paths.get(localPath, entry.getFilename()))) {
                        this.channel.get(remotePath + entry.getFilename(), outputStream);
                    }
                }
            }
        } catch (final SftpException | IOException ex) {
            ex.printStackTrace();
        }
    }

    @NotNull
    @Override
    public Task<Void> loadGlobalTemplates(@NotNull ProcessGroup group, @NotNull Path target) {
        return future(() -> Streams.allOf(group.getTemplates(), e -> e.getBackend().equals(this.getName())
                && e.isGlobal()).forEach(e -> this.loadTemplate(group.getName(), e.getName(), target)));
    }

    @NotNull
    @Override
    public Task<Void> loadPath(@NotNull String path, @NotNull Path target) {
        if (this.isDisconnected()) {
            return Task.completedTask(null);
        }

        return future(() -> this.downloadDirectory(this.config.getBaseDirectory() + path, target.toString()));
    }

    @Override
    public void deployTemplate(@NotNull String group, @NotNull String template, @NotNull Path current, @NotNull Collection<String> collection) {
        if (this.isDisconnected()) {
            return;
        }

        future(() -> {
            try {
                File[] files = current.toFile().listFiles(e -> {
                    String full = e.getAbsolutePath()
                            .replaceFirst(current.toFile().getAbsolutePath(), "")
                            .replaceFirst("\\\\", "");
                    return !collection.contains(full);
                });
                if (files == null || files.length == 0) {
                    return;
                }

                this.makeDirectory(this.config.getBaseDirectory() + group + "/" + template);
                for (File file : files) {
                    this.upload(this.config.getBaseDirectory() + group + "/" + template, file);
                }
            } catch (final SftpException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void upload(String path, File file) throws SftpException {
        String currentPath = path + "/" + file.getName();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return;
            }

            this.makeDirectory(currentPath);
            for (File next : files) {
                this.upload(currentPath, next);
            }
        } else if (file.isFile()) {
            this.channel.put(file.getPath(), currentPath);
        }
    }

    @Override
    public void deleteTemplate(@NotNull String group, @NotNull String template) {
        if (this.isDisconnected()) {
            return;
        }

        this.deleteAll(this.config.getBaseDirectory() + group + "/" + template);
    }

    private void deleteAll(String path) {
        try {
            Collection<ChannelSftp.LsEntry> entries = this.listFiles(path);
            if (entries == null) {
                return;
            }

            for (ChannelSftp.LsEntry entry : entries) {
                if (entry.getAttrs().isDir()) {
                    this.deleteAll(path + "/" + entry.getFilename());
                } else {
                    this.channel.rm(path + "/" + entry.getFilename());
                }
            }

            this.channel.rmdir(path);
        } catch (final SftpException ex) {
            ex.printStackTrace();
        }
    }

    private void makeDirectory(String path) {
        StringBuilder builder = new StringBuilder();
        for (String pathSegment : path.split("/")) {
            builder.append('/').append(pathSegment);
            try {
                this.channel.mkdir(builder.toString());
            } catch (final SftpException ignored) {
                // dir already exists
            }
        }

        this.goToBase();
    }

    private void goToBase() {
        try {
            this.channel.cd(this.config.getBaseDirectory().startsWith("/") ? this.config.getBaseDirectory() : "/" + this.config.getBaseDirectory());
        } catch (final SftpException ex) {
            ex.printStackTrace();
        }
    }

    public Collection<ChannelSftp.LsEntry> listFiles(String directory) {
        Collection<ChannelSftp.LsEntry> entries = new ArrayList<>();
        try {
            this.channel.ls(directory, lsEntry -> {
                if (!lsEntry.getFilename().equals("..") && !lsEntry.getFilename().equals(".")) {
                    entries.add(lsEntry);
                }

                return 0;
            });
        } catch (final SftpException ex) {
            return null;
        }

        return entries;
    }

    private void open() {
        try {
            this.session = new JSch().getSession(this.config.getUser(), this.config.getHost(), this.config.getPort());
            this.session.setPassword(this.config.getPassword());
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect(2500);

            this.channel = (ChannelSftp) this.session.openChannel("sftp");
            if (this.channel == null) {
                this.session.disconnect();
                this.session = null;
                return;
            }

            this.channel.connect();
            this.channel.setFilenameEncoding(StandardCharsets.UTF_8.name());
        } catch (final JSchException | SftpException ex) {
            ex.printStackTrace();
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "SFTP";
    }
}
