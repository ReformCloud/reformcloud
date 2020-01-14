package systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectOutputStream;

public interface PacketWriter {

    /**
     * Writes all needed information of a packet into a stream
     *
     * @param objectOutputStream The stream in which the data should get written
     * @throws IOException Catches all exceptions occurring while stream write
     */
    void write(@Nonnull ObjectOutputStream objectOutputStream) throws IOException;

}
