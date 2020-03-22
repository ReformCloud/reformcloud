package systems.reformcloud.reformcloud2.executor.node.network.packet.query.in;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class NodePacketInQueryStartProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_QUERY_BUS + 1;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        ProcessConfiguration configuration = packet.content().get("config", new TypeToken<ProcessConfiguration>() {
        });
        if (configuration == null) {
            return;
        }

        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", NodeExecutor.getInstance().getNodeNetworkManager().prepareProcess(
                configuration, packet.content().getBoolean("start")
        ))));
    }
}
