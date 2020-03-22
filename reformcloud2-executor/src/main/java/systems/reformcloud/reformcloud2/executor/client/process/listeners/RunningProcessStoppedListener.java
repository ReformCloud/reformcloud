package systems.reformcloud.reformcloud2.executor.client.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.network.packet.out.ClientPacketOutProcessStopped;

public class RunningProcessStoppedListener {

    @Listener
    public void handle(final RunningProcessStoppedEvent event) {
        ClientExecutor.getInstance().getProcessManager().unregisterProcess(event.getRunningProcess().getProcessInformation().getName());
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketOutProcessStopped(
                event.getRunningProcess().getProcessInformation().getProcessUniqueID(),
                event.getRunningProcess().getProcessInformation().getName()
        )));
    }
}
