package systems.reformcloud.reformcloud2.permissions.util.events.user;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

public class PermissionUserDeleteEvent extends Event {

  public PermissionUserDeleteEvent(UUID uuid) { this.uuid = uuid; }

  private final UUID uuid;

  public UUID getUuid() { return uuid; }
}
