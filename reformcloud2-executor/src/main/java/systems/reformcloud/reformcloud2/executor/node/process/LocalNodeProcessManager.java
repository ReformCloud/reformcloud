package systems.reformcloud.reformcloud2.executor.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.Template;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.*;

public class LocalNodeProcessManager implements NodeProcessManager {

    private final Collection<ProcessInformation> information = new ArrayList<>();

    @Override
    public ProcessInformation getLocalCloudProcess(String name) {
        return Links.filterToReference(information, e -> e.getName().equals(name)).orNothing();
    }

    @Override
    public ProcessInformation getLocalCloudProcess(UUID uuid) {
        return Links.filterToReference(information, e -> e.getProcessUniqueID().equals(uuid)).orNothing();
    }

    @Override
    public ProcessInformation startLocalProcess(ProcessGroup processGroup, Template template, JsonConfiguration data) {
        return null;
    }

    @Override
    public ProcessInformation stopLocalProcess(String name) {
        return null;
    }

    @Override
    public ProcessInformation stopLocalProcess(UUID uuid) {
        return null;
    }

    @Override
    public ProcessInformation queueProcess(ProcessGroup processGroup, Template template, JsonConfiguration data, NodeInformation node) {
        return null;
    }

    @Override
    public void handleProcessDisconnect(String name) {
        ProcessInformation information = getLocalCloudProcess(name);
        if (information == null) {
            return;
        }

        this.information.remove(information);
        removeProcess(information);
    }

    @Override
    public boolean isLocal(String name) {
        return Links.filterToReference(information, e -> e.getName().equals(name)).isPresent();
    }

    @Override
    public boolean isLocal(UUID uniqueID) {
        return Links.filterToReference(information, e -> e.getProcessUniqueID().equals(uniqueID)).isPresent();
    }

    @Override
    public Collection<ProcessInformation> getLocalProcesses() {
        return Collections.unmodifiableCollection(information);
    }

    @Override
    public Iterator<ProcessInformation> iterator() {
        return Links.newList(information).iterator();
    }

    @Override
    public void update(ProcessInformation processInformation) {

    }

    private void removeProcess(ProcessInformation information) {
        NodeProcess nodeProcess = Links.filter(NodeExecutor.getInstance().getNodeNetworkManager()
                .getCluster().getSelfNode().getStartedProcesses(), e -> e.getUniqueID().equals(information.getProcessUniqueID()));
        if (nodeProcess == null) {
            return;
        }

        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getStartedProcesses().remove(nodeProcess);
        NodeExecutor.getInstance().getClusterSyncManager().syncProcessStop(information);
    }
}
