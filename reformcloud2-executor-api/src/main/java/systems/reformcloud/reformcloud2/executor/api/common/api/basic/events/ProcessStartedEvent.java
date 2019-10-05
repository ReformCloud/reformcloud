package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ProcessStartedEvent extends Event {

    public ProcessStartedEvent(ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    private final ProcessInformation processInformation;

    public ProcessInformation getProcessInformation() {
        return processInformation;
    }
}
