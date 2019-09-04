package de.klaro.reformcloud2.executor.client.process;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.client.ClientExecutor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ProcessQueue extends AbsoluteThread {

    private static final Queue<ProcessInformation> QUEUE = new ConcurrentLinkedQueue<>();

    public static void queue(ProcessInformation information) {
        QUEUE.add(information);
    }

    /* ============== */

    public ProcessQueue() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!QUEUE.isEmpty()) {
                ProcessInformation processInformation = QUEUE.poll();
                System.out.println(processInformation.getName());
                RunningProcess runningProcess = RunningProcessBuilder.build(processInformation);
                if (runningProcess != null && runningProcess.bootstrap()) {
                    ClientExecutor.getInstance().getProcessManager().registerProcess(runningProcess);
                } else {
                    QUEUE.add(processInformation);
                }
            }

            AbsoluteThread.sleep(100);
        }
    }
}
