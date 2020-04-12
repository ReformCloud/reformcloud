package systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;

public interface PacketWriter {

    /**
     * Writes all needed information of a packet into a stream
     *
     * @param objectOutputStream The stream in which the data should get written
     * @throws IOException Catches all exceptions occurring while stream write
     */
    void write(@NotNull ObjectOutputStream objectOutputStream) throws IOException;

}
