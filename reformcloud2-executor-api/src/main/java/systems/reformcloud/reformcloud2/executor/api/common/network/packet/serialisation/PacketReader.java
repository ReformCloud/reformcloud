package systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation;

import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.io.ObjectInputStream;

public interface PacketReader {

    @Nonnull
    Packet read(int id, @Nonnull ObjectInputStream inputStream) throws Exception;

}
