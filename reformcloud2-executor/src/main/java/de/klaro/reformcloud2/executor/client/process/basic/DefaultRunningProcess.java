package de.klaro.reformcloud2.executor.client.process.basic;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.process.ProcessState;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.client.ClientExecutor;
import de.klaro.reformcloud2.executor.client.packet.out.ClientPacketOutProcessPrepared;
import de.klaro.reformcloud2.executor.client.process.PortUtil;
import net.md_5.config.Configuration;
import net.md_5.config.ConfigurationProvider;
import net.md_5.config.YamlConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class DefaultRunningProcess implements RunningProcess {

    public DefaultRunningProcess(ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    private final ProcessInformation processInformation;

    private Path path;

    private boolean prepared = false;

    private Process process;

    private final AtomicLong startupTime = new AtomicLong(-1);

    @Override
    public long getStartupTime() {
        return startupTime.get();
    }

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

        int port = PortUtil.checkPort(processInformation.getNetworkInfo().getPort());
        processInformation.getNetworkInfo().setPort(port);
        chooseLogicallyStartup();

        if (!Files.exists(Paths.get("reformcloud/files/runner.jar"))) {
            DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
        }

        SystemHelper.doCopy("reformcloud/files/runner.jar", path + "/runner.jar");

        String motd;
        if (processInformation.getTemplate().getMotd() == null) {
            motd = "A ReformCloud2 Process";
        } else {
            motd = processInformation.getTemplate().getMotd();
        }

        processInformation.setMotd(motd);
        new JsonConfiguration()
                .add("controller-host", ClientExecutor.getInstance().getClientExecutorConfig().getClientConnectionConfig().getHost())
                .add("controller-port", ClientExecutor.getInstance().getClientExecutorConfig().getClientConnectionConfig().getPort())
                .add("startInfo", processInformation)
                .write(path + "/reformcloud/.connection/connection.json");

        ExecutorAPI.getInstance().update(processInformation);
        prepared = true;

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
            @Override
            public void accept(PacketSender packetSender) {
                packetSender.sendPacket(new ClientPacketOutProcessPrepared(
                        processInformation.getName(),
                        processInformation.getProcessUniqueID(),
                        processInformation.getTemplate().getName()
                ));
            }
        });
        return this;
    }

    @Override
    public boolean bootstrap() {
        if (!prepared) {
            return false;
        }

        List<String> command = new ArrayList<>(Arrays.asList(
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
                command.add("-D" + s + "=" + s2);
            }
        });

        command.addAll(Arrays.asList(
                "-jar",
                "runner.jar"
        ));
        command.addAll(this.processInformation.getTemplate().getRuntimeConfiguration().getProcessParameters());
        updateCommandLine(command, processInformation.getTemplate().getVersion());

        try {
            this.process = new ProcessBuilder()
                    .directory(path.toFile())
                    .command(command)
                    .start();
        } catch (final IOException ex) {
            ex.printStackTrace();
            return false;
        }

        processInformation.setProcessState(ProcessState.STARTED);
        ExecutorAPI.getInstance().update(processInformation);
        startupTime.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean shutdown() {
        sendCommand("stop");
        sendCommand("end");

        AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 100);

        process.destroyForcibly().destroy();

        if (running()) {
            process.destroyForcibly().destroy();
        }

        if (running()) {
            process.destroyForcibly().destroy();
        }

        ClientExecutor.getInstance().getProcessManager().unregisterProcess(processInformation.getName());
        if (!processInformation.getProcessGroup().isStaticProcess()) {
            SystemHelper.deleteDirectory(path);
        }

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
        rewriteFile(config, new UnaryOperator<String>() {
            @Override
            public String apply(String s) {
                if (s.startsWith("ip-forwarding=")) {
                    s = "ip-forwarding=true";
                } else if (s.startsWith("bungeecord=")) {
                    s = "bungeecord=true";
                }

                return s;
            }
        });
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
    //Bungee
    private boolean isLogicallyBungee() {
        Version version = processInformation.getTemplate().getVersion();
        return version.equals(Version.WATERFALL)
                || version.equals(Version.TRAVERTINE)
                || version.equals(Version.HEXACORD)
                || version.equals(Version.BUNGEECORD);
    }

    private void rewriteBungeeConfig() {
        File file = Paths.get(path + "/config.yml").toFile();
        rewriteFile(file, new UnaryOperator<String>() {
            @Override
            public String apply(String s) {
                if (s.startsWith("  host:")) {
                    s = "  host: " + ClientExecutor.getInstance().getClientConfig().getStartHost() + ":" + processInformation.getNetworkInfo().getPort();
                } else if (s.startsWith("ip_forward:")) {
                    s = "ip_forward: true";
                }

                return s;
            }
        });
    }

    // ========================= //
    //Waterdog
    private void rewriteWaterDogConfig() {
        File file = Paths.get(path + "/config.yml").toFile();
        rewriteFile(file, new UnaryOperator<String>() {
            @Override
            public String apply(String s) {
                if (s.startsWith("  host:")) {
                    s = "  host: " + ClientExecutor.getInstance().getClientConfig().getStartHost() + ":" + processInformation.getNetworkInfo().getPort();
                } else if (s.startsWith("ip_forward:")) {
                    s = "ip_forward: true";
                } else if (s.startsWith("use_xuid_for_uuid:")) {
                    s = "use_xuid_for_uuid: true";
                } else if (s.startsWith("  raknet:")) {
                    s = "  raknet: " + processInformation.getTemplate().getVersion().equals(Version.WATERDOG_PE);
                }

                return s;
            }
        });
    }

    private boolean isLogicallyWaterDog() {
        return processInformation.getTemplate().getVersion().equals(Version.WATERDOG)
                || processInformation.getTemplate().getVersion().equals(Version.WATERDOG_PE);
    }

    // ========================= //
    //Velocity
    private void rewriteVelocityConfig() {
        File file = Paths.get(path + "/velocity.toml").toFile();
        rewriteFile(file, new UnaryOperator<String>() {
            @Override
            public String apply(String s) {
                if (s.startsWith("bind")) {
                    s = "bind = \"" + ClientExecutor.getInstance().getClientConfig().getStartHost() + ":" + processInformation.getNetworkInfo().getPort() + "\"";
                } else if (s.startsWith("show-max-players") && processInformation.getProcessGroup().getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
                    s = "show-max-players = " + processInformation.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
                } else if (s.startsWith("player-info-forwarding-mode")) {
                    s = "player-info-forwarding-mode = \"LEGACY\"";
                }

                return s;
            }
        });
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
            proxyStartup();
        }
    }

    private void serverStartup() {
        createEula();
        createTemplateAndFiles();

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
        } else if (processInformation.getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/mcpe/nukkit/server.properties", path + "/server.properties");
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/mcpe/nukkit/nukkit.yml", path + "/nukkit.yml");
            Properties properties = new Properties();
            try (InputStream inputStream = Files.newInputStream(Paths.get(path + "/server.properties"))) {
                properties.load(inputStream);
                properties.setProperty("server-ip", ClientExecutor.getInstance().getClientConfig().getStartHost());
                properties.setProperty("server-port", Integer.toString(processInformation.getNetworkInfo().getPort()));
                properties.setProperty("xbox-auth", Boolean.toString(false));

                try (OutputStream outputStream = Files.newOutputStream(Paths.get(path + "/server.properties"))) {
                    properties.store(outputStream, "ReformCloud2 client edit");
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        } else {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/bukkit/spigot.yml", path + "/spigot.yml");
            rewriteSpigotConfig();
        }

        if (!isLogicallyGlowstone() && !processInformation.getTemplate().getVersion().equals(Version.NUKKIT_X)) {
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
            if (!Files.exists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }

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

    private void proxyStartup() {
        createTemplateAndFiles();
        if (!Files.exists(Paths.get(path + "/server-icon.png"))) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/server-icon.png", path + "/server-icon.png");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(Paths.get(path + "/server-icon.png").toFile());
            if (bufferedImage.getHeight() != 64 || bufferedImage.getWidth() != 64) {
                System.err.println("The server icon of the process " + processInformation.getName() + " is not correctly sized");
                SystemHelper.rename(Paths.get(path + "/server-icon.png").toFile(), path + "/server-icon-old.png");
                SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/server-icon.png", path + "/server-icon.png");
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        if (isLogicallyBungee()) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/bungee/config.yml", path + "/config.yml");
            rewriteBungeeConfig();
        } else if (isLogicallyWaterDog()) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/mcpe/waterdog/config.yml", path + "/config.yml");
            rewriteWaterDogConfig();
        } else if (processInformation.getTemplate().getVersion().equals(Version.VELOCITY)) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/velocity/velocity.toml", path + "/velocity.toml");
            rewriteVelocityConfig();
        }

        if (!Files.exists(Paths.get(path + "/process.jar"))) {
            Version version = processInformation.getTemplate().getVersion();
            if (!Files.exists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }

            SystemHelper.doCopy("reformcloud/files/" + Version.format(version), path + "/process.jar");
        }
    }

    private void createTemplateAndFiles() {
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
    }

    // ========================= //
    //Static
    private static void rewriteFile(File file, Function<String, String> function) {
        try {
            List<String> list = Files.readAllLines(file.toPath());
            List<String> newLine = new ArrayList<>();

            list.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    newLine.add(function.apply(s));
                }
            });

            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8), true)) {
                newLine.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        printWriter.write(s + "\n");
                        printWriter.flush();
                    }
                });
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void updateCommandLine(List<String> command, Version version) {
        if (version.getId() == 1) {
            command.add("nogui");
        } else if (version.getId() == 3) {
            command.add("disable-ansi");
        }
    }
}
