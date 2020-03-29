package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation.PacketWriter;

import java.util.UUID;

public interface Packet extends PacketWriter {

    /**
     * @return The packet id of the packet
     */
    int packetID();

    /**
     * @return The id of the query or {@code null} if the packet is not a query
     */
    @Nullable
    UUID queryUniqueID();

    /**
     * @return The content of the packet
     */
    @NotNull
    JsonConfiguration content();

    /**
     * @return The extra content of this packet
     */
    @NotNull
    byte[] extra();

    /**
     * Sets the query id
     *
     * @param id The new id which should get used
     */
    void setQueryID(UUID id);

    /**
     * Sets the content of the packet
     *
     * @param content The new content which should get used
     */
    void setContent(JsonConfiguration content);
}
