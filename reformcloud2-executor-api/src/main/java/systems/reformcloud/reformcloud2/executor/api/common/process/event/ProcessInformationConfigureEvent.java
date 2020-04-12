package systems.reformcloud.reformcloud2.executor.api.common.process.event;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

/**
 * This event gets only called on nodes/controller
 */
public class ProcessInformationConfigureEvent extends Event {

    public ProcessInformationConfigureEvent(@NotNull ProcessInformation information) {
        this.information = information;
    }

    private final ProcessInformation information;

    @NotNull
    public ProcessInformation getInformation() {
        return information;
    }
}
