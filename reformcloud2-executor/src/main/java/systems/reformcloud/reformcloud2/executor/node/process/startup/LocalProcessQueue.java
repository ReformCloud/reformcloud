package systems.reformcloud.reformcloud2.executor.node.process.startup;

import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.basic.BasicLocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import javax.annotation.Nonnull;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class LocalProcessQueue extends AbsoluteThread {

    private static final BlockingDeque<RunningProcess> QUEUE = new LinkedBlockingDeque<>();

    public LocalProcessQueue() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    public static void queue(@Nonnull ProcessInformation processInformation) {
        RunningProcess localNodeProcess = new BasicLocalNodeProcess(processInformation);
        int size = QUEUE.size();
        System.out.println(LanguageManager.get("client-process-now-in-queue", processInformation.getName(), size +1));

        localNodeProcess.prepare().onComplete(e -> {
            localNodeProcess.handleEnqueue();
            QUEUE.offerLast(localNodeProcess);
        });
    }

    public static void queue(@Nonnull RunningProcess process) {
        System.out.println(LanguageManager.get("client-process-now-in-queue",
                process.getProcessInformation().getName(), QUEUE.size() +1));
        process.handleEnqueue();
        QUEUE.offerLast(process);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!NodeExecutor.getInstance().getClusterSyncManager().isConnectedAndSyncWithCluster()) {
                AbsoluteThread.sleep(500);
                continue;
            }

            try {
                RunningProcess process = QUEUE.takeFirst();
                if (isMemoryFree(process.getProcessInformation().getTemplate().getRuntimeConfiguration().getMaxMemory())
                        && process.bootstrap()) {
                    System.out.println(LanguageManager.get("node-process-start", process.getProcessInformation().getName()));
                    AbsoluteThread.sleep(50);
                    continue;
                }

                QUEUE.offerLast(process);
                AbsoluteThread.sleep(200);
            } catch (final InterruptedException ignored) {
            }
        }
    }

    private boolean isMemoryFree(int memory) {
        int current = LocalProcessManager.getNodeProcesses().stream()
                .mapToInt(e -> e.getProcessInformation().getTemplate().getRuntimeConfiguration().getMaxMemory()).sum() + memory;
        return NodeExecutor.getInstance().getNodeConfig().getMaxMemory() >= current;
    }
}
