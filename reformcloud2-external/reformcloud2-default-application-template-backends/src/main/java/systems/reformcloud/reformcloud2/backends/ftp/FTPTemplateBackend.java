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
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FTPTemplateBackend implements TemplateBackend {

    public static void load(String basePath) {
        if (Files.notExists(Paths.get(basePath, "ftp.json"))) {
            new JsonConfiguration()
                    .add("config", new FTPConfig(
                            false, false, "127.0.0.1", 21, "rc", "password", "rc/templates"
                    )).write(Paths.get(basePath, "ftp.json"));
        }

        FTPConfig config = JsonConfiguration.read(Paths.get(basePath, "ftp.json")).get("config", new TypeToken<FTPConfig>() {});
        if (config == null || !config.isEnabled()) {
            return;
        }

        TemplateBackendManager.registerBackend(new FTPTemplateBackend(config));
    }

    public static void unload() {
        TemplateBackendManager.unregisterBackend("FTP");
    }

    private final FTPClient ftpClient;

    private final FTPConfig config;

    private FTPTemplateBackend(FTPConfig ftpConfig) {
        this.config = ftpConfig;
        this.ftpClient = ftpConfig.isSslEnabled() ? new FTPSClient() : new FTPClient();

        try {
            this.ftpClient.setAutodetectUTF8(true);

            this.ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
            this.ftpClient.login(ftpConfig.getUser(), ftpConfig.getPassword());

            this.ftpClient.sendNoOp();
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            this.makeDirectory(ftpConfig.getBaseDirectory());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean existsTemplate(String group, String template) {
        if (this.ftpClient == null || !this.ftpClient.isConnected()) {
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
        if (this.ftpClient == null || !this.ftpClient.isConnected()) {
            return;
        }

        try {
            this.makeDirectory(group + "/" + template);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void loadTemplate(String group, String template, Path target) {
        if (this.ftpClient == null || !this.ftpClient.isConnected()) {
            return;
        }

        try {
            FTPFile[] files = this.ftpClient.listFiles(group + "/" + template);
            if (files == null || files.length == 0) {
                return;
            }

            for (FTPFile file : files) {
                this.loadFiles(file, group + "/" + template + "/" + file.getName(), target);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
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
    }

    @Override
    public void loadGlobalTemplates(ProcessGroup group, Path target) {
        if (this.ftpClient == null || !this.ftpClient.isConnected()) {
            return;
        }

        Streams.allOf(group.getTemplates(), e -> e.getBackend().equals(getName())
                && e.isGlobal()).forEach(e -> this.loadTemplate(group.getName(), e.getName(), target));
    }

    @Override
    public void deployTemplate(String group, String template, Path current) {
        if (this.ftpClient == null || !this.ftpClient.isConnected()) {
            return;
        }

        File[] localFiles = current.toFile().listFiles();
        if (localFiles == null || localFiles.length == 0) {
            return;
        }

        try {
            this.makeDirectory(group + "/" + template);

            for (File localFile : localFiles) {
                this.writeFile(current.toString(), localFile);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
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
    }

    private void makeDirectory(String path) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : path.split("/")) {
            stringBuilder.append(s).append("/");
            String current = stringBuilder.toString();

            if (!this.ftpClient.changeWorkingDirectory(current)) {
                this.ftpClient.makeDirectory(current);
            }

            this.ftpClient.changeWorkingDirectory(this.config.getBaseDirectory());
        }
    }

    @Override
    public void deleteTemplate(String group, String template) {
        if (this.ftpClient == null || !this.ftpClient.isConnected()) {
            return;
        }

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

    @Nonnull
    @Override
    public String getName() {
        return "FTP";
    }
}
