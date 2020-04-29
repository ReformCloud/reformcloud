package systems.reformcloud.reformcloud2.executor.node.process.manager;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Deprecated
public final class LocalProcessManager { // TODO: I think a rework may be good :)

    private LocalProcessManager() {
        throw new UnsupportedOperationException();
    }

    private static final Set<RunningProcess> NODE_PROCESSES = Collections.synchronizedSet(new HashSet<RunningProcess>() {
        @Override
        public Stream<RunningProcess> stream() {
            synchronized (this) {
                return super.stream();
            }
        }

        @Override
        @NotNull
        public Iterator<RunningProcess> iterator() {
            synchronized (this) {
                return super.iterator();
            }
        }
    });

    public static void registerLocalProcess(RunningProcess nodeProcess) {
        NODE_PROCESSES.add(nodeProcess);
    }

    public static void unregisterProcesses(UUID uniqueID) {
        NODE_PROCESSES.removeIf(e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uniqueID));
    }

    public static Collection<RunningProcess> getNodeProcesses() {
        return NODE_PROCESSES;
    }

    public static void close() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        NODE_PROCESSES.stream().filter(e -> e.getProcess().isPresent()).forEach(e -> executorService.submit(() -> e.shutdown()));

        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
