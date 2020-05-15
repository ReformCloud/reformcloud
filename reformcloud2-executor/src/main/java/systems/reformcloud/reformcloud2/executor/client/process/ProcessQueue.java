/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.client.process;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.process.basic.DefaultRunningProcess;

import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public final class ProcessQueue extends AbsoluteThread {

    private static final BlockingDeque<RunningProcess> QUEUE = new LinkedBlockingDeque<>();

    public ProcessQueue() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    public static void queue(ProcessInformation information) {
        RunningProcess runningProcess = new DefaultRunningProcess(information);
        System.out.println(LanguageManager.get(
                "client-process-now-in-queue",
                runningProcess.getProcessInformation().getProcessDetail().getName(),
                QUEUE.size() + 1
        ));

        runningProcess.prepare().onComplete(e -> {
            runningProcess.handleEnqueue();
            QUEUE.offerLast(runningProcess);
        });
    }

    /* ============== */

    public static void queue(RunningProcess process) {
        System.out.println(LanguageManager.get(
                "client-process-now-in-queue",
                process.getProcessInformation().getProcessDetail().getName(),
                QUEUE.size() + 1
        ));
        process.handleEnqueue();
        QUEUE.offerLast(process);
    }

    public static ProcessInformation removeFromQueue(UUID uuid) {
        synchronized (QUEUE) {
            RunningProcess process = Streams.filterToReference(QUEUE, e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uuid)).orNothing();
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
                            process.getProcessInformation().getProcessDetail().getName()
                    ));

                    if (process.bootstrap()) {
                        ClientExecutor.getInstance().getProcessManager().registerProcess(process);
                        System.out.println(LanguageManager.get(
                                "client-process-start-done",
                                process.getProcessInformation().getProcessDetail().getName()
                        ));
                    } else {
                        QUEUE.offerLast(process);
                        System.out.println(LanguageManager.get(
                                "client-process-start-failed",
                                process.getProcessInformation().getProcessDetail().getName(),
                                QUEUE.size()
                        ));
                    }
                } else {
                    QUEUE.add(process);
                }
            } catch (final InterruptedException ignored) {
            }
        }
    }
}
