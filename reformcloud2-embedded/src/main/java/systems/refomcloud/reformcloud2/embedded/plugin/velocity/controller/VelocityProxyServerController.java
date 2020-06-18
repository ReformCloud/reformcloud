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
package systems.refomcloud.reformcloud2.embedded.plugin.velocity.controller;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.reformcloud2.embedded.controller.ProxyServerController;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VelocityProxyServerController implements ProxyServerController {

    public VelocityProxyServerController(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    private final ProxyServer proxyServer;

    private final List<ProcessInformation> cachedProxies = new CopyOnWriteArrayList<>();
    private final List<ProcessInformation> cachedLobbies = new CopyOnWriteArrayList<>();

    @Override
    public void registerProcess(@NotNull ProcessInformation processInformation) {
        if (!processInformation.getProcessDetail().getTemplate().isServer()) {
            this.cachedProxies.add(processInformation);
            return;
        }

        if (!processInformation.getNetworkInfo().isConnected()) {
            return;
        }

        if (processInformation.getProcessGroup().isCanBeUsedAsLobby()) {
            this.cachedLobbies.add(processInformation);
        }

        ServerInfo serverInfo = new ServerInfo(
                processInformation.getProcessDetail().getName(),
                processInformation.getNetworkInfo().toInet()
        );
        this.proxyServer.registerServer(serverInfo);
    }

    @Override
    public void handleProcessUpdate(@NotNull ProcessInformation processInformation) {
        if (!processInformation.getNetworkInfo().isConnected()) {
            this.cachedProxies.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            this.cachedLobbies.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            this.proxyServer.getServer(processInformation.getProcessDetail().getName())
                    .map(RegisteredServer::getServerInfo)
                    .ifPresent(this.proxyServer::unregisterServer);
            return;
        }

        if (!processInformation.getProcessDetail().getTemplate().isServer()) {
            this.cachedProxies.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            this.cachedProxies.add(processInformation);
            return;
        }

        if (processInformation.getProcessGroup().isCanBeUsedAsLobby()) {
            this.cachedLobbies.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            this.cachedLobbies.add(processInformation);
        }

        if (this.proxyServer.getServer(processInformation.getProcessDetail().getName()).isPresent()) {
            return;
        }

        ServerInfo serverInfo = new ServerInfo(
                processInformation.getProcessDetail().getName(),
                processInformation.getNetworkInfo().toInet()
        );
        this.proxyServer.registerServer(serverInfo);
    }

    @Override
    public void unregisterProcess(@NotNull ProcessInformation processInformation) {
        if (!processInformation.getProcessDetail().getTemplate().isServer()) {
            this.cachedProxies.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            return;
        }

        this.cachedLobbies.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
        this.proxyServer.getServer(processInformation.getProcessDetail().getName())
                .map(RegisteredServer::getServerInfo)
                .ifPresent(this.proxyServer::unregisterServer);
    }

    @Override
    public @NotNull @UnmodifiableView List<ProcessInformation> getCachedLobbyServers() {
        return Collections.unmodifiableList(this.cachedLobbies);
    }

    @Override
    public @NotNull @UnmodifiableView List<ProcessInformation> getCachedProxies() {
        return Collections.unmodifiableList(this.cachedProxies);
    }
}
