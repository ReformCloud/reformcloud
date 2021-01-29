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
package systems.reformcloud.node.cluster;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.base.Conditions;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.event.Event;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.event.events.group.MainGroupCreateEvent;
import systems.reformcloud.event.events.group.MainGroupDeleteEvent;
import systems.reformcloud.event.events.group.MainGroupUpdateEvent;
import systems.reformcloud.event.events.group.ProcessGroupCreateEvent;
import systems.reformcloud.event.events.group.ProcessGroupDeleteEvent;
import systems.reformcloud.event.events.group.ProcessGroupUpdateEvent;
import systems.reformcloud.event.events.process.ProcessRegisterEvent;
import systems.reformcloud.event.events.process.ProcessUnregisterEvent;
import systems.reformcloud.event.events.process.ProcessUpdateEvent;
import systems.reformcloud.group.main.MainGroup;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.group.template.Template;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.node.NodeInformation;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.process.builder.ProcessInclusion;
import systems.reformcloud.task.Task;
import systems.reformcloud.wrappers.ProcessWrapper;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.node.access.ClusterAccessController;
import systems.reformcloud.node.group.DefaultNodeMainGroupProvider;
import systems.reformcloud.node.group.DefaultNodeProcessGroupProvider;
import systems.reformcloud.node.process.DefaultNodeProcessProvider;
import systems.reformcloud.node.process.DefaultNodeRemoteProcessWrapper;
import systems.reformcloud.node.protocol.NodeToNodeCreateMainGroup;
import systems.reformcloud.node.protocol.NodeToNodeCreateProcessGroup;
import systems.reformcloud.node.protocol.NodeToNodeDeleteMainGroup;
import systems.reformcloud.node.protocol.NodeToNodeDeleteProcessGroup;
import systems.reformcloud.node.protocol.NodeToNodeRegisterProcess;
import systems.reformcloud.node.protocol.NodeToNodeSetMainGroups;
import systems.reformcloud.node.protocol.NodeToNodeSetProcessGroups;
import systems.reformcloud.node.protocol.NodeToNodeSetProcesses;
import systems.reformcloud.node.protocol.NodeToNodeUnregisterProcess;
import systems.reformcloud.node.protocol.NodeToNodeUpdateMainGroup;
import systems.reformcloud.node.protocol.NodeToNodeUpdateNodeInformation;
import systems.reformcloud.node.protocol.NodeToNodeUpdateProcess;
import systems.reformcloud.node.protocol.NodeToNodeUpdateProcessGroup;
import systems.reformcloud.node.provider.DefaultNodeNodeInformationProvider;
import systems.reformcloud.protocol.api.NodeToApiMainGroupCreate;
import systems.reformcloud.protocol.api.NodeToApiMainGroupDelete;
import systems.reformcloud.protocol.api.NodeToApiMainGroupUpdated;
import systems.reformcloud.protocol.api.NodeToApiProcessGroupCreate;
import systems.reformcloud.protocol.api.NodeToApiProcessGroupDelete;
import systems.reformcloud.protocol.api.NodeToApiProcessGroupUpdated;
import systems.reformcloud.protocol.api.NodeToApiProcessRegister;
import systems.reformcloud.protocol.api.NodeToApiProcessUnregister;
import systems.reformcloud.protocol.api.NodeToApiProcessUpdated;
import systems.reformcloud.shared.node.DefaultNodeInformation;

import java.util.Collection;
import java.util.UUID;

public class DefaultClusterManager implements ClusterManager {

  private final DefaultNodeNodeInformationProvider nodeInformationProvider;
  private final DefaultNodeProcessProvider processProvider;
  private final DefaultNodeProcessGroupProvider processGroupProvider;
  private final DefaultNodeMainGroupProvider mainGroupProvider;
  private NodeInformation head;

  public DefaultClusterManager(DefaultNodeNodeInformationProvider nodeInformationProvider, DefaultNodeProcessProvider processProvider,
                               DefaultNodeProcessGroupProvider processGroupProvider, DefaultNodeMainGroupProvider mainGroupProvider,
                               DefaultNodeInformation head) {
    this.nodeInformationProvider = nodeInformationProvider;
    this.processProvider = processProvider;
    this.processGroupProvider = processGroupProvider;
    this.mainGroupProvider = mainGroupProvider;
    this.head = head;
  }

  @Override
  public @NotNull Task<ProcessWrapper> createProcess(@NotNull ProcessGroup processGroup, @Nullable String node, @Nullable String displayName,
                                                     @Nullable String messageOfTheDay, @Nullable Template template, @NotNull Collection<ProcessInclusion> inclusions,
                                                     @NotNull JsonConfiguration jsonConfiguration, @NotNull ProcessState initialState, @NotNull UUID uniqueId, int memory,
                                                     int id, int maxPlayers, @Nullable String targetProcessFactory) {
    return ClusterAccessController.createProcessPrivileged(
      processGroup, node, displayName, messageOfTheDay, template, inclusions, jsonConfiguration,
      initialState, uniqueId, memory, id, maxPlayers, targetProcessFactory
    ).thenSupply(result -> {
      if (result == null) {
        return null;
      }

      if (NodeExecutor.getInstance().isOwnIdentity(result.getId().getNodeName())) {
        return ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(result.getId().getUniqueId()).orElse(null);
      }

      return new DefaultNodeRemoteProcessWrapper(result);
    });
  }

