package systems.reformcloud.reformcloud2.executor.api.node.network;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;

import java.util.Map;
import java.util.UUID;

public interface NodeNetworkManager {

    NodeProcessManager getNodeProcessHelper();

    InternalNetworkCluster getCluster();

    ProcessInformation getCloudProcess(String name);

    ProcessInformation getCloudProcess(UUID uuid);

    ProcessInformation startProcess(ProcessGroup processGroup, Template template, JsonConfiguration data);

    void stopProcess(String name);

    void stopProcess(UUID uuid);

    Map<UUID, String> getQueuedProcesses();
}
