package systems.reformcloud.reformcloud2.executor.controller.packet.in;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public class ControllerPacketInScreenEnabled implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 13;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        UUID uniqueID = packet.content().get("uniqueID", UUID.class);
        Collection<String> lines = packet.content().get("lines", new TypeToken<Collection<String>>() {});

        ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uniqueID);
        if (processInformation != null) {
            lines.forEach(line -> System.out.println(LanguageManager.get("screen-line-added", processInformation.getName(), line)));
        }
    }
}
