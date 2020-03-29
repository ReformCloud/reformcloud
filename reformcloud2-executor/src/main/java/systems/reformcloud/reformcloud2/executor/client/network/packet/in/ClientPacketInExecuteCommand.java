package systems.reformcloud.reformcloud2.executor.client.network.packet.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;

import java.util.function.Consumer;

public final class ClientPacketInExecuteCommand extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return 48;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        String name = packet.content().getString("name");
        String command = packet.content().getString("command");

        ClientExecutor.getInstance().getProcessManager().getProcess(name).ifPresent(runningProcess -> runningProcess.sendCommand(command));
    }
}
