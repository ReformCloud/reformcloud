package systems.reformcloud.reformcloud2.signs.application.packets.in;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.application.ReformCloudApplication;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PacketInGetSignConfig implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return PacketUtil.SIGN_BUS + 3;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("config", ReformCloudApplication.getSignConfig())));
    }
}
