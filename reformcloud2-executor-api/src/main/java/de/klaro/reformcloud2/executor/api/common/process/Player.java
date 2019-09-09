package de.klaro.reformcloud2.executor.api.common.process;

import java.util.UUID;

public final class Player implements Comparable {

    Player(UUID uniqueID, String name) {
        this.uniqueID = uniqueID;
        this.name = name;
    }

    private final UUID uniqueID;

    private final String name;

    private final long joined = System.currentTimeMillis();

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public long getJoined() {
        return joined;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Player) {
            Player player = (Player) o;
            return Long.compare(player.getJoined(), getJoined());
        }

        return 0;
    }
}
