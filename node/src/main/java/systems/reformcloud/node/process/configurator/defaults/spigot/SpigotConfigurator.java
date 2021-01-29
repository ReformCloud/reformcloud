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
package systems.reformcloud.node.process.configurator.defaults.spigot;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.group.template.ProcessConfigurators;
import systems.reformcloud.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.node.process.configurator.defaults.ConfiguratorUtils;
import systems.reformcloud.node.process.configurator.defaults.ServerPropertiesConfigurator;

import java.nio.file.Path;

public class SpigotConfigurator extends ServerPropertiesConfigurator {

  public SpigotConfigurator() {
    super("files/java/bukkit/server.properties");
  }

  @Override
  public String getName() {
    return ProcessConfigurators.SPIGOT;
  }

  @Override
  public void configure(@NotNull DefaultNodeLocalProcessWrapper wrapper) {
    final Path spigotYml = wrapper.getPath().resolve("spigot.yml");

    ConfiguratorUtils.extractCompiledFile("files/java/bukkit/spigot.yml", spigotYml);
    ConfiguratorUtils.rewriteFile(spigotYml, line -> {
      if (line.trim().startsWith("bungeecord:")) {
        line = "  bungeecord: true";
      }
      return line;
    });

    super.configure(wrapper);
  }
}
