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
        nodeInformation.add(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode());
    }

    @Override
    public void handleNodeDisconnect(@NotNull InternalNetworkCluster cluster, @NotNull String name) {
        Streams.allOf(nodeInformation, e -> e.getName().equals(name)).forEach(e -> {
            this.nodeInformation.remove(e);
            cluster.getConnectedNodes().remove(e);

            Streams.allOf(
                    Streams.newList(NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getClusterProcesses()),
                    i -> i.getProcessDetail().getParentUniqueID().equals(e.getNodeUniqueID())
            ).forEach(i -> {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessStop(i);
                DefaultClusterSyncManager.sendToAllExcludedNodes(new EventPacketProcessClosed(i));
            });

            if (head != null && head.getNodeUniqueID().equals(e.getNodeUniqueID())) {
                head = null;
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
        if (head == null) {
            this.recalculateHead();
        }

        return head;
    }

    @Override
    public void updateHeadNode(@NotNull NodeInformation newHeadNodeInformation) {
        this.head = newHeadNodeInformation;
    }

    private void recalculateHead() {
        for (NodeInformation information : nodeInformation) {
            if (head == null) {
                head = information;
            } else if (information.getStartupTime() < head.getStartupTime()) {
                head = information;
            }
        }
    }
}
