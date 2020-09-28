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
package systems.refomcloud.reformcloud2.embedded.plugin.bungee.controller;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.reformcloud2.embedded.controller.ProxyServerController;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class BungeeProxyServerController implements ProxyServerController {

    private static Method constructServerInfo;

    static {
        try {
            constructServerInfo = ProxyServer.class.getMethod(
                    "constructServerInfo",
                    String.class,
                    SocketAddress.class,
                    String.class,
                    boolean.class,
                    boolean.class,
                    String.class
            );
            constructServerInfo.setAccessible(true);
        } catch (NoSuchMethodException ignored) {
            // we are not on a waterdog service
        }
    }

    public BungeeProxyServerController() {
        ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().clear());
        ProxyServer.getInstance().getConfig().getServers().clear();
    }

    private final List<ProcessInformation> cachedLobbyServices = new CopyOnWriteArrayList<>();
    private final List<ProcessInformation> cachedProxyServices = new CopyOnWriteArrayList<>();

    @Override
    public void registerProcess(@NotNull ProcessInformation processInformation) {
        if (!processInformation.getProcessDetail().getTemplate().isServer()) {
            this.cachedProxyServices.add(processInformation);
            return;
        }

        if (!processInformation.getNetworkInfo().isConnected()) {
            return;
        }

        if (processInformation.getProcessGroup().isCanBeUsedAsLobby()) {
            this.cachedLobbyServices.add(processInformation);
        }

        this.constructServerInfo(processInformation).ifPresent(
                info -> ProxyServer.getInstance().getServers().put(info.getName(), info)
        );
    }

    @Override
    public void handleProcessUpdate(@NotNull ProcessInformation processInformation) {
        if (!processInformation.getNetworkInfo().isConnected()) {
            this.cachedProxyServices.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            this.cachedLobbyServices.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            ProxyServer.getInstance().getServers().remove(processInformation.getProcessDetail().getName());
            return;
        }

        if (!processInformation.getProcessDetail().getTemplate().isServer()) {
            this.cachedProxyServices.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            this.cachedProxyServices.add(processInformation);
            return;
        }

        if (processInformation.getProcessGroup().isCanBeUsedAsLobby()) {
            this.cachedLobbyServices.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            this.cachedLobbyServices.add(processInformation);
        }

        if (ProxyServer.getInstance().getServerInfo(processInformation.getProcessDetail().getName()) != null) {
            return;
        }

        this.constructServerInfo(processInformation).ifPresent(
                info -> ProxyServer.getInstance().getServers().put(info.getName(), info)
        );
    }

    @Override
    public void unregisterProcess(@NotNull ProcessInformation processInformation) {
        if (!processInformation.getProcessDetail().getTemplate().isServer()) {
            this.cachedProxyServices.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
            return;
        }

        this.cachedLobbyServices.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
        ProxyServer.getInstance().getServers().remove(processInformation.getProcessDetail().getName());
    }

    @Override
    public @NotNull @UnmodifiableView List<ProcessInformation> getCachedLobbyServers() {
        return Collections.unmodifiableList(this.cachedLobbyServices);
    }

    @Override
    public @NotNull @UnmodifiableView List<ProcessInformation> getCachedProxies() {
        return Collections.unmodifiableList(this.cachedProxyServices);
    }

    private @NotNull Optional<ServerInfo> constructServerInfo(@NotNull ProcessInformation processInformation) {
        if (constructServerInfo != null) {
            // WaterDog for Pocket Edition
            if (processInformation.getProcessDetail().getTemplate().getVersion().getId() != 3) {
                return Optional.empty();
            }

            try {
                return Optional.of((ServerInfo) constructServerInfo.invoke(ProxyServer.getInstance(),
                        processInformation.getProcessDetail().getName(),
                        processInformation.getNetworkInfo().toInet(),
                        "ReformCloud2",
                        false,
                        processInformation.getProcessDetail().getTemplate().getVersion().getId() == 3,
                        "default"
                ));
            } catch (InvocationTargetException | IllegalAccessException exception) {
                exception.printStackTrace();
            }

            return Optional.empty();
        }

        return Optional.of(ProxyServer.getInstance().constructServerInfo(
                processInformation.getProcessDetail().getName(),
                processInformation.getNetworkInfo().toInet(),
                "ReformCloud2",
                false
        ));
    }
}
