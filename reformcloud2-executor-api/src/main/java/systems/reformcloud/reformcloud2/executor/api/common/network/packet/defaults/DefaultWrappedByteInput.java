package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.network.packet.WrappedByteInput;

import javax.annotation.Nonnull;

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

    @Nonnull
    @Override
    public byte[] getContent() {
        return this.bytes;
    }
}
