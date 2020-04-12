package systems.reformcloud.reformcloud2.executor.client.network.packet.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.process.ProcessQueue;
import systems.reformcloud.reformcloud2.executor.client.process.basic.DefaultRunningProcess;

import java.util.function.Consumer;

public final class ClientPacketInStartProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 2;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        ProcessInformation processInformation = packet.content().get("info", ProcessInformation.TYPE);
        if (packet.content().getBoolean("start")) {
            ProcessQueue.queue(processInformation);
        } else {
            RunningProcess process = new DefaultRunningProcess(processInformation);
            process.prepare();
            ClientExecutor.getInstance().getProcessManager().registerProcess(process);
        }
    }
}
