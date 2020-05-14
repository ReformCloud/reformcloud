package systems.reformcloud.reformcloud2.executor.client.process;

import systems.reformcloud.reformcloud2.executor.api.client.process.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;

import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public final class ProcessQueue extends AbsoluteThread {

    private static final BlockingDeque<RunningProcess> QUEUE = new LinkedBlockingDeque<>();

    public ProcessQueue() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    /* ============== */

    public static void queue(ProcessInformation information) {
        RunningProcess runningProcess = RunningProcessBuilder.build(information);
        System.out.println(LanguageManager.get(
                "client-process-now-in-queue",
                runningProcess.getProcessInformation().getName(),
                QUEUE.size() + 1
        ));

        runningProcess.initTemplate().thenAccept(e -> {
            runningProcess.prepare();
            QUEUE.offerLast(runningProcess);
        });
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

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                RunningProcess process = QUEUE.takeFirst();

                if (isStartupNowLogic()) {
                    System.out.println(LanguageManager.get(
                            "client-process-start",
                            process.getProcessInformation().getName()
                    ));

                    if (process.bootstrap()) {
                        ClientExecutor.getInstance().getProcessManager().registerProcess(process);
                        System.out.println(LanguageManager.get(
                                "client-process-start-done",
                                process.getProcessInformation().getName()
                        ));
                    } else {
                        QUEUE.offerLast(process);
                        System.out.println(LanguageManager.get(
                                "client-process-start-failed",
                                process.getProcessInformation().getName(),
                                QUEUE.size()
                        ));
                    }
                } else {
                    QUEUE.add(process);
                }
            } catch (final InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
