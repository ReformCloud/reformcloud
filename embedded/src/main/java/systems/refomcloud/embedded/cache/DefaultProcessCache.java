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
package systems.refomcloud.embedded.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.event.events.process.ProcessRegisterEvent;
import systems.reformcloud.event.events.process.ProcessUnregisterEvent;
import systems.reformcloud.event.events.process.ProcessUpdateEvent;
import systems.reformcloud.event.handler.Listener;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessInformationByName;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessInformationByUniqueId;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessInformationResult;
import systems.reformcloud.registry.service.ServiceRegistry;
import systems.reformcloud.utility.MoreCollections;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DefaultProcessCache implements ProcessCache {

  private final Set<ProcessInformation> cache = ConcurrentHashMap.newKeySet();
  private ProcessCachePolicy cachePolicy;

  private final Function<Packet, ProcessInformation> resultMapper = result -> {
    if (result instanceof ApiToNodeGetProcessInformationResult) {
      final ProcessInformation processInformation = ((ApiToNodeGetProcessInformationResult) result).getProcessInformation();
      if (processInformation != null && this.cachePolicy.shouldCache(processInformation)) {
        this.cache.add(processInformation);
      }
      return processInformation;
    }
    return null;
  };

  public DefaultProcessCache() {
    this.cachePolicy = ProcessCachePolicy.never();
    ServiceRegistry.getUnchecked(EventManager.class).registerListener(this);
  }

  @Override
  public @NotNull Optional<ProcessInformation> provide(@NotNull String name) {
    final Optional<ProcessInformation> information = MoreCollections.findFirst(this.cache, info -> info.getName().equals(name));
    if (!information.isPresent()) {
      return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessInformationByName(name)).map(this.resultMapper);
    }
    return information;
  }

  @Override
  public @NotNull Optional<ProcessInformation> provide(@NotNull UUID uniqueId) {
    final Optional<ProcessInformation> information = MoreCollections.findFirst(this.cache, info -> info.getId().getUniqueId().equals(uniqueId));
    if (!information.isPresent()) {
      return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessInformationByUniqueId(uniqueId)).map(this.resultMapper);
    }
    return information;
  }

  @Override
  public @NotNull @UnmodifiableView Set<ProcessInformation> getCache() {
    return Collections.unmodifiableSet(this.cache);
  }

  @Override
  public int getSize() {
    return this.cache.size();
  }

  @Override
  public void invalidate() {
    this.cache.clear();
  }

  @Override
  public void refresh() {
    this.cache.clear();
    for (ProcessInformation process : Embedded.getInstance().getProcessProvider().getProcesses()) {
      if (this.cachePolicy.shouldCache(process)) {
        this.cache.add(process);
      }
    }
  }

  @Override
  public void setCachePolicy(@NotNull ProcessCachePolicy policy) {
    this.cachePolicy = policy;
  }

  @Listener
  public void handle(final @NotNull ProcessRegisterEvent event) {
    final ProcessInformation information = event.getProcessInformation();
    if (this.cachePolicy.shouldCache(information)) {
      this.cache.add(information);
    }
  }

  @Listener
  public void handle(final @NotNull ProcessUpdateEvent event) {
    final ProcessInformation information = event.getProcessInformation();
    if (this.cachePolicy.shouldCache(information)) {
      this.cache.removeIf(information::equals);
      this.cache.add(information);
    }
  }

  @Listener
  public void handle(final @NotNull ProcessUnregisterEvent event) {
    final ProcessInformation information = event.getProcessInformation();
    if (this.cachePolicy.shouldCache(information)) {
      this.cache.remove(information);
    }
  }
}
