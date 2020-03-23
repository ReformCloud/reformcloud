package systems.reformcloud.reformcloud2.executor.api.common.process.event;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import javax.annotation.Nonnull;

/**
 * This event gets only called on nodes/controller
 */
public class ProcessInformationConfigureEvent extends Event {

    public ProcessInformationConfigureEvent(@Nonnull ProcessInformation information) {
        this.information = information;
    }

    private final ProcessInformation information;

    @Nonnull
    public ProcessInformation getInformation() {
        return information;
    }
}
