package de.klaro.reformcloud2.executor.api.common.api.basic.packets.in.event;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.function.Consumer;

public final class EventPacketInProcessClosed implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.EVENT_BUS + 1;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        ProcessInformation processInformation = packet.content().get("info", ProcessInformation.TYPE);
        ExternalEventBusHandler.getInstance().callEvent(new ProcessStoppedEvent(processInformation));
    }
}
