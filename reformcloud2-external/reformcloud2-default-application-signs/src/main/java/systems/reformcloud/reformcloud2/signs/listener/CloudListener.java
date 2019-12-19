package systems.reformcloud.reformcloud2.signs.listener;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;

public class CloudListener {

    @Listener
    public void handle(final ProcessStartedEvent event) {
        if (SignSystemAdapter.getInstance() == null) {
            return;
        }

        SignSystemAdapter.getInstance().handleProcessStart(event.getProcessInformation());
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (SignSystemAdapter.getInstance() == null) {
            return;
        }

        SignSystemAdapter.getInstance().handleProcessUpdate(event.getProcessInformation());
    }

    @Listener
    public void handle(final ProcessStoppedEvent event) {
        if (SignSystemAdapter.getInstance() == null) {
            return;
        }

        SignSystemAdapter.getInstance().handleProcessStop(event.getProcessInformation());
    }
}
