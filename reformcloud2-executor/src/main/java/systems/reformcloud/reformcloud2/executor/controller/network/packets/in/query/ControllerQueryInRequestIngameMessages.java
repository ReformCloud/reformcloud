package systems.reformcloud.reformcloud2.executor.controller.network.packets.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ControllerQueryInRequestIngameMessages extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_QUERY_BUS + 2;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        responses.accept(new JsonPacket(-1, new JsonConfiguration()
                .add("messages", ControllerExecutor.getInstance().getControllerExecutorConfig().getIngameMessages())
        ));
    }
}
