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
package systems.reformcloud.reformcloud2.node.process.configurator.defaults;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.address.NetworkAddress;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class ConfiguratorUtils {

  private ConfiguratorUtils() {
    throw new UnsupportedOperationException();
  }

  public static void rewriteServerProperties(@NotNull DefaultNodeLocalProcessWrapper wrapper) {
    Properties properties = new Properties();
    try (InputStream inputStream = Files.newInputStream(wrapper.getPath().resolve("server.properties"))) {
      properties.load(inputStream);
      properties.setProperty("server-ip", wrapper.getProcessInformation().getHost().getHost());
      properties.setProperty("server-port", Integer.toString(wrapper.getProcessInformation().getHost().getPort()));
      properties.setProperty("online-mode", Boolean.toString(false));
      properties.setProperty("use-native-transport", Boolean.toString(wrapper.getProcessInformation().getPrimaryTemplate().getVersion().isNativeTransportSupported()));

      final PlayerAccessConfiguration configuration = wrapper.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration();
      if (configuration.isUsePlayerLimit() && configuration.getMaxPlayers() >= 0) {
        properties.setProperty("max-players", Integer.toString(configuration.getMaxPlayers()));
      }

      try (OutputStream outputStream = Files.newOutputStream(wrapper.getPath().resolve("server.properties"))) {
        properties.store(outputStream, "ReformCloud2 node edit");
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static String formatHost(DefaultNodeLocalProcessWrapper wrapper) {
    final NetworkAddress address = wrapper.getProcessInformation().getHost();
    return String.format(address.toInetAddress() instanceof Inet6Address ? "[%s]:%d" : "%s:%d", address.getHost(), address.getPort());
  }

  public static void rewriteFile(@NotNull Path path, UnaryOperator<String> operator) {
    try {
      final List<String> lines = Files.readAllLines(path).stream().map(operator::apply).collect(Collectors.toList());
      if (!lines.isEmpty()) {
        Files.write(path, lines);
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static void extractCompiledFile(@NotNull String internalFile, @NotNull Path target) {
    if (Files.notExists(target)) {
      try (InputStream inputStream = ConfiguratorUtils.class.getClassLoader().getResourceAsStream(internalFile)) {
        Files.copy(Objects.requireNonNull(inputStream), target);
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }
  }

  public static void checkServerIcon(@NotNull DefaultNodeLocalProcessWrapper wrapper) {
    try {
      final Path iconPath = wrapper.getPath().resolve("server-icon.png");
      if (Files.exists(iconPath)) {
        final BufferedImage bufferedImage = ImageIO.read(wrapper.getPath().resolve("server-icon.png").toFile());
        if (bufferedImage.getHeight() != 64 || bufferedImage.getWidth() != 64) {
          System.err.println("The server icon of the process " + wrapper.getProcessInformation().getName() + " is not correctly sized");
          IOUtils.rename(Paths.get(wrapper.getPath() + "/server-icon.png"), wrapper.getPath() + "/server-icon-old.png");
          IOUtils.doOverrideInternalCopy(ConfiguratorUtils.class.getClassLoader(), "files/server-icon.png", wrapper.getPath() + "/server-icon.png");
        }
      } else {
        extractCompiledFile("files/server-icon.png", iconPath);
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
