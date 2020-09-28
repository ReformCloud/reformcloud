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
package systems.reformcloud.reformcloud2.commands.plugin.velocity.handler;

import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.commands.plugin.velocity.commands.CommandLeave;
import systems.reformcloud.reformcloud2.commands.plugin.velocity.commands.CommandReformCloud;

public class VelocityCommandConfigHandler extends CommandConfigHandler {

    private final ProxyServer proxyServer;
    private CommandLeave commandLeave;
    private CommandReformCloud commandReformCloud;

    public VelocityCommandConfigHandler(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void handleCommandConfigRelease(@NotNull CommandsConfig commandsConfig) {
        this.unregisterAllCommands();
        if (commandsConfig.isLeaveCommandEnabled() && commandsConfig.getLeaveCommands().size() > 0) {
            this.commandLeave = new CommandLeave(commandsConfig.getLeaveCommands());
            this.proxyServer.getCommandManager().register(
                    this.commandLeave,
                    commandsConfig.getLeaveCommands().toArray(new String[0])
            );
        }

        if (commandsConfig.isReformCloudCommandEnabled() && commandsConfig.getReformCloudCommands().size() > 0) {
            this.commandReformCloud = new CommandReformCloud(commandsConfig.getReformCloudCommands());
            this.proxyServer.getCommandManager().register(
                    this.commandReformCloud,
                    commandsConfig.getReformCloudCommands().toArray(new String[0])
            );
        }
    }

    @Override
    public void unregisterAllCommands() {
        if (this.commandLeave != null) {
            this.commandLeave.getAliases().forEach(this.proxyServer.getCommandManager()::unregister);
            this.commandLeave = null;
        }

        if (this.commandReformCloud != null) {
            this.commandReformCloud.getAliases().forEach(this.proxyServer.getCommandManager()::unregister);
            this.commandReformCloud = null;
        }
    }
}
