package systems.reformcloud.reformcloud2.executor.api.common.process.running.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;

/**
 * This event will only get called on clients and nodes!
 */
public class RunningProcessPrepareEvent extends RunningProcessEvent {

    public RunningProcessPrepareEvent(@NotNull RunningProcess runningProcess) {
        super(runningProcess);
    }
}
