package systems.reformcloud.reformcloud2.executor.api.common.process;

public enum ProcessState {

    PREPARED,

    STARTED,

    READY,

    FULL,

    INVISIBLE,

    STOPPED;

    public boolean isReady() {
        return equals(READY) || equals(FULL) || equals(INVISIBLE);
    }
}
