package systems.reformcloud.reformcloud2.executor.api.common.process;

public final class ThreadInfo {

    private ThreadInfo(String name, long id, int priority, boolean daemon, Thread.State state) {
        this.name = name;
        this.id = id;
        this.priority = priority;
        this.daemon = daemon;
        this.state = state;
    }

    private final String name;

    private final long id;

    private final int priority;

    private final boolean daemon;

    private final Thread.State state;

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
}
