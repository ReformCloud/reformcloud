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
package systems.reformcloud.commands.plugin.waterdog.handler;

import dev.waterdog.ProxyServer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.commands.config.CommandsConfig;
import systems.reformcloud.commands.plugin.CommandConfigHandler;
import systems.reformcloud.commands.plugin.waterdog.commands.CommandLeave;
import systems.reformcloud.commands.plugin.waterdog.commands.CommandReformCloud;

public class WaterDogCommandConfigHandler extends CommandConfigHandler {

  private CommandLeave commandLeave;
  private CommandReformCloud commandReformCloud;

  @Override
  public void handleCommandConfigRelease(@NotNull CommandsConfig commandsConfig) {
    this.unregisterAllCommands();

    if (commandsConfig.isLeaveCommandEnabled() && !commandsConfig.getLeaveCommands().isEmpty()) {
      final String name = commandsConfig.getLeaveCommands().get(0);
      commandsConfig.getLeaveCommands().remove(name);
      this.commandLeave = new CommandLeave(name, commandsConfig.getLeaveCommands().toArray(new String[0]));
      ProxyServer.getInstance().getCommandMap().registerCommand(name, this.commandLeave);
    }

    if (commandsConfig.isReformCloudCommandEnabled() && !commandsConfig.getReformCloudCommands().isEmpty()) {
      final String name = commandsConfig.getReformCloudCommands().get(0);
      commandsConfig.getReformCloudCommands().remove(name);
      this.commandReformCloud = new CommandReformCloud(name, commandsConfig.getReformCloudCommands().toArray(new String[0]));
      ProxyServer.getInstance().getCommandMap().registerCommand(name, this.commandReformCloud);
    }
  }

  @Override
  public void unregisterAllCommands() {
    if (this.commandLeave != null) {
      ProxyServer.getInstance().getCommandMap().unregisterCommand(this.commandLeave.getName());
      this.commandLeave = null;
    }

    if (this.commandReformCloud != null) {
      ProxyServer.getInstance().getCommandMap().unregisterCommand(this.commandReformCloud.getName());
      this.commandReformCloud = null;
    }
  }
}
