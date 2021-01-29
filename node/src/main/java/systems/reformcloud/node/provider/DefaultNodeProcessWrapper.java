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
package systems.reformcloud.node.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.network.packet.query.QueryManager;
import systems.reformcloud.node.NodeInformation;
import systems.reformcloud.wrappers.NodeProcessWrapper;
import systems.reformcloud.node.protocol.NodeToNodeProcessCommand;
import systems.reformcloud.node.protocol.NodeToNodeProcessCommandResult;
import systems.reformcloud.node.protocol.NodeToNodeRequestNodeInformationUpdate;
import systems.reformcloud.node.protocol.NodeToNodeRequestNodeInformationUpdateResult;
import systems.reformcloud.node.protocol.NodeToNodeTabCompleteCommand;
import systems.reformcloud.node.protocol.NodeToNodeTabCompleteCommandResult;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DefaultNodeProcessWrapper implements NodeProcessWrapper {

  protected NodeInformation nodeInformation;

  protected DefaultNodeProcessWrapper(@NotNull NodeInformation nodeInformation) {
    this.nodeInformation = nodeInformation;
  }

  @NotNull
  @Override
  public NodeInformation getNodeInformation() {
    return this.nodeInformation;
  }

  @NotNull
  @Override
  public Optional<NodeInformation> requestNodeInformationUpdate() {
    Optional<NetworkChannel> channel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getChannel(this.nodeInformation.getName());
    if (!channel.isPresent()) {
      return Optional.empty();
    }

    Packet packet = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(QueryManager.class)
      .sendPacketQuery(channel.get(), new NodeToNodeRequestNodeInformationUpdate())
      .getUninterruptedly(TimeUnit.SECONDS, 5);
    if (!(packet instanceof NodeToNodeRequestNodeInformationUpdateResult)) {
      return Optional.empty();
    }

    return Optional.of(this.nodeInformation = ((NodeToNodeRequestNodeInformationUpdateResult) packet).getNodeInformation());
  }

  @NotNull
  @Override
  public @UnmodifiableView Collection<String> sendCommandLine(@NotNull String commandLine) {
    Optional<NetworkChannel> channel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getChannel(this.nodeInformation.getName());
    if (!channel.isPresent()) {
      return Collections.emptyList();
    }

    Packet packet = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(QueryManager.class)
      .sendPacketQuery(channel.get(), new NodeToNodeProcessCommand(commandLine))
      .getUninterruptedly(TimeUnit.SECONDS, 5);
    if (!(packet instanceof NodeToNodeProcessCommandResult)) {
      return Collections.emptyList();
    }

    return ((NodeToNodeProcessCommandResult) packet).getResult();
  }

  @NotNull
  @Override
  public @UnmodifiableView Collection<String> tabCompleteCommandLine(@NotNull String commandLine) {
    Optional<NetworkChannel> channel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getChannel(this.nodeInformation.getName());
    if (!channel.isPresent()) {
      return Collections.emptyList();
    }

    Packet packet = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(QueryManager.class)
      .sendPacketQuery(channel.get(), new NodeToNodeTabCompleteCommand(commandLine))
      .getUninterruptedly(TimeUnit.SECONDS, 5);
    if (!(packet instanceof NodeToNodeTabCompleteCommandResult)) {
      return Collections.emptyList();
    }

    return ((NodeToNodeTabCompleteCommandResult) packet).getResult();
  }

  public void updateNodeInformation(@NotNull NodeInformation nodeInformation) {
    this.nodeInformation = nodeInformation;
  }
}
