package systems.reformcloud.reformcloud2.executor.node.process.manager;

import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;

import java.util.ArrayList;
import java.util.Collection;

public class LocalProcessManager {

    private LocalProcessManager() { throw new UnsupportedOperationException(); }

    private static final Collection<LocalNodeProcess> NODE_PROCESSES = new ArrayList<>();

    public static void registerLocalProcess(LocalNodeProcess nodeProcess) {
        NODE_PROCESSES.add(nodeProcess);
    }

    public static void unregisterProcesses(LocalNodeProcess localNodeProcess) {
        NODE_PROCESSES.remove(localNodeProcess);
    }

    public static Collection<LocalNodeProcess> getNodeProcesses() {
        return NODE_PROCESSES;
    }

    public static void close() {
        NODE_PROCESSES.forEach(LocalNodeProcess::shutdown);
        NODE_PROCESSES.clear();
    }
}
