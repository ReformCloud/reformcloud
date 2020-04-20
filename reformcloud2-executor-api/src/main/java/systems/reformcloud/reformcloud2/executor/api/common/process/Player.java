package systems.reformcloud.reformcloud2.executor.api.common.process;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public final class Player implements Comparable<Player> {

    public Player(@NotNull UUID uniqueID, @NotNull String name) {
        this.uniqueID = uniqueID;
        this.name = name;
    }

    private final UUID uniqueID;

    private final String name;

    private final long joined = System.currentTimeMillis();

    @NotNull
    public UUID getUniqueID() {
        return uniqueID;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public long getJoined() {
        return joined;
    }

    @Override
    public int compareTo(@NotNull Player o) {
        return Long.compare(getJoined(), o.getJoined());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return getUniqueID().equals(player.getUniqueID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniqueID());
    }
}
