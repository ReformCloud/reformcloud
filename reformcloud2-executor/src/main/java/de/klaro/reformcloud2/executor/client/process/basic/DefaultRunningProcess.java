package de.klaro.reformcloud2.executor.client.process.basic;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.process.ProcessState;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.client.ClientExecutor;
import net.md_5.config.Configuration;
import net.md_5.config.ConfigurationProvider;
import net.md_5.config.YamlConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class DefaultRunningProcess implements RunningProcess {

    public DefaultRunningProcess(ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    private final ProcessInformation processInformation;

    private Path path;

    private boolean prepared = false;

    private Process process;

    @Override
    public RunningProcess prepare() {
        processInformation.setProcessState(ProcessState.PREPARED);

        if (processInformation.getProcessGroup().isStaticProcess()) {
            this.path = Paths.get("reformcloud/static/" + processInformation.getName());
            SystemHelper.createDirectory(path);
        } else {
            this.path = Paths.get("reformcloud/temp/" + processInformation.getName() + "-" + processInformation.getProcessUniqueID());
            SystemHelper.recreateDirectory(path);
        }

        chooseLogicallyStartup();

        if (!Files.exists(Paths.get("reformcloud/files/runner.jar"))) {
            DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
        }

        SystemHelper.doCopy("reformcloud/files/runner.jar", path + "/runner.jar");

        new JsonConfiguration()
                .add("controller-host", ClientExecutor.getInstance().getClientExecutorConfig().getClientConnectionConfig().getHost())
                .add("controller-port", ClientExecutor.getInstance().getClientExecutorConfig().getClientConnectionConfig().getPort())
                .write(path + "/reformcloud/.connection/connection.json");

        ExecutorAPI.getInstance().update(processInformation);
        prepared = true;
        return this;
    }

    @Override
    public boolean bootstrap() {
        if (!prepared) {
            return false;
        }

        List<String> pre = new ArrayList<>(Arrays.asList(
                "java",
                "-XX:+UseG1GC",
                "-XX:MaxGCPauseMillis=50",
                "-XX:-UseAdaptiveSizePolicy",
                "-XX:CompileThreshold=100",
                "-Dcom.mojang.eula.agree=true",
                "-DIReallyKnowWhatIAmDoingISwear=true",
                "-Djline.terminal=jline.UnsupportedTerminal",
                "-Dreformcloud.executor.type=3",
                "-Xmx" + processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory() + "M"
        ));

        this.processInformation.getTemplate().getRuntimeConfiguration().getSystemProperties().forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                pre.add("-D" + s + "=" + s2);
            }
        });

        List<String> after = new ArrayList<>(Arrays.asList(
                "-jar",
                "runner.jar",
                "nogui"
        ));

        this.processInformation.getTemplate().getRuntimeConfiguration().getProcessParameters().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                after.add(s);
            }
        });

        List<String> fullCommand = new ArrayList<>();
        pre.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                fullCommand.add(s);
            }
        });

        after.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                fullCommand.add(s);
            }
        });

        try {
            this.process = Runtime.getRuntime().exec(fullCommand.toArray(new String[0]),null, path.toFile());
        } catch (final IOException ex) {
            ex.printStackTrace();
            return false;
        }

        processInformation.setProcessState(ProcessState.STARTED);
        ExecutorAPI.getInstance().update(processInformation);
        return true;
    }

    @Override
    public boolean shutdown() {
        process.destroyForcibly().destroy();

        if (running()) {
            process.destroyForcibly().destroy();
        }

        if (running()) {
            process.destroyForcibly().destroy();
        }

        ClientExecutor.getInstance().getProcessManager().unregisterProcess(processInformation.getName());

        try {
            this.finalize();
        } catch (final Throwable ignored) {
        }

        return !running();
    }

    @Override
    public boolean sendCommand(String command) {
        if (running()) {
            try {
                process.getOutputStream().write((command + "\n").getBytes());
                process.getOutputStream().flush();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        return running();
    }

    @Override
    public boolean running() {
        try {
            return process != null && process.getInputStream().available() != -1 && process.isAlive();
        } catch (final IOException ex) {
            return false;
        }
    }

    @Override
    public ProcessInformation getProcessInformation() {
        return processInformation;
    }

    /* ================================= */

    private void createEula() {
        try (InputStream inputStream = DefaultRunningProcess.class.getClassLoader().getResourceAsStream("files/java/bukkit/eula.txt")) {
            Files.copy(Objects.requireNonNull(inputStream), Paths.get(path + "/eula.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    //Sponge
    private boolean isLogicallySpongeVanilla() {
        Version version = processInformation.getTemplate().getVersion();
        return version.equals(Version.SPONGEVANILLA_1_8_9)
                || version.equals(Version.SPONGEVANILLA_1_9_4)
                || version.equals(Version.SPONGEVANILLA_1_10_2)
                || version.equals(Version.SPONGEVANILLA_1_11_2)
                || version.equals(Version.SPONGEVANILLA_1_12_2);
    }

    private boolean isLogicallySpongeForge() {
        Version version = processInformation.getTemplate().getVersion();
        return version.equals(Version.SPONGEFORGE_1_8_9)
                || version.equals(Version.SPONGEFORGE_1_10_2)
                || version.equals(Version.SPONGEFORGE_1_11_2)
                || version.equals(Version.SPONGEFORGE_1_12_2);
    }

    private void rewriteSpongeConfig() {
        File config = Paths.get(path + "/config/sponge/global.conf").toFile();

        writeToFile(config, Objects.requireNonNull(readToString(config))
                .replace("ip-forwarding=false", "ip-forwarding=true")
                .replace("bungeecord=false", "bungeecord=true")
        );
    }

    // ========================= //
    //Glowstone
    private boolean isLogicallyGlowstone() {
        Version version = processInformation.getTemplate().getVersion();
        return version.equals(Version.GLOWSTONE_1_7_9)
                || version.equals(Version.GLOWSTONE_1_8_9)
                || version.equals(Version.GLOWSTONE_1_9_4)
                || version.equals(Version.GLOWSTONE_1_10_2)
                || version.equals(Version.GLOWSTONE_1_11_2)
                || version.equals(Version.GLOWSTONE_1_12_2);
    }

    private void rewriteGlowstoneConfig() {
        try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(Paths.get(path + "/config/glowstone.yml")), StandardCharsets.UTF_8)) {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
            Configuration section = configuration.getSection("server");

            section.set("ip", ClientExecutor.getInstance().getClientConfig().getStartHost());
            section.set("port", processInformation.getNetworkInfo().getPort());
            section.set("log-file", "logs/latest.log");
            section.set("online-mode", false);

            if (processInformation.getProcessGroup().getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
                section.set("max-players", processInformation.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers());
            }

            configuration.set("server", section);
            configuration.set("console.use-jline", false);
            configuration.set("console.prompt", "");
            configuration.set("advanced.proxy-support", true);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get(path + "/config/glowstone.yml")), StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    // ========================= //
    //Spigot
    private void rewriteSpigotConfig() {
        try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(Paths.get(path + "/spigot.yml")), StandardCharsets.UTF_8)) {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
            configuration.set("settings.bungeecord", true);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get(path + "/spigot.yml")), StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    // ========================= //
    //Startup

    private void chooseLogicallyStartup() {
        if (processInformation.getTemplate().isServer()) {
            serverStartup();
        } else {

        }
    }

    private void serverStartup() {
        if (processInformation.getTemplate().isServer()) {
            createEula();
        }

        if (processInformation.getTemplate().getDownloadURL() != null) {
            DownloadHelper.downloadAndDisconnect(processInformation.getTemplate().getDownloadURL(), path + "/template.zip");
            SystemHelper.unZip(Paths.get(path + "/template.zip").toFile(), path.toString());
            SystemHelper.deleteFile(Paths.get(path + "/template.zip").toFile());
        } else {
            Path template = Paths.get("reformcloud/templates/" + processInformation.getProcessGroup().getName() + "/" + processInformation.getTemplate().getName());
            if (Files.exists(template)) {
                SystemHelper.copyDirectory(template, path.toString());
            } else {
                SystemHelper.createDirectory(template);
            }
        }

        SystemHelper.copyDirectory(Paths.get("reformcloud/.bin/libs"), path + "/reformcloud/.bin/libs");
        SystemHelper.createDirectory(Paths.get(path + "/plugins"));
        SystemHelper.createDirectory(Paths.get(path + "/reformcloud/.connection"));
        SystemHelper.doCopy("reformcloud/files/.connection/connection.json", path + "/reformcloud/.connection/key.json");

        if (isLogicallySpongeVanilla()) {
            SystemHelper.createDirectory(Paths.get(path + "/config/sponge"));
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/sponge/vanilla/global.conf", path + "/config/sponge");
            rewriteSpongeConfig();
        } else if (isLogicallySpongeForge()) {
            SystemHelper.createDirectory(Paths.get(path + "/config/sponge"));
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/sponge/forge/global.conf", path + "/config/sponge");
            rewriteSpongeConfig();
        } else if (isLogicallyGlowstone()) {
            SystemHelper.createDirectory(Paths.get(path + "/config"));
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/glowstone/glowstone.yml", path + "/config/glowstone.yml");
            rewriteGlowstoneConfig();
        } else {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/bukkit/spigot.yml", path + "/spigot.yml");
            rewriteSpigotConfig();
        }

        if (!isLogicallyGlowstone()) {
            Properties properties = new Properties();
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/bukkit/server.properties", path + "/server.properties");
            try (InputStream inputStream = Files.newInputStream(Paths.get(path + "/server.properties"))) {
                properties.load(inputStream);
                properties.setProperty("server-ip", ClientExecutor.getInstance().getClientConfig().getStartHost());
                properties.setProperty("server-port", Integer.toString(processInformation.getNetworkInfo().getPort()));
                properties.setProperty("online-mode", Boolean.toString(false));

                try (OutputStream outputStream = Files.newOutputStream(Paths.get(path + "/server.properties"))) {
                    properties.store(outputStream, "ReformCloud2 client edit");
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        if (!isLogicallySpongeForge() && !Files.exists(Paths.get(path + "/process.jar"))) {
            Version version = processInformation.getTemplate().getVersion();
            Version.downloadVersion(version);
            SystemHelper.doCopy("reformcloud/files/" + Version.format(version), path + "/process.jar");
        } else if (isLogicallySpongeForge()) {
            Version version = processInformation.getTemplate().getVersion();
            if (!Files.exists(Paths.get("reformcloud/files/" + version.getName() + ".zip"))) {
                DownloadHelper.downloadAndDisconnect(
                        version.getUrl(), "reformcloud/files/" + version.getName() + ".zip"
                );
            }

            SystemHelper.doCopy("reformcloud/files/" + version.getName() + ".zip", path + "/version.zip");
            SystemHelper.unZip(Paths.get(path + "/version.zip").toFile(), path.toString());
            SystemHelper.rename(Paths.get(path + "/sponge.jar").toFile(), path + "/process.jar");
        }
    }

    // ========================= //
    //Static
    private static String readToString(File file) {
        try {
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void writeToFile(File file, String s) {
        try {
            FileUtils.write(file, s, StandardCharsets.UTF_8);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
