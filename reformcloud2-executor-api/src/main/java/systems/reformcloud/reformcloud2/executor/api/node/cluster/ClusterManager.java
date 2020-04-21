package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

/**
 * Represents the manager of a cluster which handles the internal cluster
 */
public interface ClusterManager {

    /**
     * Initializes the current cluster manager
     */
    void init();

    /**
     * Handles the connection of a node to the cluster
     *
     * @param cluster         The cluster to which the node has connected
     * @param nodeInformation The information of the node which is connected
     */
    void handleConnect(@NotNull InternalNetworkCluster cluster, @NotNull NodeInformation nodeInformation);

    /**
     * Handles the disconnect of a node in the cluster
     *
     * @param cluster  The cluster from which the node has disconnected
     * @param nodeName The name of the node which disconnected
     */
    void handleNodeDisconnect(@NotNull InternalNetworkCluster cluster, @NotNull String nodeName);

    /**
     * Get all online and currently waiting processes of the specified group
     *
     * @param groupName The group of which the processes should be
     * @return The amount of online and waiting processes in the cluster
     */
    int getOnlineAndWaiting(@NotNull String groupName);

    /**
     * @return The information of the head node of the current cluster manager
     */
    @Nullable
    NodeInformation getHeadNode();

    /**
     * Updates the head node information
     *
     * @param newHeadNodeInformation The new node information of the head node
     */
    void updateHeadNode(@NotNull NodeInformation newHeadNodeInformation);
}
