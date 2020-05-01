package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DefaultQueryHandler implements QueryHandler {

    private final Map<UUID, Task<Packet>> waiting = new HashMap<>();

    @Override
    public Task<Packet> getWaitingQuery(@NotNull UUID uuid) {
        return waiting.remove(uuid);
    }

    @Override
    public boolean hasWaitingQuery(@NotNull UUID uuid) {
        return waiting.containsKey(uuid);
    }

    @NotNull
    @Override
    public Task<Packet> sendQueryAsync(@NotNull PacketSender sender, @NotNull Packet packet) {
        return this.sendQueryAsync(sender, UUID.randomUUID(), packet);
    }

    @NotNull
    @Override
    public Task<Packet> sendQueryAsync(@NotNull PacketSender sender, @NotNull UUID queryUniqueID, @NotNull Packet packet) {
        Task<Packet> task = new DefaultTask<>();

        this.waiting.put(queryUniqueID, task);
        packet.setQueryUniqueID(queryUniqueID);

        System.out.println("Sending query packet with id " + packet.getId() + " " + packet.getClass().getName()); // TODO: remove

        sender.sendPacket(packet);
        return task;
    }

    @Override
    public void sendQueryResultAsync(@NotNull PacketSender sender, @NotNull UUID queryUniqueID, @NotNull Packet packet) {
        packet.setQueryUniqueID(queryUniqueID);
        sender.sendPacket(packet);
    }

    @Override
    public void clearQueries() {
        this.waiting.clear();
    }
}
