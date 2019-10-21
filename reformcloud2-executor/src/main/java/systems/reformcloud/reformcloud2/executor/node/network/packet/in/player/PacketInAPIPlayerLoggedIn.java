package systems.reformcloud.reformcloud2.executor.node.network.packet.in.player;

import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventPlayerConnected;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;

import java.util.function.Consumer;

public final class PacketInAPIPlayerLoggedIn implements NetworkHandler {

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

        DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventPlayerConnected(name));
    }
}
