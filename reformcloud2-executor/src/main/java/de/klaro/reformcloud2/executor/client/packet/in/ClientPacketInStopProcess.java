package de.klaro.reformcloud2.executor.client.packet.in;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.client.ClientExecutor;

import java.util.UUID;
import java.util.function.Consumer;

public final class ClientPacketInStopProcess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_PACKETS + 3;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        UUID uniqueID = packet.content().get("uniqueID", UUID.class);
        ClientExecutor.getInstance().getProcessManager().getProcess(uniqueID).ifPresent(new Consumer<RunningProcess>() {
            @Override
            public void accept(RunningProcess runningProcess) {
                runningProcess.shutdown();
            }
        });
    }
}
