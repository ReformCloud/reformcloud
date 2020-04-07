package systems.reformcloud.reformcloud2.executor.api.common.process.running.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;

public abstract class RunningProcessEvent extends Event {

    public RunningProcessEvent(@NotNull RunningProcess runningProcess) {
        this.runningProcess = runningProcess;
    }

    private final RunningProcess runningProcess;

    /**
     * @return The running process which the current event targets
     */
    @NotNull
    public RunningProcess getRunningProcess() {
        return runningProcess;
    }
}
