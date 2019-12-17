package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import java.util.UUID;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

public class PlayerLogoutEvent extends Event {

  public PlayerLogoutEvent(@Nonnull String name, @Nonnull UUID uuid) {
    this.name = name;
    this.uuid = uuid;
  }

  private final String name;

  private final UUID uuid;

  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public UUID getUuid() {
    return uuid;
  }
}
