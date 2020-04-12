package systems.reformcloud.reformcloud2.executor.controller.network.packets.in.query;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;

import java.util.function.Consumer;

public final class ControllerQueryStartProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 31;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        if (packet.content().has("info")) {
            ProcessInformation processInformation = packet.content().get("info", ProcessInformation.TYPE);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(processInformation);
            return;
        }

        ProcessConfiguration configuration = packet.content().get("config", new TypeToken<ProcessConfiguration>() {
        });
        if (configuration == null) {
            return;
        }

        if (packet.content().getBoolean("start")) {
            responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result",
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(configuration))));
        } else {
            responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result",
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().prepareProcess(configuration))));
        }
    }
}
