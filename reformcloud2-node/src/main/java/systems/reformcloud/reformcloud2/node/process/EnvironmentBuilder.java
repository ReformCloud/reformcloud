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
package systems.reformcloud.reformcloud2.node.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.io.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.process.NetworkInfo;
import systems.reformcloud.reformcloud2.executor.api.utility.NetworkAddress;
import systems.reformcloud.reformcloud2.executor.api.utility.PortUtil;
import systems.reformcloud.reformcloud2.executor.api.utility.StringUtil;
import systems.reformcloud.reformcloud2.node.NodeExecutor;

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
        throw new AssertionError("You should not instantiate the class");
    }

    /**
     * Constructs the env for the specified process
     *
     * @param runningProcess The process for which we are building the env
     */
    static void constructEnvFor(@NotNull DefaultNodeProcessWrapper runningProcess, boolean firstStart) {
        NetworkInfo networkInfo = runningProcess.getProcessInformation().getNetworkInfo();
        networkInfo.setPort(PortUtil.checkPort(networkInfo.getPort()));

        if (!runningProcess.getProcessInformation().getProcessGroup().isStaticProcess() || firstStart) {
            loadTemplateInclusions(runningProcess, Inclusion.InclusionLoadType.PRE);
            loadPathInclusions(runningProcess, Inclusion.InclusionLoadType.PRE);
            initGlobalTemplateAndCurrentTemplate(runningProcess);
        }

        InclusionLoader.loadInclusions(runningProcess.getPath(), runningProcess.getProcessInformation().getPreInclusions());
        if (!runningProcess.getProcessInformation().getProcessGroup().isStaticProcess() || firstStart) {
            loadTemplateInclusions(runningProcess, Inclusion.InclusionLoadType.PAST);
            loadPathInclusions(runningProcess, Inclusion.InclusionLoadType.PAST);
        }

        if (!Files.exists(Paths.get("reformcloud/files/runner.jar"))) {
            DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
        }

        IOUtils.createDirectory(Paths.get(runningProcess.getPath() + "/plugins"));
        IOUtils.createDirectory(Paths.get(runningProcess.getPath() + "/reformcloud/.connection"));
        IOUtils.doCopy("reformcloud/files/runner.jar", runningProcess.getPath() + "/runner.jar");
        IOUtils.doCopy("reformcloud/.bin/executor.jar", runningProcess.getPath() + "/plugins/executor.jar");

        new JsonConfiguration()
                .add("key", runningProcess.getConnectionKey())
                .write(runningProcess.getPath() + "/reformcloud/.connection/key.json");

        NetworkAddress connectHost = NodeExecutor.getInstance().getAnyAddress();
        new JsonConfiguration()
                .add("host", connectHost.getHost())
                .add("port", connectHost.getPort())
                .add("startInfo", runningProcess.getProcessInformation())
                .write(runningProcess.getPath() + "/reformcloud/.connection/connection.json");

        if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().isServer()) {
            serverStartup(runningProcess);
        } else {
            proxyStartup(runningProcess);
        }
    }

    private static void serverStartup(@NotNull DefaultNodeProcessWrapper runningProcess) {
        createEula(runningProcess);

        if (isLogicallySpongeForge(runningProcess)) {
            Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
            String fileName = "reformcloud/files/" + version.getName().toLowerCase().replace(" ", "-") + ".zip";
            String destPath = "reformcloud/files/" + version.getName().toLowerCase().replace(" ", "-");

            if (!Files.exists(Paths.get(destPath))) {
                DownloadHelper.downloadAndDisconnect(version.getUrl(), fileName);

                IOUtils.unZip(Paths.get(fileName).toFile(), destPath);
                IOUtils.rename(Paths.get(destPath + "/sponge.jar").toFile(), destPath + "/process.jar");
                IOUtils.deleteFile(new File(fileName));
            }

            IOUtils.copyDirectory(Paths.get(destPath + "/mods"), Paths.get(runningProcess.getPath() + "/mods"));
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
        } else if (runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/nukkit/server.properties", runningProcess.getPath() + "/server.properties");
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/nukkit/nukkit.yml", runningProcess.getPath() + "/nukkit.yml");
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
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bukkit/spigot.yml", runningProcess.getPath() + "/spigot.yml");
            rewriteSpigotConfig(runningProcess);
        }

        if (!runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            Properties properties = new Properties();
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bukkit/server.properties", runningProcess.getPath() + "/server.properties");
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

    private static void proxyStartup(@NotNull DefaultNodeProcessWrapper runningProcess) {
        if (!Files.exists(Paths.get(runningProcess.getPath() + "/server-icon.png"))) {
            IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/server-icon.png", runningProcess.getPath() + "/server-icon.png");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(Paths.get(runningProcess.getPath() + "/server-icon.png").toFile());
            if (bufferedImage.getHeight() != 64 || bufferedImage.getWidth() != 64) {
                System.err.println("The server icon of the process " + runningProcess.getProcessInformation().getProcessDetail().getName() + " is not correctly sized");
                IOUtils.rename(Paths.get(runningProcess.getPath() + "/server-icon.png").toFile(), runningProcess.getPath() + "/server-icon-old.png");
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

        if (!Files.exists(Paths.get(runningProcess.getPath() + "/process.jar"))) {
            Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
            if (!Files.exists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }
        }
    }

    private static void createEula(@NotNull DefaultNodeProcessWrapper runningProcess) {
        try (InputStream inputStream = EnvironmentBuilder.class.getClassLoader().getResourceAsStream("files/java/bukkit/eula.txt")) {
            Files.copy(Objects.requireNonNull(inputStream), Paths.get(runningProcess.getPath() + "/eula.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    //Sponge
    private static boolean isLogicallySpongeVanilla(@NotNull DefaultNodeProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.SPONGEVANILLA_1_11_2) || version.equals(Version.SPONGEVANILLA_1_12_2);
    }

    private static boolean isLogicallySpongeForge(@NotNull DefaultNodeProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.SPONGEFORGE_1_10_2) || version.equals(Version.SPONGEFORGE_1_11_2) || version.equals(Version.SPONGEFORGE_1_12_2);
    }

    private static void rewriteSpongeConfig(@NotNull DefaultNodeProcessWrapper runningProcess) {
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

    //Bungee
    private static boolean isLogicallyBungee(@NotNull DefaultNodeProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.WATERFALL) || version.equals(Version.TRAVERTINE) || version.equals(Version.HEXACORD) || version.equals(Version.BUNGEECORD);
    }

    private static void rewriteBungeeConfig(@NotNull DefaultNodeProcessWrapper runningProcess) {
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

    //Waterdog
    private static boolean isLogicallyWaterDog(@NotNull DefaultNodeProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.WATERDOG) || version.equals(Version.WATERDOG_PE);
    }

    private static void rewriteWaterDogConfig(@NotNull DefaultNodeProcessWrapper runningProcess) {
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

    //Velocity
    private static void rewriteVelocityConfig(@NotNull DefaultNodeProcessWrapper runningProcess) {
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

    //Glowstone
    private static boolean isLogicallyGlowstone(@NotNull DefaultNodeProcessWrapper runningProcess) {
        Version version = runningProcess.getProcessInformation().getProcessDetail().getTemplate().getVersion();
        return version.equals(Version.GLOWSTONE_1_10_2) || version.equals(Version.GLOWSTONE_1_12_2);
    }

    private static void rewriteGlowstoneConfig(@NotNull DefaultNodeProcessWrapper runningProcess) {
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

    //Spigot
    private static void rewriteSpigotConfig(@NotNull DefaultNodeProcessWrapper runningProcess) {
        rewriteFile(new File(runningProcess.getPath() + "/spigot.yml"), s -> {
            if (s.trim().startsWith("bungeecord:")) {
                s = "  bungeecord: true";
            }

            return s;
        });
    }

    private static void rewriteFile(@NotNull File file, UnaryOperator<String> operator) {
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

    private static void loadTemplateInclusions(@NotNull DefaultNodeProcessWrapper processInformation, @NotNull Inclusion.InclusionLoadType loadType) {
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

    private static void loadPathInclusions(@NotNull DefaultNodeProcessWrapper processInformation, @NotNull Inclusion.InclusionLoadType loadType) {
        processInformation.getProcessInformation().getProcessDetail().getTemplate().getPathInclusionsOfType(loadType).forEach(e -> {
            TemplateBackendManager.getOrDefault(e.getSecond()).loadPath(
                    e.getFirst(),
                    processInformation.getPath()
            ).awaitUninterruptedly();
        });
    }

    private static void initGlobalTemplateAndCurrentTemplate(@NotNull DefaultNodeProcessWrapper processInformation) {
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
}
