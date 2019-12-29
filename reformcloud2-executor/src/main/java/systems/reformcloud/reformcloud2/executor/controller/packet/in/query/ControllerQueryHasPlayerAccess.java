package systems.reformcloud.reformcloud2.executor.controller.packet.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.join.OnlyProxyJoinHelper;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerQueryHasPlayerAccess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 4;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uuid", UUID.class);
        String name = packet.content().getString("name");

        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("access", OnlyProxyJoinHelper.walkedOverProxy(uuid, name))));
    }
}
