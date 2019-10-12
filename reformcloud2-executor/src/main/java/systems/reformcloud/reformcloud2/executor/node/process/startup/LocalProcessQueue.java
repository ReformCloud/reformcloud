package systems.reformcloud.reformcloud2.executor.node.process.startup;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.basic.BasicLocalNodeProcess;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class LocalProcessQueue extends AbsoluteThread {

    private static final Queue<LocalNodeProcess> QUEUE = new ConcurrentLinkedDeque<>();

    public LocalProcessQueue() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    public static void queue(ProcessInformation processInformation) {
        LocalNodeProcess localNodeProcess = new BasicLocalNodeProcess(processInformation);
        localNodeProcess.prepare();
        QUEUE.add(localNodeProcess);
    }

    @Override
    public void run() {
        while (!NodeExecutor.getInstance().getClusterSyncManager().isConnectedAndSyncWithCluster()) {
            AbsoluteThread.sleep(500);
        }

        while (!Thread.currentThread().isInterrupted()) {
            if (!QUEUE.isEmpty()) {
                LocalNodeProcess process = QUEUE.poll();
                if (!process.bootstrap()) {
                    QUEUE.add(process);
                }
            }

            AbsoluteThread.sleep(100);
        }
    }
}
