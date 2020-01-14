package systems.reformcloud.reformcloud2.executor.node.process.manager;

import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class LocalProcessManager {

    private LocalProcessManager() { throw new UnsupportedOperationException(); }

    private static final Collection<LocalNodeProcess> NODE_PROCESSES = new ArrayList<>();

    public static void registerLocalProcess(LocalNodeProcess nodeProcess) {
        NODE_PROCESSES.add(nodeProcess);
    }

    public static void unregisterProcesses(UUID uniqueID) {
        Links.filterToReference(NODE_PROCESSES, e -> e.getProcessInformation().getProcessUniqueID().equals(uniqueID)).ifPresent(NODE_PROCESSES::remove);
    }

    public static Collection<LocalNodeProcess> getNodeProcesses() {
        return NODE_PROCESSES;
    }

    public static void close() {
        Links.newList(NODE_PROCESSES).forEach(LocalNodeProcess::shutdown);
        NODE_PROCESSES.clear();
    }
}
