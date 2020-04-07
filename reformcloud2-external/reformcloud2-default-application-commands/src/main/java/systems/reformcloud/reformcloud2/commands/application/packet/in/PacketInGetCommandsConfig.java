package systems.reformcloud.reformcloud2.commands.application.packet.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.application.ReformCloudApplication;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public class PacketInGetCommandsConfig extends DefaultJsonNetworkHandler {

    public PacketInGetCommandsConfig() {
        super(NetworkUtil.EXTERNAL_BUS + 1);
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("content", ReformCloudApplication.getCommandsConfig())));
    }
}
