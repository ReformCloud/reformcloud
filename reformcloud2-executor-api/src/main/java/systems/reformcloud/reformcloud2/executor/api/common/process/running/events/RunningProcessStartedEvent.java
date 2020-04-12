package systems.reformcloud.reformcloud2.executor.api.common.process.running.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;

/**
 * This event will only get called on clients and nodes!
 */
public class RunningProcessStartedEvent extends RunningProcessEvent {

    public RunningProcessStartedEvent(@NotNull RunningProcess runningProcess) {
        super(runningProcess);
    }
}
