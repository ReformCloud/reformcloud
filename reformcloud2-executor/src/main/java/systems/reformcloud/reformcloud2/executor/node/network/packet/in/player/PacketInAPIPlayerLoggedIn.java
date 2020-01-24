package systems.reformcloud.reformcloud2.executor.node.network.packet.in.player;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerLoginEvent;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventPlayerConnected;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class PacketInAPIPlayerLoggedIn extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 2;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        String name = packet.content().getString("name");
        System.out.println(LanguageManager.get(
                "player-logged-in",
                name,
                packetSender.getName()
        ));

        NodeExecutor.getInstance().getEventManager().callEvent(new PlayerLoginEvent(name));
        DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventPlayerConnected(name));
    }
}
