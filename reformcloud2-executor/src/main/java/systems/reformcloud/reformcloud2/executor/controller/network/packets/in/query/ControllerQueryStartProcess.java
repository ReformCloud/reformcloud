package systems.reformcloud.reformcloud2.executor.controller.network.packets.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class ControllerQueryStartProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 31;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        if (packet.content().has("info")) {
            ProcessInformation processInformation = packet.content().get("info", ProcessInformation.TYPE);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(processInformation);
            return;
        }

        String group = packet.content().getString("group");
        String template = packet.content().getString("template");
        JsonConfiguration extra = packet.content().get("extra");
        if (packet.content().getBoolean("start")) {
            responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(group, template, extra))));
        } else {
            responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().prepareProcess(group, template, extra))));
        }
    }
}
