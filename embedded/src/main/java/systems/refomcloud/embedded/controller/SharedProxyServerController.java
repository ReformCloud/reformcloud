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
package systems.refomcloud.embedded.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.process.ProcessInformation;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SharedProxyServerController implements ProxyServerController {

  private final Map<UUID, ProcessInformation> cachedLobbyServices = new ConcurrentHashMap<>();
  private final Map<UUID, ProcessInformation> cachedProxyServices = new ConcurrentHashMap<>();

  public SharedProxyServerController() {
    ExecutorAPI.getInstance().getProcessProvider().getProcessesAsync().thenAccept(information -> {
      for (ProcessInformation processInformation : information) {
        this.registerProcess(processInformation);
      }
    });
  }

  @Override
  public void registerProcess(@NotNull ProcessInformation information) {
    if (information.getPrimaryTemplate().getVersion().getVersionType().isProxy()) {
      this.cachedProxyServices.put(information.getId().getUniqueId(), information);
    } else if (information.getCurrentState().isOnline()) {
      this.registerServer(information);
      if (information.getProcessGroup().isLobbyGroup()) {
        this.cachedLobbyServices.put(information.getId().getUniqueId(), information);
      }
    }
  }

  @Override
  public void handleProcessUpdate(@NotNull ProcessInformation information) {
    if (!information.getCurrentState().isOnline()) {
      this.cachedProxyServices.remove(information.getId().getUniqueId());
      this.cachedLobbyServices.remove(information.getId().getUniqueId());
      this.unregisterServer(information);
    } else if (information.getPrimaryTemplate().getVersion().getVersionType().isProxy()) {
      this.cachedProxyServices.put(information.getId().getUniqueId(), information);
    } else {
      this.registerServerWhenUnknown(information);
      if (information.getProcessGroup().isLobbyGroup()) {
        this.cachedLobbyServices.put(information.getId().getUniqueId(), information);
      }
    }
  }

  @Override
  public void unregisterProcess(@NotNull ProcessInformation information) {
    if (information.getPrimaryTemplate().getVersion().getVersionType().isProxy()) {
      this.cachedProxyServices.remove(information.getId().getUniqueId());
    } else {
      this.cachedLobbyServices.remove(information.getId().getUniqueId());
      this.unregisterServer(information);
    }
  }

  @Override
  public @NotNull @UnmodifiableView Collection<ProcessInformation> getCachedLobbyServers() {
    return this.cachedLobbyServices.values();
  }

  @Override
  public @NotNull @UnmodifiableView Collection<ProcessInformation> getCachedProxies() {
    return this.cachedProxyServices.values();
  }

  protected abstract void registerServer(@NotNull ProcessInformation information);

  protected abstract void registerServerWhenUnknown(@NotNull ProcessInformation information);

  protected abstract void unregisterServer(@NotNull ProcessInformation information);
}
