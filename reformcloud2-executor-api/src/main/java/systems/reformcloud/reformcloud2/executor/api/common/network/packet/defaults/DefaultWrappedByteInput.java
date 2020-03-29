package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.WrappedByteInput;

public class DefaultWrappedByteInput implements WrappedByteInput {

    public DefaultWrappedByteInput(int id, byte[] bytes) {
        this.id = id;
        this.bytes = bytes;
    }

    private final int id;

    private final byte[] bytes;

    @Override
    public int getPacketID() {
        return this.id;
    }

    @NotNull
    @Override
    public byte[] getContent() {
        return this.bytes;
    }
}
