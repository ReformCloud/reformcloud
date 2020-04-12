package systems.reformcloud.reformcloud2.executor.api.common.process;

public enum ProcessState {

    CREATED,

    PREPARED,

    READY_TO_START,

    STARTED,

    READY,

    FULL,

    INVISIBLE,

    STOPPED;

    public boolean isValid() {
        return equals(CREATED) || equals(READY_TO_START) || equals(STARTED) || equals(READY) || equals(FULL) || equals(INVISIBLE);
    }

    public boolean isReady() {
        return equals(READY) || equals(FULL) || equals(INVISIBLE);
    }
}
