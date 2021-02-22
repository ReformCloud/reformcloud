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
package systems.reformcloud.shared.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.configuration.data.DefaultJsonDataHolder;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.group.template.Template;
import systems.reformcloud.group.template.builder.DefaultTemplate;
import systems.reformcloud.network.address.DefaultNetworkAddress;
import systems.reformcloud.network.address.NetworkAddress;
import systems.reformcloud.network.data.ProtocolBuffer;
import systems.reformcloud.process.Identity;
import systems.reformcloud.process.Player;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessRuntimeInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.process.builder.ProcessInclusion;
import systems.reformcloud.shared.group.DefaultProcessGroup;
import systems.reformcloud.utility.MoreCollections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class DefaultProcessInformation extends DefaultJsonDataHolder<ProcessInformation> implements ProcessInformation {

  private Identity id;
  private NetworkAddress host;
  private Template primaryTemplate;
  private ProcessGroup processGroup;
  private ProcessRuntimeInformation runtimeInformation;
  private Collection<Player> players;
  private ProcessState currentState;
  private ProcessState initialState;
  private Collection<ProcessInclusion> inclusions;

  protected DefaultProcessInformation() {
  }

  public DefaultProcessInformation(JsonConfiguration dataHolder, Identity id, NetworkAddress host, Template primaryTemplate, ProcessGroup processGroup, ProcessRuntimeInformation info,
                                   Collection<Player> players, ProcessState currentState, ProcessState initialState, Collection<ProcessInclusion> inclusions) {
    super(dataHolder);
    this.id = id;
    this.host = host;
    this.primaryTemplate = primaryTemplate;
    this.processGroup = processGroup;
    this.runtimeInformation = info;
    this.players = players;
    this.currentState = currentState;
    this.initialState = initialState;
    this.inclusions = inclusions;
  }

  @Override
  public @NotNull Identity getId() {
    return this.id;
  }

  @Override
  public @NotNull NetworkAddress getHost() {
    return this.host;
  }

  @Override
  public @NotNull Template getPrimaryTemplate() {
    return this.primaryTemplate;
  }

  @Override
  public @NotNull ProcessGroup getProcessGroup() {
    return this.processGroup;
  }

  @Override
  public void setProcessGroup(@NotNull ProcessGroup processGroup) {
    this.processGroup = processGroup;
  }

  @Override
  public @NotNull ProcessRuntimeInformation getRuntimeInformation() {
    return this.runtimeInformation;
  }

  @Override
  public void setRuntimeInformation(@NotNull ProcessRuntimeInformation information) {
    this.runtimeInformation = information;
  }

  @Override
  public void addProcessInclusion(@NotNull ProcessInclusion inclusion) {
    this.inclusions.add(inclusion);
  }

  @Override
  public void removeProcessInclusion(@NotNull ProcessInclusion inclusion) {
    this.inclusions.remove(inclusion);
  }

  @Override
  public void removeAllProcessInclusions() {
    this.inclusions.clear();
  }

  @Override
  @Unmodifiable
  public @NotNull Collection<ProcessInclusion> getProcessInclusions() {
    return Collections.unmodifiableCollection(this.inclusions);
  }

  @Override
  public int getOnlineCount() {
    return this.players.size();
  }

  @Override
  public @NotNull Collection<Player> getPlayers() {
    return this.players;
  }

  @Override
  public @NotNull Optional<Player> getPlayerByName(@NotNull String name) {
    return MoreCollections.findFirst(this.players, player -> player.getName().equals(name));
  }

  @Override
  public @NotNull Optional<Player> getPlayerByUniqueId(@NotNull UUID uniqueId) {
    return MoreCollections.findFirst(this.players, player -> player.getUniqueID().equals(uniqueId));
  }

  @Override
  public @NotNull ProcessState getCurrentState() {
    return this.currentState;
  }

  @Override
  public void setCurrentState(@NotNull ProcessState newState) {
    this.currentState = newState;
  }

  @Override
  public @NotNull ProcessState getInitialState() {
    return this.initialState;
  }

  @Override
  public void setInitialState(@NotNull ProcessState newInitialState) {
    this.initialState = newInitialState;
  }

  @Override
  public @NotNull ProcessInformation clone() {
    return new DefaultProcessInformation(
      this.dataHolder.clone(),
      this.id.clone(),
      this.host.clone(),
      this.primaryTemplate.clone(),
      this.processGroup.clone(),
      this.runtimeInformation.clone(),
      new ArrayList<>(this.players),
      this.currentState,
      this.initialState,
      new ArrayList<>(this.inclusions)
    );
  }

  @Override
  public void update() {
    ExecutorAPI.getInstance().getProcessProvider().updateProcessInformation(this);
  }

  @Override
  public int compareTo(@NotNull ProcessInformation o) {
    return this.id.compareTo(o.getId());
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeObject(this.id);
    buffer.writeObject(this.host);
    buffer.writeObject(this.primaryTemplate);
    buffer.writeObject(this.processGroup);
    buffer.writeObject(this.runtimeInformation);
    buffer.writeObjects(this.players);
    buffer.writeEnum(this.currentState);
    buffer.writeEnum(this.initialState);
    super.write(buffer);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.id = buffer.readObject(DefaultIdentity.class, Identity.class);
    this.host = buffer.readObject(DefaultNetworkAddress.class, NetworkAddress.class);
    this.primaryTemplate = buffer.readObject(DefaultTemplate.class, Template.class);
    this.processGroup = buffer.readObject(DefaultProcessGroup.class, ProcessGroup.class);
    this.runtimeInformation = buffer.readObject(DefaultProcessRuntimeInformation.class, ProcessRuntimeInformation.class);
    this.players = buffer.readObjects(DefaultPlayer.class, Player.class);
    this.currentState = buffer.readEnum(ProcessState.class);
    this.initialState = buffer.readEnum(ProcessState.class);
    super.read(buffer);
  }

  @Override
  public @NotNull ProcessInformation self() {
    return this;
  }

  @Override
  public @NotNull String getName() {
    return this.id.getName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    DefaultProcessInformation that = (DefaultProcessInformation) o;
    return Objects.equals(this.id.getUniqueId(), that.id.getUniqueId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id.getUniqueId());
  }
}
