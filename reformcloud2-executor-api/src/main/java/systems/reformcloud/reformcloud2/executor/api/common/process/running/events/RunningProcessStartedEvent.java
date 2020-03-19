package systems.reformcloud.reformcloud2.executor.api.common.process.running.events;

import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;

import javax.annotation.Nonnull;

/**
 * This event will only get called on clients and nodes!
 */
public class RunningProcessStartedEvent extends RunningProcessEvent {

    public RunningProcessStartedEvent(@Nonnull RunningProcess runningProcess) {
        super(runningProcess);
    }
}
