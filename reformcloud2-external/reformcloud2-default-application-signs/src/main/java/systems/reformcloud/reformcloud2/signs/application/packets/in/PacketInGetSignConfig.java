package systems.reformcloud.reformcloud2.signs.application.packets.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.application.ReformCloudApplication;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;

import java.util.function.Consumer;

public class PacketInGetSignConfig extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return PacketUtil.SIGN_BUS + 3;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("config", ReformCloudApplication.getSignConfig())));
    }
}
