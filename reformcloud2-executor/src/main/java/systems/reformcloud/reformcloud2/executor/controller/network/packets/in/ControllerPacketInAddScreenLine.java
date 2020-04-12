package systems.reformcloud.reformcloud2.executor.controller.network.packets.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerPacketInAddScreenLine extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 9;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        String line = packet.content().getString("line");
        UUID uuid = packet.content().get("uuid", UUID.class);

        ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uuid);
        if (processInformation != null) {
            System.out.println(LanguageManager.get("screen-line-added", processInformation.getProcessDetail().getName(), line));
        }
    }
}
