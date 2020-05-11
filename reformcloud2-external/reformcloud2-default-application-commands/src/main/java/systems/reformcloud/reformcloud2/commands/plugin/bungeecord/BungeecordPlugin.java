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
package systems.reformcloud.reformcloud2.commands.plugin.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.commands.application.packet.PacketGetCommandsConfig;
import systems.reformcloud.reformcloud2.commands.application.packet.PacketGetCommandsConfigResult;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.commands.plugin.bungeecord.handler.BungeeCommandConfigHandler;
import systems.reformcloud.reformcloud2.commands.plugin.packet.PacketReleaseCommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;

public class BungeecordPlugin extends Plugin {

    private static BungeecordPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        CommandConfigHandler.setInstance(new BungeeCommandConfigHandler());

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

    @Override
    public void onDisable() {
        CommandConfigHandler.getInstance().unregisterAllCommands();
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandler(NetworkUtil.EXTERNAL_BUS + 4);
    }

    public static BungeecordPlugin getInstance() {
        return instance;
    }
}
