package systems.reformcloud.reformcloud2.executor.api.common.process.running.events;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;

import javax.annotation.Nonnull;

public abstract class RunningProcessEvent extends Event {

    public RunningProcessEvent(@Nonnull RunningProcess runningProcess) {
        this.runningProcess = runningProcess;
    }

    private final RunningProcess runningProcess;

    /**
     * @return The running process which the current event targets
     */
    @Nonnull
    public RunningProcess getRunningProcess() {
        return runningProcess;
    }
}
