package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import java.util.Optional;
import java.util.UUID;

public class PlayerLogoutEvent extends Event {

    public PlayerLogoutEvent(@NotNull String name, @NotNull UUID uuid, @Nullable String lastServer) {
        this.name = name;
        this.uuid = uuid;
        this.lastServer = lastServer;
    }

    private final String name;

    private final UUID uuid;

    private final String lastServer;

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @NotNull
    public Optional<String> getLastServer() {
        return Optional.ofNullable(this.lastServer);
    }
}
