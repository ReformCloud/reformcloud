package systems.reformcloud.reformcloud2.executor.client.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPrepareEvent;
import systems.reformcloud.reformcloud2.executor.client.network.packet.out.ClientPacketOutProcessPrepared;

public class RunningProcessPreparedListener {

    @Listener
    public void handle(final RunningProcessPrepareEvent event) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketOutProcessPrepared(
                event.getRunningProcess().getProcessInformation().getName(),
                event.getRunningProcess().getProcessInformation().getProcessUniqueID(),
                event.getRunningProcess().getProcessInformation().getTemplate().getName()
        )));
    }
}
