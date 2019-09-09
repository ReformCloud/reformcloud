package de.klaro.reformcloud2.executor.client.process;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.CommonHelper;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.client.ClientExecutor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ProcessQueue extends AbsoluteThread {

    private static final Queue<RunningProcess> QUEUE = new ConcurrentLinkedQueue<>();

    public static void queue(ProcessInformation information) {
        RunningProcess runningProcess = RunningProcessBuilder.build(information).prepare();
        QUEUE.add(runningProcess);
    }

    /* ============== */

    public ProcessQueue() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!QUEUE.isEmpty()) {
                RunningProcess runningProcess = QUEUE.poll();
                if (isStartupNowLogic() && runningProcess != null && runningProcess.bootstrap()) {
                    ClientExecutor.getInstance().getProcessManager().registerProcess(runningProcess);
                } else {
                    QUEUE.add(runningProcess);
                }
            }

            AbsoluteThread.sleep(100);
        }
    }

    private static boolean isStartupNowLogic() {
        if (ClientExecutor.getInstance().getClientConfig().getMaxCpu() <= 0D) {
            return true;
        }

        return CommonHelper.cpuUsageSystem() <= ClientExecutor.getInstance().getClientConfig().getMaxCpu();
    }
}
