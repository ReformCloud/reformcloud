package systems.reformcloud.reformcloud2.executor.api.common.network.packet.query;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

public final class QueryRequest<T extends Packet> {

  public QueryRequest() { this.task = new DefaultTask<>(); }

  private final Task<T> task;

  public void onComplete(@Nonnull Consumer<T> consumer) {
    task.thenAccept(consumer);
  }

  public void complete(@Nonnull T result) { this.task.complete(result); }

  @Nonnull
  public Task<T> getTask() {
    return task;
  }
}
