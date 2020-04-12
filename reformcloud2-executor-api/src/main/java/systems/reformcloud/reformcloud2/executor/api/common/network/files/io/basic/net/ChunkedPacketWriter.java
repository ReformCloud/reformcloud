package systems.reformcloud.reformcloud2.executor.api.common.network.files.io.basic.net;

import io.netty.channel.ChannelFutureListener;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.files.ChunkedFilePacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.files.io.basic.ChunkedFileReader;

import java.nio.file.Paths;
import java.util.UUID;

public class ChunkedPacketWriter extends ChunkedFileReader {

    public ChunkedPacketWriter(@NotNull String path, @NotNull UUID operationID, @NotNull PacketSender packetSender) {
        super(Paths.get(path));
        this.packetSender = packetSender;
        this.path = path;
        this.operationID = operationID;
    }

    private final PacketSender packetSender;

    private final UUID operationID;

    private final String path;

    public void startRead() {
        byte[] current = newByteArray();
        int result = read(current);
        if (result == -1) {
            return;
        }

        this.readUntilEnd(current, result);
    }

    private void readUntilEnd(byte[] startArray, int length) {
        packetSender.getChannelContext().channel().writeAndFlush(new ChunkedFilePacket(
                operationID, path, length, startArray
        )).addListener((ChannelFutureListener) channelFuture -> {
            if (length == -1) {
                return;
            }

            byte[] bytes = newByteArray();
            int result = read(bytes);
            ChunkedPacketWriter.this.readUntilEnd(bytes, result);
        });
    }
}
