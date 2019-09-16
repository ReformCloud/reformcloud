package de.klaro.reformcloud2.executor.controller.packet.in;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.PlayerLogoutEvent;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.controller.ControllerExecutor;
import de.klaro.reformcloud2.executor.controller.join.OnlyProxyJoinHelper;
import de.klaro.reformcloud2.executor.controller.packet.out.event.ControllerEventLogoutPlayer;

import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerPacketInAPILogoutPlayer implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 3;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uuid", UUID.class);
        String name = packet.content().getString("name");

        OnlyProxyJoinHelper.onDisconnect(uuid);
        System.out.println(LanguageManager.get(
                "player-logged-out",
                name,
                uuid,
                packetSender.getName()
        ));

        ControllerExecutor.getInstance().getEventManager().callEvent(new PlayerLogoutEvent(name, uuid));
        ExecutorAPI.getInstance().getAllProcesses().forEach(process -> DefaultChannelManager.INSTANCE.get(process.getName()).ifPresent(channel -> channel.sendPacket(
                new ControllerEventLogoutPlayer(name, uuid)
        )));
    }
}
