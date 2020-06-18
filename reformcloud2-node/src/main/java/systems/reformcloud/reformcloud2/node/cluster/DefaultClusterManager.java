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
package systems.reformcloud.reformcloud2.node.cluster;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.event.Event;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.event.events.group.*;
import systems.reformcloud.reformcloud2.executor.api.event.events.process.ProcessRegisterEvent;
import systems.reformcloud.reformcloud2.executor.api.event.events.process.ProcessUnregisterEvent;
import systems.reformcloud.reformcloud2.executor.api.event.events.process.ProcessUpdateEvent;
import systems.reformcloud.reformcloud2.executor.api.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.access.ClusterAccessController;
import systems.reformcloud.reformcloud2.node.group.DefaultNodeMainGroupProvider;
import systems.reformcloud.reformcloud2.node.group.DefaultNodeProcessGroupProvider;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeProcessProvider;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeRemoteProcessWrapper;
import systems.reformcloud.reformcloud2.node.protocol.*;
import systems.reformcloud.reformcloud2.node.provider.DefaultNodeNodeInformationProvider;
import systems.reformcloud.reformcloud2.protocol.api.*;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DefaultClusterManager implements ClusterManager {

    public DefaultClusterManager(DefaultNodeNodeInformationProvider nodeInformationProvider, DefaultNodeProcessProvider processProvider,
                                 DefaultNodeProcessGroupProvider processGroupProvider, DefaultNodeMainGroupProvider mainGroupProvider,
                                 NodeInformation head) {
        this.nodeInformationProvider = nodeInformationProvider;
        this.processProvider = processProvider;
        this.processGroupProvider = processGroupProvider;
        this.mainGroupProvider = mainGroupProvider;
        this.head = head;
    }

    private final DefaultNodeNodeInformationProvider nodeInformationProvider;
    private final DefaultNodeProcessProvider processProvider;
    private final DefaultNodeProcessGroupProvider processGroupProvider;
    private final DefaultNodeMainGroupProvider mainGroupProvider;

    private NodeInformation head;

    @Override
    public @NotNull Task<ProcessWrapper> createProcess(@NotNull ProcessGroup processGroup, @Nullable String node, @Nullable String displayName, @Nullable String messageOfTheDay, @Nullable Template template, @NotNull Collection<ProcessInclusion> inclusions, @NotNull JsonConfiguration jsonConfiguration, @NotNull ProcessState initialState, @NotNull UUID uniqueId, int memory, int id, int maxPlayers, @Nullable String targetProcessFactory) {
        Task<ProcessInformation> task = ClusterAccessController.createProcessPrivileged(processGroup, node, displayName, messageOfTheDay, template, inclusions, jsonConfiguration, initialState, uniqueId, memory, id, maxPlayers, targetProcessFactory);
        return Task.supply(() -> {
            ProcessInformation result = task.getUninterruptedly(TimeUnit.SECONDS, 10);
            if (result == null) {
                return null;
            }

            if (NodeExecutor.getInstance().isOwnIdentity(result.getProcessDetail().getParentName())) {
                return ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(result.getProcessDetail().getProcessUniqueID()).orElse(null);
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
        this.sendPacketToNodes(new NodeToNodeUnregisterProcess(processInformation.getProcessDetail().getName()));
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
        return this.getHeadNode().getNodeUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID());
    }

    @Override
    public @NotNull NodeInformation getHeadNode() {
        return this.head;
    }

    private void updateHead() {
        Collection<NodeInformation> nodes = ExecutorAPI.getInstance().getNodeInformationProvider().getNodes();
        Conditions.isTrue(nodes.size() > 0, "All node information were unregistered");

        for (NodeInformation node : nodes) {
            if (node.getStartupTime() < this.head.getStartupTime()) {
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
                    .getChannel(process.getProcessDetail().getName())
                    .ifPresent(channel -> channel.sendPacket(packet));
        }
    }

    private void callEvent(@NotNull Event event) {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(event);
    }
}
