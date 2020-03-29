package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import java.util.UUID;

public class PlayerLogoutEvent extends Event {

    public PlayerLogoutEvent(@NotNull String name, @NotNull UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    private final String name;

    private final UUID uuid;

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }
}
