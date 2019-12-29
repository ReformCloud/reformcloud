package systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectOutputStream;

public interface PacketWriter {

    void write(@Nonnull ObjectOutputStream objectOutputStream) throws IOException;

}
