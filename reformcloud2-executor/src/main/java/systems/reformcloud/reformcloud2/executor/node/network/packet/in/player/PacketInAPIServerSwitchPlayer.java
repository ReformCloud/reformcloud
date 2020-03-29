package systems.reformcloud.reformcloud2.executor.node.network.packet.in.player;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerServerSwitchEvent;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventPlayerServerSwitch;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;

import java.util.UUID;
import java.util.function.Consumer;

public class PacketInAPIServerSwitchPlayer extends DefaultJsonNetworkHandler {

    public PacketInAPIServerSwitchPlayer() {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 12);
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uuid", UUID.class);
        String target = packet.content().getString("target");

        NodeExecutor.getInstance().getEventManager().callEvent(new PlayerServerSwitchEvent(uuid, target));
        DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventPlayerServerSwitch(uuid, target));
    }
}
