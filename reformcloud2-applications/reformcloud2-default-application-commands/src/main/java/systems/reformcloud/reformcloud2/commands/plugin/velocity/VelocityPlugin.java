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
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.commands.application.packet.PacketGetCommandsConfig;
import systems.reformcloud.reformcloud2.commands.application.packet.PacketGetCommandsConfigResult;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.commands.plugin.packet.PacketReleaseCommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.velocity.handler.VelocityCommandConfigHandler;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;

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
    }

    private final ProxyServer proxyServer;

    @Subscribe
    public void handle(ProxyInitializeEvent event) {
        CommandConfigHandler.setInstance(new VelocityCommandConfigHandler(this.proxyServer));

        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).registerPacket(PacketGetCommandsConfigResult.class);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).registerPacket(PacketReleaseCommandsConfig.class);

        Embedded.getInstance().sendSyncQuery(new PacketGetCommandsConfig()).ifPresent(e -> {
            if (e instanceof PacketGetCommandsConfigResult) {
                CommandConfigHandler.getInstance().handleCommandConfigRelease(((PacketGetCommandsConfigResult) e).getCommandsConfig());
            }
        });
    }

    @Subscribe
    public void handle(ProxyShutdownEvent event) {
        CommandConfigHandler.getInstance().unregisterAllCommands();
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(NetworkUtil.RESERVED_EXTRA_BUS + 3);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(NetworkUtil.RESERVED_EXTRA_BUS + 2);
    }
}
