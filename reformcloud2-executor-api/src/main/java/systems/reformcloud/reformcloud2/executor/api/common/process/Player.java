package systems.reformcloud.reformcloud2.executor.api.common.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.Objects;
import java.util.UUID;

public final class Player implements Comparable<Player>, SerializableObject {

    public Player(@NotNull UUID uniqueID, @NotNull String name) {
        this.uniqueID = uniqueID;
        this.name = name;
    }

    private UUID uniqueID;

    private String name;

    private long joined = System.currentTimeMillis();

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

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.uniqueID);
        buffer.writeString(this.name);
        buffer.writeLong(this.joined);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.uniqueID = buffer.readUniqueId();
        this.name = buffer.readString();
        this.joined = buffer.readLong();
    }
}
