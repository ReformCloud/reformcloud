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
package systems.reformcloud.reformcloud2.executor.node.cluster;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketProcessClosed;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import static systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState.PREPARED;

public final class DefaultClusterManager implements ClusterManager {

    private final Collection<NodeInformation> nodeInformation = new CopyOnWriteArrayList<>();

    private NodeInformation head;

    @Override
    public void init() {
        this.nodeInformation.add(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode());
    }

    @Override
    public void handleNodeDisconnect(@NotNull InternalNetworkCluster cluster, @NotNull String name) {
        Streams.allOf(this.nodeInformation, e -> e.getName().equals(name)).forEach(e -> {
            this.nodeInformation.remove(e);
            cluster.getConnectedNodes().remove(e);

            Streams.allOf(
                    Streams.newList(NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getClusterProcesses()),
                    i -> i.getProcessDetail().getParentUniqueID().equals(e.getNodeUniqueID())
            ).forEach(i -> {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessStop(i);
                DefaultClusterSyncManager.sendToAllExcludedNodes(new EventPacketProcessClosed(i));
            });

            if (this.head != null && this.head.getNodeUniqueID().equals(e.getNodeUniqueID())) {
                this.head = null;
            }
        });

        this.recalculateHead();
    }

    @Override
    public void handleConnect(@NotNull InternalNetworkCluster cluster, @NotNull NodeInformation nodeInformation) {
        if (this.nodeInformation.stream().anyMatch(e -> e.getName().equals(nodeInformation.getName()))) {
            return;
        }

        this.nodeInformation.add(nodeInformation);
        cluster.getConnectedNodes().add(nodeInformation);
        this.recalculateHead();
    }

    @Override
    public int getOnlineAndWaiting(@NotNull String groupName) {
        int allNotPrepared = Streams.allOf(
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getClusterProcesses(),
                e -> e.getProcessGroup().getName().equals(groupName) && !e.getProcessDetail().getProcessState().equals(PREPARED)
        ).size();

        int waiting = Streams.allOf(
                NodeExecutor.getInstance().getNodeNetworkManager().getWaitingProcesses(),
                e -> e.getFirst().getBase().getName().equals(groupName)
        ).size();

        return allNotPrepared + waiting;
    }

    @Override
    public NodeInformation getHeadNode() {
        if (this.head == null) {
            this.recalculateHead();
        }

        return this.head;
    }

    @Override
    public void updateHeadNode(@NotNull NodeInformation newHeadNodeInformation) {
        this.head = newHeadNodeInformation;
    }

    private void recalculateHead() {
        for (NodeInformation information : this.nodeInformation) {
            if (this.head == null) {
                this.head = information;
            } else if (information.getStartupTime() < this.head.getStartupTime()) {
                this.head = information;
            }
        }
    }
}
