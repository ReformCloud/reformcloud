package systems.reformcloud.reformcloud2.executor.client.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPrepareEvent;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.ClientPacketProcessPrepared;

public class RunningProcessPreparedListener {

    @Listener
    public void handle(final RunningProcessPrepareEvent event) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketProcessPrepared(
                event.getRunningProcess().getProcessInformation().getProcessDetail().getName(),
                event.getRunningProcess().getProcessInformation().getProcessDetail().getProcessUniqueID(),
                event.getRunningProcess().getProcessInformation().getProcessDetail().getTemplate().getName()
        )));
    }
}
