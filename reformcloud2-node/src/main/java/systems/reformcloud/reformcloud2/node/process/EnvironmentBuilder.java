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
package systems.reformcloud.reformcloud2.node.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.version.Version;
import systems.reformcloud.reformcloud2.node.template.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.io.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.process.NetworkInfo;
import systems.reformcloud.reformcloud2.shared.network.SimpleNetworkAddress;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.shared.Constants;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;
import systems.reformcloud.reformcloud2.shared.network.NetworkUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

final class EnvironmentBuilder {

    private EnvironmentBuilder() {
        throw new AssertionError("You should not instantiate the class");
    }

    /**
     * Constructs the env for the specified process
     *
     * @param runningProcess The process for which we are building the env
     * @param firstStart     If the process is prepared the first time
     * @param connectionKey  The connection key generated for the process
     */
    static void constructEnvFor(@NotNull DefaultNodeLocalProcessWrapper runningProcess, boolean firstStart, @NotNull String connectionKey) {
        NetworkInfo networkInfo = runningProcess.getProcessInformation().getNetworkInfo();
        networkInfo.setPort(NetworkUtils.checkAndReplacePortIfInUse(networkInfo.getPort()));

        if (!runningProcess.getProcessInformation().getProcessGroup().isStaticProcess() || firstStart) {
            loadTemplateInclusions(runningProcess, Inclusion.InclusionLoadType.PRE);
            loadPathInclusions(runningProcess, Inclusion.InclusionLoadType.PRE);
            initGlobalTemplateAndCurrentTemplate(runningProcess);
        }

        ProcessUtil.loadInclusions(runningProcess.getPath(), runningProcess.getProcessInformation().getPreInclusions());
        if (!runningProcess.getProcessInformation().getProcessGroup().isStaticProcess() || firstStart) {
            loadTemplateInclusions(runningProcess, Inclusion.InclusionLoadType.PAST);
            loadPathInclusions(runningProcess, Inclusion.InclusionLoadType.PAST);
        }

        if (Files.notExists(Paths.get("reformcloud/files/runner.jar"))) {
            DownloadHelper.downloadAndDisconnect(Constants.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
        }

        IOUtils.createDirectory(Paths.get(runningProcess.getPath() + "/plugins"));
        IOUtils.doCopy("reformcloud/files/runner.jar", runningProcess.getPath().resolve("runner.jar"));
        IOUtils.doOverrideInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/embedded.jar", runningProcess.getPath() + "/plugins/executor.jar");

        SimpleNetworkAddress connectHost = NodeExecutor.getInstance().getAnyAddress();
        new JsonConfiguration()
            .add("host", connectHost.getHost())
            .add("port", connectHost.getPort())
            .add("key", connectionKey)
            .add("startInfo", runningProcess.getProcessInformation())
            .write(runningProcess.getPath() + "/.reformcloud/config.json");

        if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().isServer()) {
            serverStartup(runningProcess);
        } else {
            proxyStartup(runningProcess);
        }
    }

