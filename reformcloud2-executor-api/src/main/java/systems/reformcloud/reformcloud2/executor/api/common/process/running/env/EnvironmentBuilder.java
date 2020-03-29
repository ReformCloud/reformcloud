package systems.reformcloud.reformcloud2.executor.api.common.process.running.env;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.UnaryOperator;

public final class EnvironmentBuilder {

    private EnvironmentBuilder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Constructs the env for the specified process
     *
     * @param runningProcess The process for which we are building the env
     */
    public static void constructEnvFor(@NotNull RunningProcess runningProcess) {
        chooseLogicallyStartup(runningProcess);
    }

    private static void createEula(RunningProcess runningProcess) {
        try (InputStream inputStream = EnvironmentBuilder.class.getClassLoader().getResourceAsStream("files/java/bukkit/eula.txt")) {
            Files.copy(Objects.requireNonNull(inputStream), Paths.get(runningProcess.getPath() + "/eula.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    //Sponge
    private static boolean isLogicallySpongeVanilla(RunningProcess runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.SPONGEVANILLA_1_11_2) || version.equals(Version.SPONGEVANILLA_1_12_2);
    }

    private static boolean isLogicallySpongeForge(RunningProcess runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.SPONGEFORGE_1_10_2) || version.equals(Version.SPONGEFORGE_1_11_2) || version.equals(Version.SPONGEFORGE_1_12_2);
    }

    private static void rewriteSpongeConfig(RunningProcess runningProcess) {
        File config = Paths.get(runningProcess.getPath() + "/config/sponge/global.conf").toFile();
        rewriteFile(config, s -> {
            if (s.startsWith("ip-forwarding=")) {
                s = "ip-forwarding=true";
            } else if (s.startsWith("bungeecord=")) {
                s = "bungeecord=true";
            }

            return s;
        });
    }

    // ========================= //
    //Bungee
    private static boolean isLogicallyBungee(RunningProcess runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.WATERFALL) || version.equals(Version.TRAVERTINE) || version.equals(Version.HEXACORD) || version.equals(Version.BUNGEECORD);
    }

    private static void rewriteBungeeConfig(RunningProcess runningProcess) {
        File file = Paths.get(runningProcess.getPath() + "/config.yml").toFile();
        rewriteFile(file, s -> {
            if (s.startsWith("  host:")) {
                s = "  host: " + runningProcess.getProcessInformation().getNetworkInfo().getHost() + ":" + runningProcess.getProcessInformation().getNetworkInfo().getPort();
            } else if (s.startsWith("ip_forward:")) {
                s = "ip_forward: true";
            } else if (s.startsWith("- query_port: ")) {
                s = "- query_port: " + runningProcess.getProcessInformation().getNetworkInfo().getPort();
            }

            return s;
        });
    }

    // ========================= //
    //Waterdog

    private static boolean isLogicallyWaterDog(RunningProcess runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.WATERDOG) || version.equals(Version.WATERDOG_PE);
    }

