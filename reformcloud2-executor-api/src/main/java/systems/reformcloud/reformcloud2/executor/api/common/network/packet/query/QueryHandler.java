package systems.reformcloud.reformcloud2.executor.api.common.network.packet.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.UUID;

public interface QueryHandler {

    /**
     * Tries to get the waiting query of the given id
     *
     * @param queryUniqueId The id of the query
     * @return The waiting query request or {@code null} if no such request is known
     */
    @Nullable
    Task<? extends QueryPacket> getWaitingQuery(@NotNull UUID queryUniqueId);

    /**
     * Checks if a id has a waiting query
     *
     * @param queryUniqueId The id of the query
     * @return If the id has a waiting query
     */
    boolean hasWaitingQuery(@NotNull UUID queryUniqueId);

    /**
     * Sends a query async to a packet sender
     * Note: It's not needed to give the packet as a query packet, because the cloud is going to convert it internal
     *
     * @param sender The sender who should receive the packet
     * @param packet The packet itself which will be converted to q query packet
     * @return The query request which got created
     */
    @NotNull
    <T extends QueryPacket> Task<T> sendQueryAsync(@NotNull PacketSender sender, @NotNull T packet);

    /**
     * Clears all waiting queries
     */
    void clearQueries();
}
