package systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation;

import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.io.ObjectInputStream;

public interface PacketReader {

    /**
     * Reads the given input stream and converts it to a packet
     *
     * @param id The id of the packet which is incoming
     * @param inputStream The stream from which the serialized data can get read
     * @return The de-serialized packet from the stream
     * @throws Exception If an exception occurs during the read
     */
    @Nonnull
    Packet read(int id, @Nonnull ObjectInputStream inputStream) throws Exception;
}
