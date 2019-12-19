package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import java.util.Collection;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public interface ClusterSyncManager {

  void syncSelfInformation();

  void syncProcessStartup(ProcessInformation processInformation);

  void syncProcessUpdate(ProcessInformation processInformation);

  void syncProcessStop(ProcessInformation processInformation);

  void syncProcessGroups(Collection<ProcessGroup> processGroups,
                         SyncAction action);

  void syncMainGroups(Collection<MainGroup> mainGroups, SyncAction action);

  void syncProcessInformation(Collection<ProcessInformation> information);

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

  void handleProcessGroupSync(Collection<ProcessGroup> groups,
                              SyncAction action);

  void handleMainGroupSync(Collection<MainGroup> groups, SyncAction action);

  void handleProcessInformationSync(Collection<ProcessInformation> information);

  void handleClusterReload();

  void handleNodeInformationUpdate(NodeInformation nodeInformation);

  void doClusterReload();

  void disconnectFromCluster();

  boolean isConnectedAndSyncWithCluster();

  Collection<String> getWaitingConnections();
}
