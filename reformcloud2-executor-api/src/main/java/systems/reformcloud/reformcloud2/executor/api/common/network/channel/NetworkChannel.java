package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public interface NetworkChannel extends DirectIdentifiableChannel {

    /**
     * Sends a packet into the channel
     *
     * @param packet The packet which should get sent
     */
    void sendPacket(@NotNull Object packet);

    /**
     * Sends a packet sync into the channel
     *
     * @param packet The packet which should get sent
     */
    void sendPacketSync(@NotNull Object packet);

    /**
     * Sends many packets into the channel
     *
     * @param packets The packets which should be sent
     */
    void sendPackets(@NotNull Object... packets);

    /**
     * Sends many sync packets into the channel
     *
     * @param packets The packets which should be sent
     */
    void sendPacketsSync(@NotNull Object... packets);

    void sendQueryResult(@Nullable UUID queryUniqueID, @NotNull Packet result);

    /**
     * @return If the current network channel is connected
     */
    boolean isConnected();

    /**
     * @see #isConnected()
     * Closes the current network channel
     */
    void close();
}
