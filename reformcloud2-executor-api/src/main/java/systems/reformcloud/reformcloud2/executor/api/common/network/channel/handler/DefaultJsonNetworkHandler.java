package systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class DefaultJsonNetworkHandler implements NetworkHandler {

    public DefaultJsonNetworkHandler() {
        this(Integer.MIN_VALUE);
    }

    public DefaultJsonNetworkHandler(int id) {
        this.id = id;
    }

    private final int id;

    @Override
    public int getHandlingPacketID() {
        return id;
    }

    @Nonnull
    @Override
    public Packet read(int id, @Nonnull ObjectInputStream inputStream) throws Exception {
        return readPacket(id, inputStream);
    }

    public static Packet readPacket(int id, @Nonnull ObjectInputStream inputStream) throws Exception {
        String uid = inputStream.readUTF();
        JsonConfiguration content = read((byte[]) inputStream.readObject());
        byte[] extra = (byte[]) inputStream.readObject();

        return new JsonPacket(id, content, "null".equals(uid) ? null : UUID.fromString(uid), extra);
    }

    private static JsonConfiguration read(byte[] bytes) {
        return new JsonConfiguration(new String(bytes, StandardCharsets.UTF_8));
    }
}
