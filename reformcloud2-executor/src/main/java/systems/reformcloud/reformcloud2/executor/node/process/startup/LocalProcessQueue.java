package systems.reformcloud.reformcloud2.executor.node.process.startup;

import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.basic.BasicLocalNodeProcess;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class LocalProcessQueue extends AbsoluteThread {

    private static final PriorityBlockingQueue<LocalNodeProcess> QUEUE = new PriorityBlockingQueue<>(15, Comparator.comparingInt(o -> o.getProcessInformation().getId()));

    public LocalProcessQueue() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    public static void queue(ProcessInformation processInformation) {
        LocalNodeProcess localNodeProcess = new BasicLocalNodeProcess(processInformation);
        localNodeProcess.prepare();
        int size = QUEUE.size();
        QUEUE.offer(localNodeProcess);
        System.out.println(LanguageManager.get("client-process-now-in-queue", processInformation.getName(), size +1));
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!NodeExecutor.getInstance().getClusterSyncManager().isConnectedAndSyncWithCluster()) {
                AbsoluteThread.sleep(500);
                continue;
            }

            try {
                LocalNodeProcess process = QUEUE.take();
                if (isMemoryFree(process.getProcessInformation().getTemplate().getRuntimeConfiguration().getMaxMemory())
                        && process.bootstrap()) {
                    System.out.println(LanguageManager.get("node-process-start", process.getProcessInformation().getName()));
                    AbsoluteThread.sleep(50);
                    continue;
                }

                System.out.println(LanguageManager.get("client-process-start-failed", process.getProcessInformation().getName(), QUEUE.size() +1));
                QUEUE.offer(process);
                AbsoluteThread.sleep(200);
            } catch (final InterruptedException ignored) {
            }
        }
    }

    private boolean isMemoryFree(int memory) {
        return NodeExecutor.getInstance().getNodeConfig().getMaxMemory() >=
                (NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getLocalProcesses()
                        .stream().mapToInt(e -> e.getTemplate().getRuntimeConfiguration().getMaxMemory()).sum() + memory);
    }
}
