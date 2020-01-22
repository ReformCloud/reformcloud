package systems.reformcloud.reformcloud2.cloudflare.listener;

import systems.reformcloud.reformcloud2.cloudflare.api.CloudFlareHelper;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;

public final class ProcessListener {

    @Listener
    public void handle(final ProcessStartedEvent event) {
        if (event.getProcessInformation().getTemplate().isServer()) {
            return;
        }

        CloudFlareHelper.createForProcess(event.getProcessInformation());
    }

    @Listener
    public void handle(final ProcessStoppedEvent event) {
        CloudFlareHelper.deleteRecord(event.getProcessInformation());
    }
}
