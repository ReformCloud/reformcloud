package systems.reformcloud.reformcloud2.backends.ftp;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public final class FTPTemplateBackend implements TemplateBackend {

    public static void load(String basePath) {
        if (Files.notExists(Paths.get(basePath, "ftp.json"))) {
            new JsonConfiguration()
                    .add("config", new FTPConfig(
                            false, false, "127.0.0.1", 21, "rc", "password", "rc/templates"
                    )).write(Paths.get(basePath, "ftp.json"));
        }

        FTPConfig config = JsonConfiguration.read(Paths.get(basePath, "ftp.json")).get("config", new TypeToken<FTPConfig>() {
        });
        if (config == null || !config.isEnabled()) {
            return;
        }

        TemplateBackendManager.registerBackend(new FTPTemplateBackend(config));
    }

    public static void unload() {
        TemplateBackendManager.unregisterBackend("FTP");
    }

    private static final BlockingDeque<Runnable> TASKS = new LinkedBlockingDeque<>();

    private final FTPClient ftpClient;

    private final FTPConfig config;

    private FTPTemplateBackend(FTPConfig ftpConfig) {
        this.config = ftpConfig;
        this.ftpClient = ftpConfig.isSslEnabled() ? new FTPSClient() : new FTPClient();
        this.open(ftpConfig);

        NetworkUtil.EXECUTOR.execute(() -> {
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
                } catch (final InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean existsTemplate(String group, String template) {
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
    public void createTemplate(String group, String template) {
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

    @Nonnull
    @Override
    public Task<Void> loadTemplate(String group, String template, Path target) {
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
                    this.loadFiles(file, group + "/" + template + "/" + file.getName(), Paths.get(target.toString(), file.getName()));
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
                this.loadFiles(ftpFile, path + "/" + ftpFile.getName(), Paths.get(target.toString(), ftpFile.getName()));
            }
        } else if (file.isFile()) {
            SystemHelper.createDirectory(target.getParent());
            if (Files.notExists(target)) {
                Files.createFile(target);
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(target.toFile(), false)) {
                this.ftpClient.retrieveFile(path, fileOutputStream);
            }
        }

        this.ftpClient.changeWorkingDirectory(this.config.getBaseDirectory());
    }

    @Nonnull
    @Override
    public Task<Void> loadGlobalTemplates(ProcessGroup group, Path target) {
        if (this.ftpClient == null) {
            return Task.completedTask(null);
        }

        return future(() ->
                Streams.allOf(group.getTemplates(), e -> e.getBackend().equals(getName())
                        && e.isGlobal()).forEach(e -> this.loadTemplate(group.getName(), e.getName(), target))
        );
    }

    @Nonnull
    @Override
    public Task<Void> loadPath(String path, Path target) {
        if (this.ftpClient == null) {
            return Task.completedTask(null);
        }

        return future(() -> {
            try {
                FTPFile[] files = this.ftpClient.listFiles(path);
                if (files == null || files.length == 0) {
                    return;
                }

                for (FTPFile file : files) {
                    this.loadFiles(file, path + "/" + file.getName(), Paths.get(target.toString(), file.getName()));
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void deployTemplate(String group, String template, Path current) {
        if (this.ftpClient == null) {
            return;
        }

        File[] localFiles = current.toFile().listFiles();
        if (localFiles == null || localFiles.length == 0) {
            return;
        }

        future(() -> {
            try {
                for (File localFile : localFiles) {
                    this.writeFile(group + "/" + template, localFile);
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void writeFile(String path, File local) throws IOException {
        String remotePath = path + "/" + local.getName();
        if (local.isDirectory()) {
            File[] localFiles = local.listFiles();
            if (localFiles == null || localFiles.length == 0) {
                return;
            }

            this.makeDirectory(remotePath);

            for (File localFile : localFiles) {
                this.writeFile(remotePath, localFile);
            }
        } else if (local.isFile()) {
            try (InputStream inputStream = new FileInputStream(local)) {
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
    public void deleteTemplate(String group, String template) {
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

    private static Task<Void> future(@Nonnull Runnable runnable) {
        Task<Void> completableFuture = new DefaultTask<>();
        Runnable newRunnable = () -> {
            runnable.run();
            completableFuture.complete(null);
        };
        TASKS.offerLast(newRunnable);
        return completableFuture;
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

    @Nonnull
    @Override
    public String getName() {
        return "FTP";
    }
}
