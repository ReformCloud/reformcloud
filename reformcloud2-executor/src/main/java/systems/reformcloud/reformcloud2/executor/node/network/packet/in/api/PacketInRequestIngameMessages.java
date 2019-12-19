package systems.reformcloud.reformcloud2.executor.node.network.packet.in.api;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.function.Consumer;

public class PacketInRequestIngameMessages implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_QUERY_BUS + 2;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        responses.accept(new DefaultPacket(-1, new JsonConfiguration()
                .add("messages", NodeExecutor.getInstance().getNodeExecutorConfig().getIngameMessages())
        ));
    }
}
