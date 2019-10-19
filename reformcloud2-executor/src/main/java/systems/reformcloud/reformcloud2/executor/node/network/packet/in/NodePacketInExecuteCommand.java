package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import java.util.function.Consumer;

public class NodePacketInExecuteCommand implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 14;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String name = packet.content().getString("name");
        String command = packet.content().getString("command");

        LocalNodeProcess nodeProcess = Links.filterToReference(LocalProcessManager.getNodeProcesses(), e -> e.getProcessInformation().getName().equals(name)).orNothing();
        if (nodeProcess == null) {
            return;
        }

        nodeProcess.sendCommand(command);
    }
}
