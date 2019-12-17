package systems.reformcloud.reformcloud2.executor.node.network.packet.query.in;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.function.Consumer;

public class NodePacketInQueryStartProcess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_QUERY_BUS + 1;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        ProcessGroup group = packet.content().get("group", ProcessGroup.TYPE);
        Template template = packet.content().get("template", Template.TYPE);
        JsonConfiguration data = packet.content().get("data");

        responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", NodeExecutor.getInstance().getNodeNetworkManager().startProcess(
                group, template, data
        ))));
    }
}
