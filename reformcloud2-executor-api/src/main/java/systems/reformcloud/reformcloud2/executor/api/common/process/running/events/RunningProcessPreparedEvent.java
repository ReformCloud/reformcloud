package systems.reformcloud.reformcloud2.executor.api.common.process.running.events;

import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;

import javax.annotation.Nonnull;

/**
 * This event will only get called on clients and nodes!
 */
public class RunningProcessPreparedEvent extends RunningProcessEvent {

    public RunningProcessPreparedEvent(@Nonnull RunningProcess runningProcess) {
        super(runningProcess);
    }
}
