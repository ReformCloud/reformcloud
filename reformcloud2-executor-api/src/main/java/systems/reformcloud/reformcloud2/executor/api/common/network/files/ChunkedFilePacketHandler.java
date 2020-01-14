package systems.reformcloud.reformcloud2.executor.api.common.network.files;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.io.ObjectInputStream;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class ChunkedFilePacketHandler implements NetworkHandler {

    @Override
    public final int getHandlingPacketID() {
        return NetworkUtil.FILE_BUS + 1;
    }

    @Override
    public final void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        Conditions.isTrue(packet instanceof ChunkedFilePacket);

        ChunkedFilePacket chunkedFilePacket = (ChunkedFilePacket) packet;
        this.handleChunkPacketReceive(
                chunkedFilePacket.getUniqueID(),
                chunkedFilePacket.getPath(),
                chunkedFilePacket.getReadLength(),
                chunkedFilePacket.getCurrent()
        );
    }

    @Nonnull
    @Override
    public Packet read(int id, @Nonnull ObjectInputStream inputStream) throws Exception {
        UUID uniqueID = UUID.fromString(inputStream.readUTF());
        String path = inputStream.readUTF();
        int length = inputStream.readInt();
        byte[] current = (byte[]) inputStream.readObject();

        return new ChunkedFilePacket(uniqueID, path, length, current);
    }

    public abstract void handleChunkPacketReceive(UUID operationID, String path, int length, byte[] bytes);
}
