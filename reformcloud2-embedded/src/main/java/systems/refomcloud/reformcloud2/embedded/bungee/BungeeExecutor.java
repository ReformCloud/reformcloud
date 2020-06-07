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
package systems.refomcloud.reformcloud2.embedded.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.bungee.controller.BungeeProxyServerController;
import systems.refomcloud.reformcloud2.embedded.bungee.event.PlayerListenerHandler;
import systems.refomcloud.reformcloud2.embedded.bungee.executor.BungeePlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.bungee.reconnect.ReformCloudReconnectHandler;
import systems.refomcloud.reformcloud2.embedded.controller.ProxyServerController;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

public final class BungeeExecutor extends Embedded {

    private static BungeeExecutor instance;
    private final Plugin plugin;

    BungeeExecutor(Plugin plugin) {
        super();
        super.type = ExecutorType.API;

        instance = this;
        this.plugin = plugin;

        PlayerAPIExecutor.setInstance(new BungeePlayerAPIExecutor());
        ProxyServer.getInstance().setReconnectHandler(new ReformCloudReconnectHandler());
        super.getServiceRegistry().setProvider(ProxyServerController.class, new BungeeProxyServerController(), true);

        for (ProcessInformation process : super.getProcessProvider().getProcesses()) {
            this.getServiceRegistry().getProviderUnchecked(ProxyServerController.class).registerProcess(process);
        }

        ProxyServer.getInstance().getPluginManager().registerListener(this.plugin, new PlayerListenerHandler());
        this.fixInvalidPlayers();
    }

    private void fixInvalidPlayers() {
        SharedInvalidPlayerFixer.start(
                uuid -> ProxyServer.getInstance().getPlayer(uuid) != null,
                () -> ProxyServer.getInstance().getOnlineCount(),
                information -> super.processInformation = information
        );
    }

    @NotNull
    public static BungeeExecutor getInstance() {
        return instance;
    }

    @NotNull
    public Plugin getPlugin() {
        return this.plugin;
    }
}
