package systems.reformcloud.reformcloud2.executor.api.common.process;

import javax.annotation.Nonnull;
import java.util.UUID;

public final class Player implements Comparable {

    Player(@Nonnull UUID uniqueID, @Nonnull String name) {
        this.uniqueID = uniqueID;
        this.name = name;
    }

    private final UUID uniqueID;

    private final String name;

    private final long joined = System.currentTimeMillis();

    @Nonnull
    public UUID getUniqueID() {
        return uniqueID;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public long getJoined() {
        return joined;
    }

    @Override
    public int compareTo(@Nonnull Object o) {
        if (o instanceof Player) {
            Player player = (Player) o;
            return Long.compare(player.getJoined(), getJoined());
        }

        return 0;
    }
}
