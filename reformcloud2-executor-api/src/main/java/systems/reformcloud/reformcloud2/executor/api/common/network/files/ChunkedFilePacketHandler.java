package systems.reformcloud.reformcloud2.executor.api.common.network.files;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class ChunkedFilePacketHandler implements NetworkHandler {

    @Override
    public final int getHandlingPacketID() {
        return NetworkUtil.FILE_BUS + 1;
    }

    @Override
    public final void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        this.handleChunkPacketReceive(
                packet.content().get("key", UUID.class),
                packet.content().getString("path"),
                packet.content().getInteger("length"),
                packet.extra()
        );
    }

    public abstract void handleChunkPacketReceive(UUID operationID, String path, int length, byte[] bytes);
}
