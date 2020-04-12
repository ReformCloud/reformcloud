package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.function.Consumer;

public class PacketInConnectionInitDone extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 18;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        NodeInformation information = packet.content().get("info", NodeInformation.TYPE);
        if (information == null) {
            return;
        }

        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getClusterManager().handleConnect(
                NodeExecutor.getInstance().getNodeNetworkManager().getCluster(),
                information
        );
        NodeExecutor.getInstance().getClusterSyncManager().getWaitingConnections().remove(packetSender.getAddress());
        NodeExecutor.getInstance().sync(packetSender.getName());

        System.out.println(LanguageManager.get("network-node-other-node-connected", information.getName(), packetSender.getAddress()));
    }
}