    private static void serverStartup(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        createEula(runningProcess);

        if (isLogicallySpongeForge(runningProcess)) {
            Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
            String fileName = "reformcloud/files/" + version.getName().toLowerCase().replace(" ", "-") + ".zip";
            String destPath = "reformcloud/files/" + version.getName().toLowerCase().replace(" ", "-");

            Path targetDestination = Paths.get(destPath);
            if (Files.notExists(targetDestination)) {
                DownloadHelper.downloadAndDisconnect(version.getUrl(), fileName);

                IOUtils.unZip(Paths.get(fileName), targetDestination);
                IOUtils.rename(targetDestination.resolve("sponge.jar"), destPath + "/process.jar");
                IOUtils.deleteFile(fileName);
            }

            IOUtils.copyDirectory(targetDestination.resolve("mods"), runningProcess.getPath().resolve("mods"));
        }

        if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/nukkit/server.properties", runningProcess.getPath() + "/server.properties");
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/nukkit/nukkit.yml", runningProcess.getPath() + "/nukkit.yml");
        } else if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.CLOUDBURST)) {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/cloudburst/server.properties", runningProcess.getPath() + "/server.properties");
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/cloudburst/cloudburst.yml", runningProcess.getPath() + "/cloudburst.yml");
        }

        if (isLogicallyGlowstone(runningProcess)) {
            IOUtils.createDirectory(Paths.get(runningProcess.getPath() + "/config"));
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/glowstone/glowstone.yml", runningProcess.getPath() + "/config/glowstone.yml");
            rewriteGlowstoneConfig(runningProcess);
        } else if (isLogicallySpongeVanilla(runningProcess)) {
            IOUtils.createDirectory(Paths.get(runningProcess.getPath() + "/config/sponge"));
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/sponge/vanilla/global.conf", runningProcess.getPath() + "/config/sponge/global.conf");
            rewriteSpongeConfig(runningProcess);
        } else if (isLogicallySpongeForge(runningProcess)) {
            IOUtils.createDirectory(Paths.get(runningProcess.getPath() + "/config/sponge"));
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/sponge/forge/global.conf", runningProcess.getPath() + "/config/sponge/global.conf");
            rewriteSpongeConfig(runningProcess);
        } else if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.NUKKIT_X)
            || runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.CLOUDBURST)) {
            Properties properties = new Properties();
            try (InputStream inputStream = Files.newInputStream(Paths.get(runningProcess.getPath() + "/server.properties"))) {
                properties.load(inputStream);
                properties.setProperty("server-ip", runningProcess.getProcessInformation().getNetworkInfo().getHostPlain());
                properties.setProperty("server-port", Integer.toString(runningProcess.getProcessInformation().getNetworkInfo().getPort()));
                properties.setProperty("xbox-auth", Boolean.toString(false));

                if (runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers() >= 0) {
                    properties.setProperty("max-players", Integer.toString(runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers()));
                }

                try (OutputStream outputStream = Files.newOutputStream(Paths.get(runningProcess.getPath() + "/server.properties"))) {
                    properties.store(outputStream, "ReformCloud2 node edit");
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        } else {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bukkit/spigot.yml", runningProcess.getPath() + "/spigot.yml");
            rewriteSpigotConfig(runningProcess);
        }

        if (!runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            Properties properties = new Properties();
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bukkit/server.properties", runningProcess.getPath() + "/server.properties");
            try (InputStream inputStream = Files.newInputStream(Paths.get(runningProcess.getPath() + "/server.properties"))) {
                properties.load(inputStream);
                properties.setProperty("server-ip", runningProcess.getProcessInformation().getNetworkInfo().getHostPlain());
                properties.setProperty("server-port", Integer.toString(runningProcess.getProcessInformation().getNetworkInfo().getPort()));
                properties.setProperty("online-mode", Boolean.toString(false));

                if (runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers() >= 0) {
                    properties.setProperty("max-players", Integer.toString(runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers()));
                }

                try (OutputStream outputStream = Files.newOutputStream(Paths.get(runningProcess.getPath() + "/server.properties"))) {
                    properties.store(outputStream, "ReformCloud2 node edit");
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        if (!isLogicallySpongeForge(runningProcess) && Files.notExists(Paths.get(runningProcess.getPath() + "/process.jar"))) {
            Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
            if (Files.notExists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }
        }
    }

    private static void proxyStartup(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        if (Files.notExists(Paths.get(runningProcess.getPath() + "/server-icon.png"))) {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/server-icon.png", runningProcess.getPath() + "/server-icon.png");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(Paths.get(runningProcess.getPath() + "/server-icon.png").toFile());
            if (bufferedImage.getHeight() != 64 || bufferedImage.getWidth() != 64) {
                System.err.println("The server icon of the process " + runningProcess.getProcessInformation().getProcessDetail().getName() + " is not correctly sized");
                IOUtils.rename(Paths.get(runningProcess.getPath() + "/server-icon.png"), runningProcess.getPath() + "/server-icon-old.png");
                IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/server-icon.png", runningProcess.getPath() + "/server-icon.png");
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        if (isLogicallyBungee(runningProcess)) {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bungee/internal-bungeecord-config.yml", runningProcess.getPath() + "/config.yml");
            rewriteBungeeConfig(runningProcess);
        } else if (isLogicallyWaterDog(runningProcess)) {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/waterdog/internal-waterdog-config.yml", runningProcess.getPath() + "/config.yml");
            rewriteWaterDogConfig(runningProcess);
        } else if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.VELOCITY)) {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/velocity/velocity.toml", runningProcess.getPath() + "/velocity.toml");
            rewriteVelocityConfig(runningProcess);
        }

        if (Files.notExists(Paths.get(runningProcess.getPath() + "/process.jar"))) {
            Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
            if (Files.notExists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }
        }
    }

    private static void createEula(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        try (InputStream inputStream = EnvironmentBuilder.class.getClassLoader().getResourceAsStream("files/java/bukkit/eula.txt")) {
            Files.copy(Objects.requireNonNull(inputStream), Paths.get(runningProcess.getPath() + "/eula.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    //Sponge
    private static boolean isLogicallySpongeVanilla(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.SPONGEVANILLA_1_11_2) || version.equals(Version.SPONGEVANILLA_1_12_2);
    }

    private static boolean isLogicallySpongeForge(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.SPONGEFORGE_1_10_2) || version.equals(Version.SPONGEFORGE_1_11_2) || version.equals(Version.SPONGEFORGE_1_12_2);
    }

    private static void rewriteSpongeConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        rewriteFile(runningProcess.getPath().resolve("config/sponge/global.conf"), s -> {
            if (s.trim().startsWith("ip-forwarding=")) {
                s = "ip-forwarding=true";
            } else if (s.trim().startsWith("bungeecord=")) {
                s = "bungeecord=true";
            }

            return s;
        });
    }

    //Bungee
    private static boolean isLogicallyBungee(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.WATERFALL) || version.equals(Version.TRAVERTINE) || version.equals(Version.HEXACORD) || version.equals(Version.BUNGEECORD);
    }

    private static void rewriteBungeeConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        rewriteFile(runningProcess.getPath().resolve("config.yml"), s -> {
            if (s.trim().startsWith("host:")) {
                s = "    host: '" + formatHost(runningProcess) + "'";
            } else if (s.trim().startsWith("ip_forward:")) {
                s = "ip_forward: true";
            } else if (s.trim().startsWith("- query_port:")) {
                s = "  - query_port: " + runningProcess.getProcessInformation().getNetworkInfo().getPort();
            } else if (s.trim().startsWith("max_players:") && runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers() >= 0) {
                s = "    max_players: " + runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers();
            }

            return s;
        });
    }

    //Waterdog
    private static boolean isLogicallyWaterDog(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.WATERDOG) || version.equals(Version.WATERDOG_PE);
    }

    private static void rewriteWaterDogConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        rewriteFile(runningProcess.getPath().resolve("config.yml"), s -> {
            if (s.trim().startsWith("host:")) {
                s = "    host: '" + formatHost(runningProcess) + "'";
            } else if (s.trim().startsWith("ip_forward:")) {
                s = "ip_forward: true";
            } else if (s.trim().startsWith("use_xuid_for_uuid:")) {
                s = "use_xuid_for_uuid: true";
            } else if (s.trim().startsWith("raknet:")) {
                s = "    raknet: " + runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG_PE);
            } else if (s.trim().startsWith("- query_port:")) {
                s = "  - query_port: " + runningProcess.getProcessInformation().getNetworkInfo().getPort();
            } else if (s.trim().startsWith("max_players:") && runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers() >= 0) {
                s = "    max_players: " + runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers();
            }

            return s;
        });
    }

    //Velocity
    private static void rewriteVelocityConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        rewriteFile(runningProcess.getPath().resolve("velocity.toml"), s -> {
            if (s.trim().startsWith("bind")) {
                s = "bind = \"" + formatHost(runningProcess) + "\"";
            } else if (s.trim().startsWith("show-max-players") && runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers() >= 0) {
                s = "show-max-players = " + runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers();
            } else if (s.trim().startsWith("player-info-forwarding-mode")) {
                s = "player-info-forwarding-mode = \"LEGACY\"";
            }

            return s;
        });
    }

    //Glowstone
    private static boolean isLogicallyGlowstone(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.GLOWSTONE_1_10_2) || version.equals(Version.GLOWSTONE_1_12_2);
    }

    private static void rewriteGlowstoneConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        rewriteFile(runningProcess.getPath().resolve("config/glowstone.yml"), s -> {
            if (s.trim().startsWith("ip:")) {
                s = "  ip: '" + runningProcess.getProcessInformation().getNetworkInfo().getHostPlain() + "'";
            } else if (s.trim().startsWith("port:")) {
                s = "  port: " + runningProcess.getProcessInformation().getNetworkInfo().getPort();
            } else if (s.trim().startsWith("online-mode:")) {
                s = "  online-mode: false";
            } else if (s.trim().startsWith("proxy-support:")) {
                s = "  proxy-support: true";
            } else if (s.trim().startsWith("max-players:") && runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers() >= 0) {
                s = "  max-players: " + runningProcess.getProcessInformation().getProcessDetail().getMaxPlayers();
            }

            return s;
        });
    }

    //Spigot
    private static void rewriteSpigotConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
        rewriteFile(runningProcess.getPath().resolve("spigot.yml"), s -> {
            if (s.trim().startsWith("bungeecord:")) {
                s = "  bungeecord: true";
            }

            return s;
        });
    }

    private static void rewriteFile(@NotNull Path path, UnaryOperator<String> operator) {
        try {
            List<String> lines = Files.readAllLines(path).stream().map(operator::apply).collect(Collectors.toList());
            if (!lines.isEmpty()) {
                Files.write(path, lines);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void loadTemplateInclusions(@NotNull DefaultNodeLocalProcessWrapper processInformation, @NotNull Inclusion.InclusionLoadType loadType) {
        processInformation.getProcessInformation().getProcessDetail().getTemplate().getTemplateInclusionsOfType(loadType).forEach(e -> {
            String[] splitTemplate = e.getFirst().split("/");
            if (splitTemplate.length != 2) {
                return;
            }

            TemplateBackendManager.getOrDefault(e.getSecond()).loadTemplate(
                splitTemplate[0],
                splitTemplate[1],
                processInformation.getPath()
            ).awaitUninterruptedly();
        });
    }

    private static void loadPathInclusions(@NotNull DefaultNodeLocalProcessWrapper processInformation, @NotNull Inclusion.InclusionLoadType loadType) {
        processInformation.getProcessInformation().getProcessDetail().getTemplate().getPathInclusionsOfType(loadType).forEach(e -> {
            TemplateBackendManager.getOrDefault(e.getSecond()).loadPath(
                e.getFirst(),
                processInformation.getPath()
            ).awaitUninterruptedly();
        });
    }

    private static void initGlobalTemplateAndCurrentTemplate(@NotNull DefaultNodeLocalProcessWrapper processInformation) {
        TemplateBackendManager.getOrDefault(processInformation.getProcessInformation().getProcessDetail().getTemplate().getBackend()).loadGlobalTemplates(
            processInformation.getProcessInformation().getProcessGroup(),
            processInformation.getPath()
        ).awaitUninterruptedly();

        TemplateBackendManager.getOrDefault(processInformation.getProcessInformation().getProcessDetail().getTemplate().getBackend()).loadTemplate(
            processInformation.getProcessInformation().getProcessGroup().getName(),
            processInformation.getProcessInformation().getProcessDetail().getTemplate().getName(),
            processInformation.getPath()
        ).awaitUninterruptedly();
    }

    private static String formatHost(DefaultNodeLocalProcessWrapper wrapper) {
        NetworkInfo networkInfo = wrapper.getProcessInformation().getNetworkInfo();
        return String.format(networkInfo.getHost() instanceof Inet6Address ? "[%s]:%d" : "%s:%d", networkInfo.getHostPlain(), networkInfo.getPort());
    }
}
