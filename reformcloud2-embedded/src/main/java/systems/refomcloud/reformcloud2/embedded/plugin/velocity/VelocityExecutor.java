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
package systems.refomcloud.reformcloud2.embedded.plugin.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.controller.ProcessEventHandler;
import systems.refomcloud.reformcloud2.embedded.controller.ProxyServerController;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.plugin.velocity.controller.VelocityProxyServerController;
import systems.refomcloud.reformcloud2.embedded.plugin.velocity.event.PlayerListenerHandler;
import systems.refomcloud.reformcloud2.embedded.plugin.velocity.executor.VelocityPlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

public final class VelocityExecutor extends Embedded {

    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('§').extractUrls().build();

    private static VelocityExecutor instance;
    private final ProxyServer proxyServer;
    private final VelocityLauncher plugin;

    VelocityExecutor(VelocityLauncher launcher, ProxyServer proxyServer) {
        super.type = ExecutorType.API;
        PlayerAPIExecutor.setInstance(new VelocityPlayerAPIExecutor(proxyServer));

        instance = this;
        this.plugin = launcher;
        this.proxyServer = proxyServer;

        super.getServiceRegistry().setProvider(ProxyServerController.class, new VelocityProxyServerController(proxyServer), true);
        super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new ProcessEventHandler());

        for (ProcessInformation process : super.getProcessProvider().getProcesses()) {
            this.getServiceRegistry().getProviderUnchecked(ProxyServerController.class).registerProcess(process);
        }

        proxyServer.getEventManager().register(launcher, new PlayerListenerHandler());
        this.fixInvalidPlayers();
    }

    @NotNull
    public static VelocityExecutor getInstance() {
        return instance;
    }

    @Override
    protected int getMaxPlayersOfEnvironment() {
        return this.proxyServer.getConfiguration().getShowMaxPlayers();
    }

    @NotNull
    public VelocityLauncher getPlugin() {
        return this.plugin;
    }

    @NotNull
    public ProxyServer getProxyServer() {
        return this.proxyServer;
    }

    private void fixInvalidPlayers() {
        SharedInvalidPlayerFixer.start(
                uuid -> this.proxyServer.getPlayer(uuid).isPresent(),
                this.proxyServer::getPlayerCount
        );
    }
}
