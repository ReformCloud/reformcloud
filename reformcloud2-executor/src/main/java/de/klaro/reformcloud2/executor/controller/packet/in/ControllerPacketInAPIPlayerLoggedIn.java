package de.klaro.reformcloud2.executor.controller.packet.in;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.PlayerLoginEvent;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.controller.ControllerExecutor;
import de.klaro.reformcloud2.executor.controller.packet.out.event.ControllerEventPlayerConnected;

import java.util.function.Consumer;

public final class ControllerPacketInAPIPlayerLoggedIn implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 2;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String name = packet.content().getString("name");
        System.out.println(LanguageManager.get(
                "player-logged-in",
                name,
                packetSender.getName()
        ));

        ControllerExecutor.getInstance().getEventManager().callEvent(new PlayerLoginEvent(name));
        ExecutorAPI.getInstance().getAllProcesses().forEach(process -> DefaultChannelManager.INSTANCE.get(process.getName()).ifPresent(channel -> channel.sendPacket(
                new ControllerEventPlayerConnected(name)
        )));
    }
}
