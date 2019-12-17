package systems.reformcloud.reformcloud2.executor.api.common.restapi;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.utility.operation.Operation;

public final class HttpOperation implements Operation {

  private boolean done = false;

  private final UUID uuid = UUID.randomUUID();

  @Override
  public UUID identifier() {
    return uuid;
  }

  @Override
  public boolean isDone() {
    return done;
  }

  @Override
  public void complete() {
    done = true;
  }
}
