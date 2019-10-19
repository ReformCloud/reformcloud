package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import java.util.UUID;
import java.util.function.Consumer;

public class PacketInNodeStopProcess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 1;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        LocalNodeProcess information;
        if (packet.content().has("name")) {
            String name = packet.content().getString("name");
            information = Links.filterToReference(LocalProcessManager.getNodeProcesses(),
                    e -> e.getProcessInformation().getName().equals(name)).orNothing();
        } else {
            UUID uniqueID = packet.content().get("uniqueID", UUID.class);
            information = Links.filterToReference(LocalProcessManager.getNodeProcesses(),
                    e -> e.getProcessInformation().getProcessUniqueID().equals(uniqueID)).orNothing();
        }

        if (information == null) {
            return;
        }

        information.shutdown();
    }
}
