package systems.reformcloud.reformcloud2.executor.api.common.process;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public final class Player implements Comparable<Player> {

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
    public int compareTo(@Nonnull Player o) {
        return Long.compare(getJoined(), o.getJoined());
    }
}
