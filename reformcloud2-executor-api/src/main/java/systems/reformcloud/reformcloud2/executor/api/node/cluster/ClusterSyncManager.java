package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;

public interface ClusterSyncManager {

    void syncProcessStartup(ProcessInformation processInformation);

    void syncProcessUpdate(ProcessInformation processInformation);

    void syncProcessStop(ProcessInformation processInformation);

    void syncProcessGroups(Collection<ProcessGroup> processGroups);

    void syncMainGroup(Collection<MainGroup> mainGroups);

    Collection<ProcessGroup> getProcessGroups();

    Collection<MainGroup> getMainGroups();

    boolean existsProcessGroup(String name);

    boolean existsMainGroup(String name);

    void syncProcessGroupCreate(ProcessGroup group);

    void syncMainGroupCreate(MainGroup group);

    void syncProcessGroupUpdate(ProcessGroup processGroup);

    void syncMainGroupUpdate(MainGroup mainGroup);

    void syncProcessGroupDelete(String name);

    void syncMainGroupDelete(String name);

    void doClusterReload();

    void disconnectFromCluster();

    boolean isConnectedAndSyncWithCluster();

    Collection<String> getWaitingConnections();
}
