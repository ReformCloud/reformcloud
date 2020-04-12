package systems.reformcloud.reformcloud2.executor.api.common.process.event;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessDetail;

/**
 * Gets called when a new process detail gets created
 */
public final class ProcessDetailConfigureEvent extends Event {

    public ProcessDetailConfigureEvent(@NotNull ProcessDetail processDetail) {
        this.processDetail = processDetail;
    }

    private final ProcessDetail processDetail;

    /**
     * @return The process detail which got created
     */
    @NotNull
    public ProcessDetail getProcessDetail() {
        return processDetail;
    }
}
