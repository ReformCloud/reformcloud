package systems.reformcloud.reformcloud2.executor.api.velocity.event;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

public final class ProcessEventHandler {

    @Listener
    public void handleStart(ProcessStartedEvent event) {
        VelocityExecutor.getInstance().publishNotification(
                VelocityExecutor.getInstance().getMessages().getProcessStarted(),
                event.getProcessInformation().getName()
        );
    }

    @Listener
    public void handleUpdate(ProcessUpdatedEvent event) {
        VelocityExecutor.getInstance().handleProcessUpdate(event.getProcessInformation());
    }

    @Listener
    public void handleRemove(ProcessStoppedEvent event) {
        VelocityExecutor.getInstance().handleProcessRemove(event.getProcessInformation());
    }
}
