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
package systems.reformcloud.reformcloud2.node.process.configurator.defaults.glowstone;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.group.template.ProcessConfigurators;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.reformcloud2.node.process.configurator.defaults.ConfiguratorUtils;
import systems.reformcloud.reformcloud2.node.process.configurator.defaults.ServerPropertiesConfigurator;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;

public class GlowStoneConfigurator extends ServerPropertiesConfigurator {

  public GlowStoneConfigurator() {
    super("files/java/bukkit/server.properties");
  }

  @Override
  public void configure(@NotNull DefaultNodeLocalProcessWrapper wrapper) {
    super.configure(wrapper);

    IOUtils.createDirectory(wrapper.getPath().resolve("config"));
    ConfiguratorUtils.extractCompiledFile("files/java/glowstone/glowstone.yml", wrapper.getPath().resolve("/config/glowstone.yml"));

    ConfiguratorUtils.rewriteFile(wrapper.getPath().resolve("config/glowstone.yml"), line -> {
      if (line.trim().startsWith("ip:")) {
        line = "  ip: '" + wrapper.getProcessInformation().getHost().getHost() + "'";
      } else if (line.trim().startsWith("port:")) {
        line = "  port: " + wrapper.getProcessInformation().getHost().getPort();
      } else if (line.trim().startsWith("online-mode:")) {
        line = "  online-mode: false";
      } else if (line.trim().startsWith("proxy-support:")) {
        line = "  proxy-support: true";
      } else if (line.trim().startsWith("max-players:") && wrapper.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isUsePlayerLimit()) {
        line = "  max-players: " + wrapper.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
      }
      return line;
    });
  }

  @Override
  public String getName() {
    return ProcessConfigurators.GLOWSTONE;
  }
}
