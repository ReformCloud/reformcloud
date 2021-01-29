/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.node.process.configurator.defaults.waterdog;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.group.template.ProcessConfigurators;
import systems.reformcloud.group.template.version.Versions;
import systems.reformcloud.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.node.process.configurator.ProcessConfigurator;
import systems.reformcloud.node.process.configurator.defaults.ConfiguratorUtils;

public class WaterdogConfigurator implements ProcessConfigurator {

  @Override
  public void configure(@NotNull DefaultNodeLocalProcessWrapper wrapper) {
    ConfiguratorUtils.checkServerIcon(wrapper);
    ConfiguratorUtils.extractCompiledFile("files/mcpe/waterdog/config.yml", wrapper.getPath().resolve("config.yml"));
    ConfiguratorUtils.rewriteFile(wrapper.getPath().resolve("config.yml"), line -> {
      if (line.trim().startsWith("host:")) {
        line = "    host: '" + ConfiguratorUtils.formatHost(wrapper) + "'";
      } else if (line.trim().startsWith("ip_forward:")) {
        line = "ip_forward: true";
      } else if (line.trim().startsWith("use_xuid_for_uuid:")) {
        line = "use_xuid_for_uuid: true";
      } else if (line.trim().startsWith("raknet:")) {
        line = "    raknet: " + wrapper.getProcessInformation().getPrimaryTemplate().getVersion().equals(Versions.WATERDOGPE);
      } else if (line.trim().startsWith("- query_port:")) {
        line = "  - query_port: " + wrapper.getProcessInformation().getHost().getPort();
      } else if (line.trim().startsWith("max_players:") && wrapper.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isUsePlayerLimit()) {
        line = "    max_players: " + wrapper.getProcessInformation().getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
      }
      return line;
    });
  }

  @Override
  public String getName() {
    return ProcessConfigurators.WATERDOG;
  }
}
