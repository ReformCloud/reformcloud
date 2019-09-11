package de.klaro.reformcloud2.executor.controller.packet.in;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerPacketInAddScreenLine implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 9;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String line = packet.content().getString("line");
        UUID uuid = packet.content().get("uuid", UUID.class);

        ProcessInformation processInformation = ExecutorAPI.getInstance().getProcess(uuid);
        if (processInformation != null) {
            System.out.println(LanguageManager.get("screen-line-added", processInformation.getName(), line));
        }
    }
}
