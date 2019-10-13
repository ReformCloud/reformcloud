package systems.reformcloud.reformcloud2.executor.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.Template;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.NetworkInfo;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.startup.LocalProcessQueue;

import java.util.*;
import java.util.stream.Collectors;

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
        int id = nextID(processGroup);
        ProcessInformation processInformation = new ProcessInformation(
                processGroup.getName() + (processGroup.isShowIdInName() ? id : ""),
                NodeExecutor.getInstance().getNodeConfig().getName(),
                NodeExecutor.getInstance().getNodeConfig().getUniqueID(),
                UUID.randomUUID(),
                id,
                ProcessState.PREPARED,
                new NetworkInfo(
                        NodeExecutor.getInstance().getConnectHost().getFirst(),
                        NodeExecutor.getInstance().getConnectHost().getSecond(),
                        false
                ), processGroup,
                template,
                ProcessRuntimeInformation.empty(),
                new ArrayList<>(),
                data,
                processGroup.getPlayerAccessConfiguration().getMaxPlayers()
        );
        LocalProcessQueue.queue(processInformation);
        return processInformation;
    }

    @Override
    public ProcessInformation stopLocalProcess(String name) {
        List<LocalNodeProcess> processes = LocalProcessManager.getNodeProcesses()
                .stream()
                .filter(e -> e.getProcessInformation().getName().equals(name))
                .collect(Collectors.toList());
        if (processes.isEmpty()) {
            return null;
        }

        processes.forEach(LocalNodeProcess::shutdown);
        return processes.get(0).getProcessInformation();
    }

    @Override
    public ProcessInformation stopLocalProcess(UUID uuid) {
        List<LocalNodeProcess> processes = LocalProcessManager.getNodeProcesses()
                .stream()
                .filter(e -> e.getProcessInformation().getProcessUniqueID().equals(uuid))
                .collect(Collectors.toList());
        if (processes.isEmpty()) {
            return null;
        }

        processes.forEach(LocalNodeProcess::shutdown);
        return processes.get(0).getProcessInformation();
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
        if (isLocal(processInformation.getProcessUniqueID())) {
            Links.filterToReference(information, e -> e.getProcessUniqueID().equals(processInformation.getProcessUniqueID())).ifPresent(e -> {
                information.remove(e);
                information.add(processInformation);
            });
        }

        NodeExecutor.getInstance().getClusterSyncManager().syncProcessUpdate(processInformation);
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

    private int nextID(ProcessGroup processGroup) {
        int id = 1;
        Collection<Integer> ids = Links.newCollection(information, processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName()), ProcessInformation::getId);

        while (ids.contains(id)) {
            id++;
        }

        return id;
    }
}
