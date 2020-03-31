package systems.reformcloud.reformcloud2.executor.api.node.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.BiMap;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;

import java.util.Collection;
import java.util.UUID;

public interface NodeNetworkManager {

    /**
     * @return The process helper which is associated with the current network manager instance
     */
    @NotNull
    NodeProcessManager getNodeProcessHelper();

    /**
     * @return The cluster in which the network manager is operating
     */
    @NotNull
    InternalNetworkCluster getCluster();

    /**
     * Prepares a process
     *
     * @param configuration The process configuration on which the new process is based
     * @param start         If the node should start the process after the preparation
     * @return The created process information from the process configuration
     */
    @Nullable
    ProcessInformation prepareProcess(@NotNull ProcessConfiguration configuration, boolean start);

    /**
     * Starts a process which is already prepared
     *
     * @param processInformation The process information of the prepared process
     * @return The process information after the start command sent
     */
    @NotNull
    ProcessInformation startProcess(@NotNull ProcessInformation processInformation);

    /**
     * Stops a process by it's name
     *
     * @param processName The name of the process which should get stopped
     */
    void stopProcess(@NotNull String processName);

    /**
     * Stops a process by it's unique id
     *
     * @param processUniqueID The unique id of the process which should get stopped
     */
    void stopProcess(@NotNull UUID processUniqueID);

    /**
     * @return All waiting process for which is no node available
     */
    @NotNull
    Collection<Duo<ProcessConfiguration, Boolean>> getWaitingProcesses();

    /**
     * @return All processes which are not registered with their processes information but already registered in the system
     */
    @NotNull
    BiMap<String, UUID> getRegisteredProcesses();
}
