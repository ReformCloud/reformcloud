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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Version;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.VersionInstaller;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Versions;
import systems.reformcloud.reformcloud2.executor.api.network.address.NetworkAddress;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.template.TemplateBackendManager;
import systems.reformcloud.reformcloud2.node.template.VersionInstallerRegistry;
import systems.reformcloud.reformcloud2.shared.Constants;
import systems.reformcloud.reformcloud2.shared.io.DownloadHelper;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;
import systems.reformcloud.reformcloud2.shared.network.NetworkUtils;
import systems.reformcloud.reformcloud2.shared.network.data.DefaultProtocolBuffer;

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
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

final class EnvironmentBuilder {

  private EnvironmentBuilder() {
    throw new UnsupportedOperationException();
  }

  /**
   * Constructs the env for the specified process
   *
   * @param runningProcess The process for which we are building the env
   * @param firstStart     If the process is prepared the first time
   * @param connectionKey  The connection key generated for the process
   */
  static void constructEnvFor(@NotNull DefaultNodeLocalProcessWrapper runningProcess, boolean firstStart, @NotNull String connectionKey) {
    NetworkAddress address = runningProcess.getProcessInformation().getHost();
    address.setPort(NetworkUtils.checkAndReplacePortIfInUse(address.getPort()));

    if (!runningProcess.getProcessInformation().getProcessGroup().createsStaticProcesses() || firstStart) {
      loadTemplateInclusions(runningProcess, Inclusion.InclusionLoadType.PRE);
      loadPathInclusions(runningProcess, Inclusion.InclusionLoadType.PRE);
      initGlobalTemplateAndCurrentTemplate(runningProcess);
    }

    ProcessUtil.loadInclusions(runningProcess.getPath(), runningProcess.getProcessInformation().getProcessInclusions());
    if (!runningProcess.getProcessInformation().getProcessGroup().createsStaticProcesses() || firstStart) {
      loadTemplateInclusions(runningProcess, Inclusion.InclusionLoadType.PAST);
      loadPathInclusions(runningProcess, Inclusion.InclusionLoadType.PAST);
    }

    if (Files.notExists(Paths.get("reformcloud/files/runner.jar"))) {
      DownloadHelper.download(Constants.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
    }

    IOUtils.createDirectory(Paths.get(runningProcess.getPath() + "/plugins"));
    IOUtils.doCopy("reformcloud/files/runner.jar", runningProcess.getPath().resolve("runner.jar"));
    IOUtils.doOverrideInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/embedded.jar", runningProcess.getPath() + "/plugins/executor.jar");

    final NetworkAddress connectHost = NodeExecutor.getInstance().getAnyAddress();
    JsonConfiguration.newJsonConfiguration()
      .add("host", connectHost.getHost())
      .add("port", connectHost.getPort())
      .add("key", connectionKey)
      .write(runningProcess.getPath() + "/.reformcloud/config.json");
    writeProcessInformation(runningProcess);

    if (runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().getVersionType().isServer()) {
      serverStartup(runningProcess);
    } else {
      proxyStartup(runningProcess);
    }
  }

  private static void serverStartup(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
    createEula(runningProcess);

    if (!installVersion(runningProcess)) {
      System.err.println("Unable to install version " + runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().getName());
      return;
    }

    if (isLogicallySpongeForge(runningProcess)) {
      final Path versionDirectory = Paths.get("reformcloud/files", runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().getName().toLowerCase(Locale.ROOT));
      IOUtils.copyDirectory(versionDirectory.resolve("mods"), runningProcess.getPath().resolve("mods"));
    }

    if (runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().equals(Versions.NUKKIT_X)) {
      IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/nukkit/server.properties", runningProcess.getPath() + "/server.properties");
      IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/mcpe/nukkit/nukkit.yml", runningProcess.getPath() + "/nukkit.yml");
    } else if (runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().equals(Versions.CLOUDBURST)) {
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
    } else if (runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().equals(Versions.NUKKIT_X)
      || runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().equals(Versions.CLOUDBURST)) {
      rewriteServerProperties(runningProcess);
    } else {
      IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bukkit/spigot.yml", runningProcess.getPath() + "/spigot.yml");
      rewriteSpigotConfig(runningProcess);
    }

    if (!runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().equals(Versions.NUKKIT_X)) {
      IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/bukkit/server.properties", runningProcess.getPath() + "/server.properties");
      rewriteServerProperties(runningProcess);
    }
  }

  private static void proxyStartup(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
    if (!installVersion(runningProcess)) {
      System.err.println("Unable to install version " + runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().getName());
      return;
    }

    if (Files.notExists(Paths.get(runningProcess.getPath() + "/server-icon.png"))) {
      IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/server-icon.png", runningProcess.getPath() + "/server-icon.png");
    }

    try {
      BufferedImage bufferedImage = ImageIO.read(Paths.get(runningProcess.getPath() + "/server-icon.png").toFile());
      if (bufferedImage.getHeight() != 64 || bufferedImage.getWidth() != 64) {
        System.err.println("The server icon of the process " + runningProcess.getProcessInformation().getName() + " is not correctly sized");
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
    } else if (runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().equals(Versions.VELOCITY)) {
      IOUtils.doInternalCopy(EnvironmentBuilder.class.getClassLoader(), "files/java/velocity/velocity.toml", runningProcess.getPath() + "/velocity.toml");
      rewriteVelocityConfig(runningProcess);
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
    Version version = runningProcess.getProcessInformation().getPrimaryTemplate().getVersion();
    return version.getName().toLowerCase(Locale.ROOT).startsWith("spongevanilla");
  }

  private static boolean isLogicallySpongeForge(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
    Version version = runningProcess.getProcessInformation().getPrimaryTemplate().getVersion();
    return version.getName().toLowerCase(Locale.ROOT).startsWith("spongeforge");
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
    Version version = runningProcess.getProcessInformation().getPrimaryTemplate().getVersion();
    return version.equals(Versions.WATERFALL) || version.equals(Versions.TRAVERTINE) || version.equals(Versions.HEXACORD) || version.equals(Versions.BUNGEECORD);
  }

  private static void rewriteBungeeConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
    rewriteFile(runningProcess.getPath().resolve("config.yml"), s -> {
      if (s.trim().startsWith("host:")) {
        s = "    host: '" + formatHost(runningProcess) + "'";
      } else if (s.trim().startsWith("ip_forward:")) {
        s = "ip_forward: true";
      } else if (s.trim().startsWith("- query_port:")) {
        s = "  - query_port: " + runningProcess.getProcessInformation().getHost().getPort();
      } else if (s.trim().startsWith("max_players:") && runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isUsePlayerLimit()) {
        s = "    max_players: " + runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
      }

      return s;
    });
  }

  //Waterdog
  private static boolean isLogicallyWaterDog(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
    Version version = runningProcess.getProcessInformation().getPrimaryTemplate().getVersion();
    return version.equals(Versions.WATERDOG) || version.equals(Versions.WATERDOG_PE);
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
        s = "    raknet: " + runningProcess.getProcessInformation().getPrimaryTemplate().getVersion().equals(Versions.WATERDOG_PE);
      } else if (s.trim().startsWith("- query_port:")) {
        s = "  - query_port: " + runningProcess.getProcessInformation().getHost().getPort();
      } else if (s.trim().startsWith("max_players:") && runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isUsePlayerLimit()) {
        s = "    max_players: " + runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
      }

      return s;
    });
  }

  //Velocity
  private static void rewriteVelocityConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
    rewriteFile(runningProcess.getPath().resolve("velocity.toml"), s -> {
      if (s.trim().startsWith("bind")) {
        s = "bind = \"" + formatHost(runningProcess) + "\"";
      } else if (s.trim().startsWith("show-max-players") && runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isUsePlayerLimit()) {
        s = "show-max-players = " + runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
      } else if (s.trim().startsWith("player-info-forwarding-mode")) {
        s = "player-info-forwarding-mode = \"LEGACY\"";
      }

      return s;
    });
  }

  //Glowstone
  private static boolean isLogicallyGlowstone(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
    Version version = runningProcess.getProcessInformation().getPrimaryTemplate().getVersion();
    return version.equals(Versions.GLOWSTONE_1_10_2) || version.equals(Versions.GLOWSTONE_1_12_2);
  }

  private static void rewriteGlowstoneConfig(@NotNull DefaultNodeLocalProcessWrapper runningProcess) {
    rewriteFile(runningProcess.getPath().resolve("config/glowstone.yml"), s -> {
      if (s.trim().startsWith("ip:")) {
        s = "  ip: '" + runningProcess.getProcessInformation().getHost().getHost() + "'";
      } else if (s.trim().startsWith("port:")) {
        s = "  port: " + runningProcess.getProcessInformation().getHost().getPort();
      } else if (s.trim().startsWith("online-mode:")) {
        s = "  online-mode: false";
      } else if (s.trim().startsWith("proxy-support:")) {
        s = "  proxy-support: true";
      } else if (s.trim().startsWith("max-players:") && runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isUsePlayerLimit()) {
        s = "  max-players: " + runningProcess.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
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
    processInformation.getProcessInformation().getPrimaryTemplate().getTemplateInclusions(loadType).forEach(e -> {
      String[] splitTemplate = e.getKey().split("/");
      if (splitTemplate.length != 2) {
        return;
      }

      TemplateBackendManager.getOrDefault(e.getBackend()).loadTemplate(
        splitTemplate[0],
        splitTemplate[1],
        processInformation.getPath()
      ).awaitUninterruptedly();
    });
  }

  private static void loadPathInclusions(@NotNull DefaultNodeLocalProcessWrapper processInformation, @NotNull Inclusion.InclusionLoadType loadType) {
    processInformation.getProcessInformation().getPrimaryTemplate().getPathInclusions(loadType).forEach(e -> {
      TemplateBackendManager.getOrDefault(e.getBackend()).loadPath(
        e.getKey(),
        processInformation.getPath()
      ).awaitUninterruptedly();
    });
  }

  private static void initGlobalTemplateAndCurrentTemplate(@NotNull DefaultNodeLocalProcessWrapper processInformation) {
    TemplateBackendManager.getOrDefault(processInformation.getProcessInformation().getPrimaryTemplate().getBackend()).loadGlobalTemplates(
      processInformation.getProcessInformation().getProcessGroup(),
      processInformation.getPath()
    ).awaitUninterruptedly();

    TemplateBackendManager.getOrDefault(processInformation.getProcessInformation().getPrimaryTemplate().getBackend()).loadTemplate(
      processInformation.getProcessInformation().getProcessGroup().getName(),
      processInformation.getProcessInformation().getPrimaryTemplate().getName(),
      processInformation.getPath()
    ).awaitUninterruptedly();
  }

  private static String formatHost(DefaultNodeLocalProcessWrapper wrapper) {
    final NetworkAddress address = wrapper.getProcessInformation().getHost();
    return String.format(address.toInetAddress() instanceof Inet6Address ? "[%s]:%d" : "%s:%d", address.getHost(), address.getPort());
  }

  private static void rewriteServerProperties(DefaultNodeLocalProcessWrapper wrapper) {
    Properties properties = new Properties();
    try (InputStream inputStream = Files.newInputStream(Paths.get(wrapper.getPath() + "/server.properties"))) {
      properties.load(inputStream);
      properties.setProperty("server-ip", wrapper.getProcessInformation().getHost().getHost());
      properties.setProperty("server-port", Integer.toString(wrapper.getProcessInformation().getHost().getPort()));
      properties.setProperty("online-mode", Boolean.toString(false));

      final PlayerAccessConfiguration configuration = wrapper.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration();
      if (configuration.isUsePlayerLimit() && configuration.getMaxPlayers() >= 0) {
        properties.setProperty("max-players", Integer.toString(configuration.getMaxPlayers()));
      }

      try (OutputStream outputStream = Files.newOutputStream(Paths.get(wrapper.getPath() + "/server.properties"))) {
        properties.store(outputStream, "ReformCloud2 node edit");
      }
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }

  private static boolean installVersion(DefaultNodeLocalProcessWrapper wrapper) {
    Version version = wrapper.getProcessInformation().getPrimaryTemplate().getVersion();
    VersionInstaller installer = VersionInstallerRegistry
      .getInstaller(version.getInstaller())
      .orElseThrow(() -> new RuntimeException("Installer " + version.getInstaller() + " not registered"));
    return installer.installVersion(version);
  }

  private static void writeProcessInformation(DefaultNodeLocalProcessWrapper wrapper) {
    final ByteBuf byteBuf = Unpooled.buffer();
    try (OutputStream outputStream = Files.newOutputStream(wrapper.getPath().resolve(".reformcloud/info"))) {
      final ProtocolBuffer buffer = new DefaultProtocolBuffer(byteBuf);
      wrapper.getProcessInformation().write(buffer);

      outputStream.write(buffer.toByteArray());
    } catch (IOException exception) {
      exception.printStackTrace();
    } finally {
      byteBuf.release();
    }
  }
}
