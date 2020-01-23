package systems.reformcloud.reformcloud2.executor.client.process;

import systems.reformcloud.reformcloud2.executor.api.client.process.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ProcessQueue extends AbsoluteThread {

    private static final Queue<RunningProcess> QUEUE = new ConcurrentLinkedQueue<>();

    public static void queue(ProcessInformation information) {
        RunningProcess runningProcess = RunningProcessBuilder.build(information).prepare();
        QUEUE.add(runningProcess);
        System.out.println(LanguageManager.get(
                "client-process-now-in-queue",
                runningProcess.getProcessInformation().getName(),
                QUEUE.size()
        ));
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
                if (runningProcess == null) {
                    continue;
                }

                if (isStartupNowLogic()) {
                    System.out.println(LanguageManager.get(
                            "client-process-start",
                            runningProcess.getProcessInformation().getName()
                    ));

                    if (runningProcess.bootstrap()) {
                        ClientExecutor.getInstance().getProcessManager().registerProcess(runningProcess);
                        System.out.println(LanguageManager.get(
                                "client-process-start-done",
                                runningProcess.getProcessInformation().getName()
                        ));
                    } else {
                        QUEUE.add(runningProcess);
                        System.out.println(LanguageManager.get(
                                "client-process-start-failed",
                                runningProcess.getProcessInformation().getName(),
                                QUEUE.size()
                        ));
                    }
                } else {
                    QUEUE.add(runningProcess);
                    System.out.println(LanguageManager.get(
                            "client-process-start-not-logic",
                            runningProcess.getProcessInformation().getName(),
                            CommonHelper.cpuUsageSystem(),
                            ClientExecutor.getInstance().getClientConfig().getMaxCpu(),
                            QUEUE.size()
                    ));
                }
            }

            AbsoluteThread.sleep(100);
        }
    }

    public static ProcessInformation removeFromQueue(UUID uuid) {
        synchronized (QUEUE) {
            RunningProcess process = Streams.filterToReference(QUEUE, e -> e.getProcessInformation().getProcessUniqueID().equals(uuid)).orNothing();
            if (process == null) {
                return null;
            }

            QUEUE.remove(process);
            return process.getProcessInformation();
        }
    }

    private static boolean isStartupNowLogic() {
        if (ClientExecutor.getInstance().getClientConfig().getMaxCpu() <= 0D) {
            return true;
        }

        return CommonHelper.cpuUsageSystem() <= ClientExecutor.getInstance().getClientConfig().getMaxCpu();
    }
}
