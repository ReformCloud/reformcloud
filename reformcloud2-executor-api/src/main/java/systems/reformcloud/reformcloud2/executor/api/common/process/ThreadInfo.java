package systems.reformcloud.reformcloud2.executor.api.common.process;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public final class ThreadInfo implements SerializableObject {

    @ApiStatus.Internal
    public ThreadInfo() {
    }

    private ThreadInfo(String name, long id, int priority, boolean daemon, Thread.State state) {
        this.name = name;
        this.id = id;
        this.priority = priority;
        this.daemon = daemon;
        this.state = state;
    }

    private String name;

    private long id;

    private int priority;

    private boolean daemon;

    private Thread.State state;

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public Thread.State getState() {
        return state;
    }

    public static ThreadInfo create(Thread thread) {
        return new ThreadInfo(thread.getName(), thread.getId(), thread.getPriority(), thread.isDaemon(), thread.getState());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeLong(this.id);
        buffer.writeInt(this.priority);
        buffer.writeBoolean(this.daemon);
        buffer.writeVarInt(this.state.ordinal());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.id = buffer.readLong();
        this.priority = buffer.readInt();
        this.daemon = buffer.readBoolean();
        this.state = Thread.State.values()[buffer.readVarInt()];
    }
}
