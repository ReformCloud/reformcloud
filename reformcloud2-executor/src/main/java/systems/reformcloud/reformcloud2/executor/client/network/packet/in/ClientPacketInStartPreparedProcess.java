package systems.reformcloud.reformcloud2.executor.client.network.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.process.ProcessQueue;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ClientPacketInStartPreparedProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 14;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        ProcessInformation information = packet.content().get("info", ProcessInformation.TYPE);
        if (information != null && information.getProcessState().equals(ProcessState.PREPARED)) {
            ClientExecutor.getInstance().getProcessManager().getProcess(information.getProcessUniqueID()).ifPresent(ProcessQueue::queue);
        }
    }
}
