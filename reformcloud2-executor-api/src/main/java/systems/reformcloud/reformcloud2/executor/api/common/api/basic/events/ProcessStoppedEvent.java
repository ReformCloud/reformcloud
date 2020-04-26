package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class ProcessStoppedEvent extends Event {

    public ProcessStoppedEvent(@NotNull ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    private final ProcessInformation processInformation;

    @NotNull
    public ProcessInformation getProcessInformation() {
        return processInformation;
    }
}
