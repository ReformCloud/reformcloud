package systems.reformcloud.reformcloud2.executor.node.process.manager;

import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class LocalProcessManager {

    private LocalProcessManager() { throw new UnsupportedOperationException(); }

    private static final Collection<RunningProcess> NODE_PROCESSES = new CopyOnWriteArrayList<>();

    public static void registerLocalProcess(RunningProcess nodeProcess) {
        NODE_PROCESSES.add(nodeProcess);
    }

    public static void unregisterProcesses(UUID uniqueID) {
        Streams.filterToReference(NODE_PROCESSES, e -> e.getProcessInformation().getProcessUniqueID().equals(uniqueID)).ifPresent(NODE_PROCESSES::remove);
    }

    public static Collection<RunningProcess> getNodeProcesses() {
        return NODE_PROCESSES;
    }

    public static void close() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        NODE_PROCESSES.forEach(e -> executorService.submit(() -> e.shutdown()));

        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
