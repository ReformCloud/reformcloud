package de.klaro.reformcloud2.executor.client.watchdog;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.client.ClientExecutor;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class WatchdogThread extends AbsoluteThread {

    public WatchdogThread() {
        updatePriority(Thread.MIN_PRIORITY).enableDaemon().start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Links.newList(ClientExecutor.getInstance().getProcessManager().getAll()).forEach(new Consumer<RunningProcess>() {
                @Override
                public void accept(RunningProcess runningProcess) {
                    if (!runningProcess.running()
                            && runningProcess.getStartupTime() != -1
                            && runningProcess.getStartupTime() + TimeUnit.SECONDS.toMillis(2) < System.currentTimeMillis()) {
                        runningProcess.shutdown();
                    }
                }
            });

            AbsoluteThread.sleep(TimeUnit.SECONDS, 1);
        }
    }
}
