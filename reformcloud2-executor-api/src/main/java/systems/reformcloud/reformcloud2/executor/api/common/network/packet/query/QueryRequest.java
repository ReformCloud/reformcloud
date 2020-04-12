package systems.reformcloud.reformcloud2.executor.api.common.network.packet.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.function.Consumer;

public final class QueryRequest<T extends Packet> {

    public QueryRequest() {
        this.task = new DefaultTask<>();
    }

    private final Task<T> task;

    public void onComplete(@NotNull Consumer<T> consumer) {
        task.thenAccept(consumer);
    }

    public void complete(@NotNull T result) {
        this.task.complete(result);
    }

    @NotNull
    public Task<T> getTask() {
        return task;
    }
}
