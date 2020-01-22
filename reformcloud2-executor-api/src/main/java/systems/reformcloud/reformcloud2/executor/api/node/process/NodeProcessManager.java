package systems.reformcloud.reformcloud2.executor.api.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.update.Updateable;

import java.util.Collection;
import java.util.UUID;

public interface NodeProcessManager extends Updateable<ProcessInformation>, Iterable<ProcessInformation> {

    ProcessInformation getLocalCloudProcess(String name);

    ProcessInformation getLocalCloudProcess(UUID uuid);

    ProcessInformation startLocalProcess(ProcessGroup processGroup, Template template, JsonConfiguration data, UUID uniqueID);

    ProcessInformation startLocalProcess(ProcessInformation processInformation);

    ProcessInformation stopLocalProcess(String name);

    ProcessInformation stopLocalProcess(UUID uuid);

    ProcessInformation queueProcess(ProcessGroup processGroup, Template template, JsonConfiguration data, NodeInformation node, UUID uniqueID);

    void registerLocalProcess(LocalNodeProcess process);

    void unregisterLocalProcess(UUID uniqueID);

    void handleLocalProcessStart(ProcessInformation processInformation);

    void handleLocalProcessStop(ProcessInformation processInformation);

    void handleProcessStart(ProcessInformation processInformation);

    void handleProcessUpdate(ProcessInformation processInformation);

    void handleProcessConnection(ProcessInformation processInformation);

    void handleProcessStop(ProcessInformation processInformation);

    void handleProcessDisconnect(String name);

    boolean isLocal(String name);

    boolean isLocal(UUID uniqueID);

    Collection<ProcessInformation> getClusterProcesses();

    Collection<ProcessInformation> getClusterProcesses(String group);

    Collection<ProcessInformation> getLocalProcesses();

    ProcessInformation getClusterProcess(String name);

    ProcessInformation getClusterProcess(UUID uniqueID);
}
