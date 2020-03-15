package systems.reformcloud.reformcloud2.executor.node.network.packet.query.in;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
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
        ProcessGroup group = packet.content().get("group", ProcessGroup.TYPE);
        Template template = packet.content().get("template", Template.TYPE);
        JsonConfiguration data = packet.content().get("data");

        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", NodeExecutor.getInstance().getNodeNetworkManager().prepareProcess(
                group, template, data, packet.content().getBoolean("start")
        ))));
    }
}
