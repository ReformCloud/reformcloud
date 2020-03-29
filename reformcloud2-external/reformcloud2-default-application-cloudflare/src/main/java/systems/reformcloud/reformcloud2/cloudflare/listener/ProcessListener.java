package systems.reformcloud.reformcloud2.cloudflare.listener;

import systems.reformcloud.reformcloud2.cloudflare.api.CloudFlareHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public final class ProcessListener {

    @Listener
    public void handle(final ProcessStartedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getTemplate().isServer()) {
            return;
        }

        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)
                && (event.getProcessInformation().getNodeUniqueID() == null
                || !event.getProcessInformation().getNodeUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID()))) {
            return;
        }

        CloudFlareHelper.createForProcess(event.getProcessInformation());
    }

    @Listener
    public void handle(final ProcessStoppedEvent event) {
        CloudFlareHelper.deleteRecord(event.getProcessInformation());
    }
}
