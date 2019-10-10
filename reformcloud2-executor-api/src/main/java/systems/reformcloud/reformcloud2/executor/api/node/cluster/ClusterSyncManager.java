package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;

public interface ClusterSyncManager {

    void syncProcessStartup(ProcessInformation processInformation);

    void syncProcessUpdate(ProcessInformation processInformation);

    void syncProcessStop(ProcessInformation processInformation);

    void syncProcessGroups(Collection<ProcessGroup> processGroups);

    Collection<ProcessGroup> getProcessGroups();

    void doClusterReload();

    void disconnectFromCluster();

    boolean isConnectedAndSyncWithCluster();

    Collection<String> getWaitingConnections();
}
