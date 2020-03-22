package systems.reformcloud.reformcloud2.executor.api.node.network;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;

import java.util.Map;
import java.util.UUID;

public interface NodeNetworkManager {

    NodeProcessManager getNodeProcessHelper();

    InternalNetworkCluster getCluster();

    ProcessInformation getCloudProcess(String name);

    ProcessInformation getCloudProcess(UUID uuid);

    ProcessInformation prepareProcess(ProcessConfiguration configuration, boolean start);

    ProcessInformation startProcess(ProcessConfiguration configuration);

    ProcessInformation startProcess(ProcessInformation processInformation);

    void stopProcess(String name);

    void stopProcess(UUID uuid);

    Map<UUID, String> getQueuedProcesses();
}
