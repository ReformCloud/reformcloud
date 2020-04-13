package systems.reformcloud.reformcloud2.executor.node.network.packet.in.player;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerLogoutEvent;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventLogoutPlayer;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;

import java.util.UUID;
import java.util.function.Consumer;

public final class PacketInAPILogoutPlayer extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 3;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uuid", UUID.class);
        String name = packet.content().getString("name");

        System.out.println(LanguageManager.get(
                "player-logged-out",
                name,
                uuid,
                packetSender.getName()
        ));

        NodeExecutor.getInstance().getEventManager().callEvent(new PlayerLogoutEvent(name, uuid));
        DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventLogoutPlayer(name, uuid));
    }
}
