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
package systems.reformcloud.node.network;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.language.TranslationHolder;
import systems.reformcloud.network.PacketIds;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.network.channel.shared.SharedChannelListener;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.utility.MoreCollections;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.node.cluster.ClusterManager;
import systems.reformcloud.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.protocol.shared.PacketAuthBegin;
import systems.reformcloud.protocol.shared.PacketAuthSuccess;
import systems.reformcloud.shared.node.DefaultNodeInformation;

import java.util.Optional;
import java.util.UUID;

public class NodeServerChannelListener extends SharedChannelListener {

  private int type = 0;

  public NodeServerChannelListener(NetworkChannel networkChannel) {
    super(networkChannel);
  }

  @Override
  public boolean shouldHandle(@NotNull Packet packet) {
    return super.networkChannel.getName() != null || packet.getId() == PacketIds.AUTH_BUS;
  }

  @Override
  public void channelActive(@NotNull NetworkChannel context) {
  }

  @Override
  public void channelInactive(@NotNull NetworkChannel channel) {
    if (channel.isOpen() && channel.isWritable()) {
      return;
    }

    if (super.networkChannel.getName().isEmpty()) {
      // channel is not authenticated - no need to do anything
      return;
    }

    if (this.type == 1) {
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).handleNodeDisconnect(super.networkChannel.getName());
    }

    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).unregisterChannel(super.networkChannel);
  }

  @Override
  public void channelWriteAbilityChanged(@NotNull NetworkChannel channel) {
    if (!channel.isActive() && !channel.isOpen() && !channel.isWritable()) {
      channel.closeSync();
    }
  }

  @Override
  public void handle(@NotNull Packet input) {
    if (input.getId() == PacketIds.AUTH_BUS) {
      if (!(input instanceof PacketAuthBegin)) {
        // should never happen
        super.networkChannel.close();
        return;
      }

      PacketAuthBegin packet = (PacketAuthBegin) input;
      this.type = packet.getType();

      if (packet.getType() == 1) {
        NodeNetworkClient.CONNECTIONS.remove(super.networkChannel.getRemoteAddress().getHost());
        DefaultNodeInformation nodeInformation = packet.getData().get("node", DefaultNodeInformation.TYPE);
        if (nodeInformation == null) {
          // invalid type to id
          super.networkChannel.close();
          return;
        }

        if (!MoreCollections.hasMatch(
          NodeExecutor.getInstance().getNodeConfig().getClusterNodes(),
          networkAddress -> networkAddress.getHost().equals(super.networkChannel.getRemoteAddress().getHost())
        )) {
          // invalid node connected (the node is not registered)
          super.networkChannel.close();
          return;
        }

        if (!packet.getConnectionKey().equals(NodeExecutor.getInstance().getNodeExecutorConfig().getConnectionKey())) {
          // invalid connection key sent by the node
          super.networkChannel.close();
          return;
        }

        super.networkChannel.setName(nodeInformation.getName());
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).handleNodeConnect(nodeInformation);

        System.out.println(TranslationHolder.translate("network-node-other-node-connected", nodeInformation.getName()));
      } else if (packet.getType() == 2) {
        UUID processUniqueId = packet.getData().get("pid", UUID.class);
        if (processUniqueId == null) {
          // invalid data
          super.networkChannel.close();
          return;
        }

        Optional<DefaultNodeLocalProcessWrapper> wrapper = NodeExecutor.getInstance().getDefaultNodeProcessProvider().getProcessWrapperByUniqueId(processUniqueId);
        if (!wrapper.isPresent()) {
          // either the process is not registered or not running on the local node
          super.networkChannel.close();
          return;
        }

        DefaultNodeLocalProcessWrapper processWrapper = wrapper.get();
        if (!processWrapper.getConnectionKey().equals(packet.getConnectionKey())) {
          // the provided connection key by the process is invalid
          super.networkChannel.close();
          return;
        }

        ProcessInformation information = processWrapper.getProcessInformation();
        super.networkChannel.setName(information.getId().getName());

        information.setCurrentState(information.getInitialState());

        ExecutorAPI.getInstance().getProcessProvider().updateProcessInformation(information);
        System.out.println(TranslationHolder.translate("process-connected-to-node", information.getName()));
      } else {
        // invalid data
        super.networkChannel.close();
        return;
      }

      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).registerChannel(super.networkChannel);

      JsonConfiguration data;
      if (this.type == 1) {
        data = JsonConfiguration.newJsonConfiguration().add("node", NodeExecutor.getInstance().getCurrentNodeInformation());
      } else {
        data = JsonConfiguration.newJsonConfiguration();
      }

      super.networkChannel.sendPacket(new PacketAuthSuccess(data));

      if (this.type == 1) {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessGroupSet(
          ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()
        );
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishMainGroupSet(
          ExecutorAPI.getInstance().getMainGroupProvider().getMainGroups()
        );
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessSet(
          ExecutorAPI.getInstance().getProcessProvider().getProcesses()
        );
      }
      return;
    }

    super.handle(input);
  }
}