  @Override
  public void handleNodeConnect(@NotNull NodeInformation nodeInformation) {
    this.nodeInformationProvider.addNode(nodeInformation);
    this.updateHead();
  }

  @Override
  public void handleNodeUpdate(@NotNull NodeInformation nodeInformation) {
    this.nodeInformationProvider.updateNode(nodeInformation);
  }

  @Override
  public void publishNodeUpdate(@NotNull NodeInformation nodeInformation) {
    this.sendPacketToNodes(new NodeToNodeUpdateNodeInformation(nodeInformation));
  }

  @Override
  public void handleNodeDisconnect(@NotNull String name) {
    this.nodeInformationProvider.removeNode(name);
    this.updateHead();
  }

  @Override
  public void handleProcessRegister(@NotNull ProcessInformation processInformation) {
    this.processProvider.registerProcess(processInformation);
    this.sendPacketToProcesses(new NodeToApiProcessRegister(processInformation));

    this.callEvent(new ProcessRegisterEvent(processInformation));
  }

  @Override
  public void publishProcessRegister(@NotNull ProcessInformation processInformation) {
    this.sendPacketToNodes(new NodeToNodeRegisterProcess(processInformation));
    this.sendPacketToProcesses(new NodeToApiProcessRegister(processInformation));

    this.callEvent(new ProcessRegisterEvent(processInformation));
  }

  @Override
  public void handleProcessUpdate(@NotNull ProcessInformation processInformation) {
    this.processProvider.updateProcessInformation0(processInformation);
    this.sendPacketToProcesses(new NodeToApiProcessUpdated(processInformation));

    this.callEvent(new ProcessUpdateEvent(processInformation));
  }

  @Override
  public void publishProcessUpdate(@NotNull ProcessInformation processInformation) {
    this.sendPacketToNodes(new NodeToNodeUpdateProcess(processInformation));
    this.sendPacketToProcesses(new NodeToApiProcessUpdated(processInformation));

    this.callEvent(new ProcessUpdateEvent(processInformation));
  }

  @Override
  public void handleProcessUnregister(@NotNull String name) {
    this.processProvider.getProcessByName(name).ifPresent(processWrapper -> {
      this.sendPacketToProcesses(new NodeToApiProcessUnregister(processWrapper.getProcessInformation()));
      this.callEvent(new ProcessUnregisterEvent(processWrapper.getProcessInformation()));
    });
    this.processProvider.unregisterProcess(name);
  }

  @Override
  public void publishProcessUnregister(@NotNull ProcessInformation processInformation) {
    this.processProvider.unregisterProcess(processInformation.getName());

    this.sendPacketToNodes(new NodeToNodeUnregisterProcess(processInformation.getName()));
    this.sendPacketToProcesses(new NodeToApiProcessUnregister(processInformation));

    this.callEvent(new ProcessUnregisterEvent(processInformation));
  }

  @Override
  public void handleProcessSet(@NotNull Collection<ProcessInformation> processInformation) {
    for (ProcessInformation information : processInformation) {
      this.handleProcessRegister(information);
    }
  }

  @Override
  public void publishProcessSet(@NotNull Collection<ProcessInformation> processInformation) {
    this.sendPacketToNodes(new NodeToNodeSetProcesses(processInformation));
  }

  @Override
  public void handleProcessGroupCreate(@NotNull ProcessGroup processGroup) {
    this.processGroupProvider.addProcessGroup0(processGroup);
    this.sendPacketToProcesses(new NodeToApiProcessGroupCreate(processGroup));

    this.callEvent(new ProcessGroupCreateEvent(processGroup));
  }

  @Override
  public void publishProcessGroupCreate(@NotNull ProcessGroup processGroup) {
    this.sendPacketToNodes(new NodeToNodeCreateProcessGroup(processGroup));
    this.sendPacketToProcesses(new NodeToApiProcessGroupCreate(processGroup));

    this.callEvent(new ProcessGroupCreateEvent(processGroup));
  }

  @Override
  public void handleProcessGroupUpdate(@NotNull ProcessGroup processGroup) {
    this.processGroupProvider.updateProcessGroup0(processGroup);
    this.sendPacketToProcesses(new NodeToApiProcessGroupUpdated(processGroup));

    this.callEvent(new ProcessGroupUpdateEvent(processGroup));
  }

  @Override
  public void publishProcessGroupUpdate(@NotNull ProcessGroup processGroup) {
    this.sendPacketToNodes(new NodeToNodeUpdateProcessGroup(processGroup));
    this.sendPacketToProcesses(new NodeToApiProcessGroupUpdated(processGroup));

    this.callEvent(new ProcessGroupUpdateEvent(processGroup));
  }

