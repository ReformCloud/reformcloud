package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryPacket;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DefaultQueryHandler implements QueryHandler {

    private final Map<UUID, Task<? extends QueryPacket>> waiting = new HashMap<>();

    @Override
    public Task<? extends QueryPacket> getWaitingQuery(@NotNull UUID uuid) {
        return waiting.remove(uuid);
    }

    @Override
    public boolean hasWaitingQuery(@NotNull UUID uuid) {
        return waiting.containsKey(uuid);
    }

    @NotNull
    @Override
    public <T extends QueryPacket> Task<T> sendQueryAsync(@NotNull PacketSender sender, @NotNull T packet) {
        Task<T> task = new DefaultTask<>();

        this.waiting.put(packet.getQueryUniqueId(), task);
        sender.sendPacket(packet);

        return task;
    }

    @Override
    public void clearQueries() {
        this.waiting.clear();
    }
}
