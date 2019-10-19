package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.function.Consumer;

public class PacketInNodeQueueProcess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 11;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        ProcessInformation information = packet.content().get("info", ProcessInformation.TYPE);
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().queueLocal(information);
    }
}
