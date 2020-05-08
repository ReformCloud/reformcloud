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
package systems.reformcloud.reformcloud2.commands.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.application.packet.PacketGetCommandsConfig;
import systems.reformcloud.reformcloud2.commands.application.packet.PacketGetCommandsConfigResult;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.commands.plugin.packet.PacketReleaseCommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.velocity.commands.CommandLeave;
import systems.reformcloud.reformcloud2.commands.plugin.velocity.commands.CommandReformCloud;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;

@Plugin(
        id = "reformcloud_2_commands",
        name = "ReformCloud2Commands",
        version = "2.0",
        description = "Get access to default reformcloud2 commands",
        url = "https://reformcloud.systems",
        authors = {"derklaro"},
        dependencies = {@Dependency(id = "reformcloud_2_api_executor")}
)
public class VelocityPlugin {

    @Inject
    public VelocityPlugin(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;

        CommandConfigHandler.setInstance(new ConfigHandler());
        NetworkUtil.EXECUTOR.execute(() -> {
            PacketSender sender = DefaultChannelManager.INSTANCE.get("Controller").orNothing();
            while (sender == null) {
                sender = DefaultChannelManager.INSTANCE.get("Controller").orNothing();
            }

            ExecutorAPI.getInstance().getPacketHandler().registerHandler(PacketGetCommandsConfigResult.class);
            ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().sendQueryAsync(sender, new PacketGetCommandsConfig()).onComplete(e -> {
                if (e instanceof PacketGetCommandsConfigResult) {
                    CommandConfigHandler.getInstance().handleCommandConfigRelease(((PacketGetCommandsConfigResult) e).getCommandsConfig());
                    ExecutorAPI.getInstance().getPacketHandler().registerHandler(PacketReleaseCommandsConfig.class);
                }
            });
        });
    }

    private final ProxyServer proxyServer;

    private class ConfigHandler extends CommandConfigHandler {

        private CommandLeave commandLeave;

        private CommandReformCloud commandReformCloud;

        @Override
        public void handleCommandConfigRelease(@NotNull CommandsConfig commandsConfig) {
            unregisterAllCommands();
            if (commandsConfig.isLeaveCommandEnabled() && commandsConfig.getLeaveCommands().size() > 0) {
                this.commandLeave = new CommandLeave(commandsConfig.getLeaveCommands());
                VelocityPlugin.this.proxyServer.getCommandManager().register(
                        this.commandLeave,
                        commandsConfig.getLeaveCommands().toArray(new String[0])
                );
            }

            if (commandsConfig.isReformCloudCommandEnabled() && commandsConfig.getReformCloudCommands().size() > 0) {
                this.commandReformCloud = new CommandReformCloud(commandsConfig.getReformCloudCommands());
                VelocityPlugin.this.proxyServer.getCommandManager().register(
                        this.commandReformCloud,
                        commandsConfig.getReformCloudCommands().toArray(new String[0])
                );
            }
        }

        @Override
        public void unregisterAllCommands() {
            if (this.commandLeave != null) {
                this.commandLeave.getAliases().forEach(VelocityPlugin.this.proxyServer.getCommandManager()::unregister);
                this.commandLeave = null;
            }

            if (this.commandReformCloud != null) {
                this.commandReformCloud.getAliases().forEach(VelocityPlugin.this.proxyServer.getCommandManager()::unregister);
                this.commandReformCloud = null;
            }
        }
    }
}
