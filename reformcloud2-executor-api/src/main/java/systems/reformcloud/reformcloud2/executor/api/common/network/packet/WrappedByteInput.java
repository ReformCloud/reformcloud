package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public interface WrappedByteInput {

    int getPacketID();

    @NotNull
    byte[] getContent();

    @NotNull
    default ObjectInputStream toObjectStream() throws IOException {
        return new ObjectInputStream(new ByteArrayInputStream(getContent()));
    }
}
