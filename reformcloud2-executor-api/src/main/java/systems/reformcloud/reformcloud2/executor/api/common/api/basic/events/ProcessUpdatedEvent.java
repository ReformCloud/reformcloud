package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class ProcessUpdatedEvent extends Event {

    public ProcessUpdatedEvent(@NotNull ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    private final ProcessInformation processInformation;

    @NotNull
    public ProcessInformation getProcessInformation() {
        return processInformation;
    }
}