  @Override
  public void handleProcessGroupDelete(@NotNull ProcessGroup processGroup) {
    this.processGroupProvider.deleteProcessGroup0(processGroup.getName());
    this.sendPacketToProcesses(new NodeToApiProcessGroupDelete(processGroup));

    this.callEvent(new ProcessGroupDeleteEvent(processGroup));
  }

  @Override
  public void publishProcessGroupDelete(@NotNull ProcessGroup processGroup) {
    this.sendPacketToNodes(new NodeToNodeDeleteProcessGroup(processGroup));
    this.sendPacketToProcesses(new NodeToApiProcessGroupDelete(processGroup));

    this.callEvent(new ProcessGroupDeleteEvent(processGroup));
  }

  @Override
  public void handleProcessGroupSet(@NotNull Collection<ProcessGroup> processGroups) {
    for (ProcessGroup processGroup : processGroups) {
      if (!this.processGroupProvider.getProcessGroup(processGroup.getName()).isPresent()) {
        this.processGroupProvider.addProcessGroup0(processGroup);
      } else {
        this.processGroupProvider.updateProcessGroup0(processGroup);
      }
    }
  }

  @Override
  public void publishProcessGroupSet(@NotNull Collection<ProcessGroup> processGroups) {
    this.sendPacketToNodes(new NodeToNodeSetProcessGroups(processGroups));
  }

  @Override
  public void handleMainGroupCreate(@NotNull MainGroup mainGroup) {
    this.mainGroupProvider.addGroup0(mainGroup);
    this.sendPacketToProcesses(new NodeToApiMainGroupCreate(mainGroup));

    this.callEvent(new MainGroupCreateEvent(mainGroup));
  }

  @Override
  public void publishMainGroupCreate(@NotNull MainGroup mainGroup) {
    this.sendPacketToNodes(new NodeToNodeCreateMainGroup(mainGroup));
    this.sendPacketToProcesses(new NodeToApiMainGroupCreate(mainGroup));

    this.callEvent(new MainGroupCreateEvent(mainGroup));
  }

  @Override
  public void handleMainGroupUpdate(@NotNull MainGroup mainGroup) {
    this.mainGroupProvider.updateMainGroup0(mainGroup);
    this.sendPacketToProcesses(new NodeToApiMainGroupUpdated(mainGroup));

    this.callEvent(new MainGroupUpdateEvent(mainGroup));
  }

  @Override
  public void publishMainGroupUpdate(@NotNull MainGroup mainGroup) {
    this.sendPacketToNodes(new NodeToNodeUpdateMainGroup(mainGroup));
    this.sendPacketToProcesses(new NodeToApiMainGroupUpdated(mainGroup));

    this.callEvent(new MainGroupUpdateEvent(mainGroup));
  }

  @Override
  public void handleMainGroupDelete(@NotNull MainGroup mainGroup) {
    this.mainGroupProvider.deleteMainGroup0(mainGroup.getName());
    this.sendPacketToProcesses(new NodeToApiMainGroupDelete(mainGroup));

    this.callEvent(new MainGroupDeleteEvent(mainGroup));
  }

  @Override
  public void publishMainGroupDelete(@NotNull MainGroup mainGroup) {
    this.sendPacketToNodes(new NodeToNodeDeleteMainGroup(mainGroup));
    this.sendPacketToProcesses(new NodeToApiMainGroupDelete(mainGroup));

    this.callEvent(new MainGroupDeleteEvent(mainGroup));
  }

  @Override
  public void handleMainGroupSet(@NotNull Collection<MainGroup> mainGroups) {
    for (MainGroup mainGroup : mainGroups) {
      if (!this.mainGroupProvider.getMainGroup(mainGroup.getName()).isPresent()) {
        this.mainGroupProvider.addGroup0(mainGroup);
      } else {
        this.mainGroupProvider.updateMainGroup0(mainGroup);
      }
    }
  }

  @Override
  public void publishMainGroupSet(@NotNull Collection<MainGroup> mainGroups) {
    this.sendPacketToNodes(new NodeToNodeSetMainGroups(mainGroups));
  }

  @Override
  public boolean isHeadNode() {
    return this.getHeadNode().getUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID());
  }

  @Override
  public @NotNull NodeInformation getHeadNode() {
    return this.head;
  }

  private void updateHead() {
    Collection<NodeInformation> nodes = ExecutorAPI.getInstance().getNodeInformationProvider().getNodes();
    Conditions.isTrue(!nodes.isEmpty(), "All node information were unregistered");

    for (NodeInformation node : nodes) {
      if (node.getStartupMillis() < this.head.getStartupMillis()) {
        this.head = node;
      }
    }
  }

  private void sendPacketToNodes(@NotNull Packet packet) {
    for (NodeInformation node : ExecutorAPI.getInstance().getNodeInformationProvider().getNodes()) {
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
        .getChannel(node.getName())
        .ifPresent(channel -> channel.sendPacket(packet));
    }
  }

  private void sendPacketToProcesses(@NotNull Packet packet) {
    for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
        .getChannel(process.getName())
        .ifPresent(channel -> channel.sendPacket(packet));
    }
  }

  private void callEvent(@NotNull Event event) {
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
  }
}
