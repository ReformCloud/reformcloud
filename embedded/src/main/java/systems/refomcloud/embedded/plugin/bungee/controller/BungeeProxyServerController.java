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
package systems.refomcloud.embedded.plugin.bungee.controller;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.embedded.controller.ProxyServerController;
import systems.reformcloud.group.template.version.VersionType;
import systems.reformcloud.process.ProcessInformation;

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

  private final List<ProcessInformation> cachedLobbyServices = new CopyOnWriteArrayList<>();
  private final List<ProcessInformation> cachedProxyServices = new CopyOnWriteArrayList<>();

  public BungeeProxyServerController() {
    ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().clear());
    ProxyServer.getInstance().getConfig().getServers().clear();
  }

  @Override
  public void registerProcess(@NotNull ProcessInformation processInformation) {
    if (!processInformation.getPrimaryTemplate().getVersion().getVersionType().isServer()) {
      this.cachedProxyServices.add(processInformation);
      return;
    }

    if (!processInformation.getCurrentState().isOnline()) {
      return;
    }

    if (processInformation.getProcessGroup().isLobbyGroup()) {
      this.cachedLobbyServices.add(processInformation);
    }

    this.constructServerInfo(processInformation).ifPresent(
      info -> ProxyServer.getInstance().getServers().put(info.getName(), info)
    );
  }

  @Override
  public void handleProcessUpdate(@NotNull ProcessInformation processInformation) {
    if (!processInformation.getCurrentState().isOnline()) {
      this.cachedProxyServices.removeIf(e -> e.getId().getUniqueId().equals(processInformation.getId().getUniqueId()));
      this.cachedLobbyServices.removeIf(e -> e.getId().getUniqueId().equals(processInformation.getId().getUniqueId()));
      ProxyServer.getInstance().getServers().remove(processInformation.getName());
      return;
    }

    if (processInformation.getPrimaryTemplate().getVersion().getVersionType().isProxy()) {
      this.cachedProxyServices.removeIf(e -> e.getId().getUniqueId().equals(processInformation.getId().getUniqueId()));
      this.cachedProxyServices.add(processInformation);
      return;
    }

    if (processInformation.getProcessGroup().isLobbyGroup()) {
      this.cachedLobbyServices.removeIf(e -> e.getId().getUniqueId().equals(processInformation.getId().getUniqueId()));
      this.cachedLobbyServices.add(processInformation);
    }

    if (ProxyServer.getInstance().getServerInfo(processInformation.getName()) != null) {
      return;
    }

    this.constructServerInfo(processInformation).ifPresent(
      info -> ProxyServer.getInstance().getServers().put(info.getName(), info)
    );
  }

  @Override
  public void unregisterProcess(@NotNull ProcessInformation processInformation) {
    if (processInformation.getPrimaryTemplate().getVersion().getVersionType().isProxy()) {
      this.cachedProxyServices.removeIf(e -> e.getId().getUniqueId().equals(processInformation.getId().getUniqueId()));
      return;
    }

    this.cachedLobbyServices.removeIf(e -> e.getId().getUniqueId().equals(processInformation.getId().getUniqueId()));
    ProxyServer.getInstance().getServers().remove(processInformation.getName());
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
      if (processInformation.getPrimaryTemplate().getVersion().getVersionType() != VersionType.POCKET_SERVER) {
        return Optional.empty();
      }

      try {
        return Optional.of((ServerInfo) constructServerInfo.invoke(ProxyServer.getInstance(),
          processInformation.getName(),
          processInformation.getHost().toInetSocketAddress(),
          "ReformCloud",
          false,
          processInformation.getPrimaryTemplate().getVersion().getVersionType() == VersionType.POCKET_SERVER,
          "default"
        ));
      } catch (InvocationTargetException | IllegalAccessException exception) {
        exception.printStackTrace();
      }

      return Optional.empty();
    }

    return Optional.of(ProxyServer.getInstance().constructServerInfo(
      processInformation.getName(),
      processInformation.getHost().toInetSocketAddress(),
      "ReformCloud",
      false
    ));
  }
}
