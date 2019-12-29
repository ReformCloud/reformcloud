package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public interface WrappedByteInput {

    int getPacketID();

    @Nonnull
    byte[] getContent();

    @Nonnull
    default ObjectInputStream toObjectStream() throws IOException {
        return new ObjectInputStream(new ByteArrayInputStream(getContent()));
    }
}
