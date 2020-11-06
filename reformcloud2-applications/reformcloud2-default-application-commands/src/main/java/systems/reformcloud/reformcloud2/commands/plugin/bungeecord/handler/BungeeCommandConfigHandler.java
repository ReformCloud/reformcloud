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
package systems.reformcloud.reformcloud2.commands.plugin.bungeecord.handler;

import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.commands.plugin.bungeecord.BungeecordPlugin;
import systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands.CommandLeave;
import systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands.CommandReformCloud;

public class BungeeCommandConfigHandler extends CommandConfigHandler {

    private CommandLeave leave;

    private CommandReformCloud reformCloud;

    @Override
    public void handleCommandConfigRelease(@NotNull CommandsConfig commandsConfig) {
        this.unregisterAllCommands();

        if (commandsConfig.isLeaveCommandEnabled() && !commandsConfig.getLeaveCommands().isEmpty()) {
            String name = commandsConfig.getLeaveCommands().get(0);
            commandsConfig.getLeaveCommands().remove(name);
            this.leave = new CommandLeave(name, commandsConfig.getLeaveCommands());
            ProxyServer.getInstance().getPluginManager().registerCommand(BungeecordPlugin.getInstance(), this.leave);
        }

        if (commandsConfig.isReformCloudCommandEnabled() && !commandsConfig.getReformCloudCommands().isEmpty()) {
            String name = commandsConfig.getReformCloudCommands().get(0);
            commandsConfig.getReformCloudCommands().remove(name);
            this.reformCloud = new CommandReformCloud(name, commandsConfig.getReformCloudCommands());
            ProxyServer.getInstance().getPluginManager().registerCommand(BungeecordPlugin.getInstance(), this.reformCloud);
        }
    }

    @Override
    public void unregisterAllCommands() {
        if (this.leave != null) {
            ProxyServer.getInstance().getPluginManager().unregisterCommand(this.leave);
            this.leave = null;
        }

        if (this.reformCloud != null) {
            ProxyServer.getInstance().getPluginManager().unregisterCommand(this.reformCloud);
            this.reformCloud = null;
        }
    }
}