    private static void rewriteWaterDogConfig(RunningProcess runningProcess) {
        File file = Paths.get(runningProcess.getPath() + "/config.yml").toFile();
        rewriteFile(file, s -> {
            if (s.startsWith("  host:")) {
                s = "  host: " + runningProcess.getProcessInformation().getNetworkInfo().getHost() + ":" + runningProcess.getProcessInformation().getNetworkInfo().getPort();
            } else if (s.startsWith("ip_forward:")) {
                s = "ip_forward: true";
            } else if (s.startsWith("use_xuid_for_uuid:")) {
                s = "use_xuid_for_uuid: true";
            } else if (s.startsWith("  raknet:")) {
                s = "  raknet: " + runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG_PE);
            } else if (s.startsWith("- query_port:")) {
                s = "- query_port: " + runningProcess.getProcessInformation().getNetworkInfo().getPort();
            }

            return s;
        });
    }

    // ========================= //
    //Velocity
    private static void rewriteVelocityConfig(RunningProcess runningProcess) {
        File file = Paths.get(runningProcess.getPath() + "/velocity.toml").toFile();
        rewriteFile(file, s -> {
            if (s.startsWith("bind")) {
                s = "bind = \"" + runningProcess.getProcessInformation().getNetworkInfo().getHost() + ":" + runningProcess.getProcessInformation().getNetworkInfo().getPort() + "\"";
            } else if (s.startsWith("show-max-players") && runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
                s = "show-max-players = " + runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers();
            } else if (s.startsWith("player-info-forwarding-mode")) {
                s = "player-info-forwarding-mode = \"LEGACY\"";
            }

            return s;
        });
    }

    // ========================= //
    //Glowstone
    private static boolean isLogicallyGlowstone(RunningProcess runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.GLOWSTONE_1_10_2) || version.equals(Version.GLOWSTONE_1_12_2);
    }

    private static void rewriteGlowstoneConfig(RunningProcess runningProcess) {
        rewriteFile(new File(runningProcess.getPath() + "/config/glowstone.yml"), s -> {
            if (s.trim().startsWith("ip:")) {
                s = "  ip: '" + runningProcess.getProcessInformation().getNetworkInfo().getHost() + "'";
            }

            if (s.trim().startsWith("port:")) {
                s = "  port: " + runningProcess.getProcessInformation().getNetworkInfo().getPort();
            }

            if (s.trim().startsWith("online-mode:")) {
                s = "  online-mode: false";
            }

            if (s.trim().startsWith("advanced.proxy-support:")) {
                s = "  online-mode: false";
            }

            return s;
        });
    }

    // ========================= //
    //Spigot
    private static void rewriteSpigotConfig(RunningProcess runningProcess) {
        rewriteFile(new File(runningProcess.getPath() + "/spigot.yml"), s -> {
            if (s.trim().startsWith("bungeecord:")) {
                s = "  bungeecord: true";
            }

            return s;
        });
    }

    // ========================= //
    //Startup

    private static void chooseLogicallyStartup(RunningProcess runningProcess) {
        if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().isServer()) {
            serverStartup(runningProcess);
        } else {
            proxyStartup(runningProcess);
        }
    }

    private static void serverStartup(RunningProcess runningProcess) {
        createEula(runningProcess);

        if (isLogicallySpongeForge(runningProcess)) {
            Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
            String fileName = "reformcloud/files/" + version.getName().toLowerCase().replace(" ", "-") + ".zip";
            String destPath = "reformcloud/files/" + version.getName().toLowerCase().replace(" ", "-");

            if (!Files.exists(Paths.get(destPath))) {
                DownloadHelper.downloadAndDisconnect(version.getUrl(), fileName);

                SystemHelper.unZip(Paths.get(fileName).toFile(), destPath);
                SystemHelper.rename(Paths.get(destPath + "/sponge.jar").toFile(), destPath + "/process.jar");
                SystemHelper.deleteFile(new File(fileName));
            }

            SystemHelper.copyDirectory(Paths.get(destPath + "/mods"), Paths.get(runningProcess.getPath() + "/mods"));
        }

        if (isLogicallyGlowstone(runningProcess)) {
            SystemHelper.createDirectory(Paths.get(runningProcess.getPath() + "/config"));
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/glowstone/glowstone.yml", runningProcess.getPath() + "/config/glowstone.yml");
            rewriteGlowstoneConfig(runningProcess);
        } else if (isLogicallySpongeVanilla(runningProcess)) {
            SystemHelper.createDirectory(Paths.get(runningProcess.getPath() + "/config/sponge"));
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/sponge/vanilla/global.conf", runningProcess.getPath() + "/config/sponge/global.conf");
            rewriteSpongeConfig(runningProcess);
        } else if (isLogicallySpongeForge(runningProcess)) {
            SystemHelper.createDirectory(Paths.get(runningProcess.getPath() + "/config/sponge"));
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/sponge/forge/global.conf", runningProcess.getPath() + "/config/sponge/global.conf");
            rewriteSpongeConfig(runningProcess);
        } else if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/nukkit/server.properties", runningProcess.getPath() + "/server.properties");
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/nukkit/nukkit.yml", runningProcess.getPath() + "/nukkit.yml");
            Properties properties = new Properties();
            try (InputStream inputStream = Files.newInputStream(Paths.get(runningProcess.getPath() + "/server.properties"))) {
                properties.load(inputStream);
                properties.setProperty("server-ip", runningProcess.getProcessInformation().getNetworkInfo().getHost());
                properties.setProperty("server-port", Integer.toString(runningProcess.getProcessInformation().getNetworkInfo().getPort()));
                properties.setProperty("xbox-auth", Boolean.toString(false));

                try (OutputStream outputStream = Files.newOutputStream(Paths.get(runningProcess.getPath() + "/server.properties"))) {
                    properties.store(outputStream, "ReformCloud2 node edit");
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        } else {
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bukkit/spigot.yml", runningProcess.getPath() + "/spigot.yml");
            rewriteSpigotConfig(runningProcess);
        }

        if (!runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            Properties properties = new Properties();
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bukkit/server.properties", runningProcess.getPath() + "/server.properties");
            try (InputStream inputStream = Files.newInputStream(Paths.get(runningProcess.getPath() + "/server.properties"))) {
                properties.load(inputStream);
                properties.setProperty("server-ip", runningProcess.getProcessInformation().getNetworkInfo().getHost());
                properties.setProperty("server-port", Integer.toString(runningProcess.getProcessInformation().getNetworkInfo().getPort()));
                properties.setProperty("online-mode", Boolean.toString(false));

                try (OutputStream outputStream = Files.newOutputStream(Paths.get(runningProcess.getPath() + "/server.properties"))) {
                    properties.store(outputStream, "ReformCloud2 node edit");
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        if (!isLogicallySpongeForge(runningProcess) && !Files.exists(Paths.get(runningProcess.getPath() + "/process.jar"))) {
            Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
            if (!Files.exists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }
        }
    }

    private static void proxyStartup(RunningProcess runningProcess) {
        if (!Files.exists(Paths.get(runningProcess.getPath() + "/server-icon.png"))) {
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/server-icon.png", runningProcess.getPath() + "/server-icon.png");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(Paths.get(runningProcess.getPath() + "/server-icon.png").toFile());
            if (bufferedImage.getHeight() != 64 || bufferedImage.getWidth() != 64) {
                System.err.println("The server icon of the process " + runningProcess.getProcessInformation().getProcessDetail().getName() + " is not correctly sized");
                SystemHelper.rename(Paths.get(runningProcess.getPath() + "/server-icon.png").toFile(), runningProcess.getPath() + "/server-icon-old.png");
                SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/server-icon.png", runningProcess.getPath() + "/server-icon.png");
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        if (isLogicallyBungee(runningProcess)) {
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bungee/internal-bungeecord-config.yml", runningProcess.getPath() + "/config.yml");
            rewriteBungeeConfig(runningProcess);
        } else if (isLogicallyWaterDog(runningProcess)) {
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/waterdog/internal-waterdog-config.yml", runningProcess.getPath() + "/config.yml");
            rewriteWaterDogConfig(runningProcess);
        } else if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.VELOCITY)) {
            SystemHelper.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/velocity/velocity.toml", runningProcess.getPath() + "/velocity.toml");
            rewriteVelocityConfig(runningProcess);
        }

        if (!Files.exists(Paths.get(runningProcess.getPath() + "/process.jar"))) {
            Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
            if (!Files.exists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }
        }
    }

    private static void rewriteFile(File file, UnaryOperator<String> operator) {
        try {
            List<String> list = Files.readAllLines(file.toPath());
            List<String> newLine = new ArrayList<>();

            list.forEach(s -> newLine.add(operator.apply(s)));

            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8), true)) {
                newLine.forEach(s -> {
                    printWriter.write(s + "\n");
                    printWriter.flush();
                });
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
