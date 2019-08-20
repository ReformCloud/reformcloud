package de.klaro.reformcloud2.executor.api.common.process;

public enum ProcessState {

    PREPARED,

    STARTED,

    FULL,

    INVISIBLE,

    STOPPED;

    public boolean isJoineAble() {
        return equals(FULL) || equals(INVISIBLE) || equals(STARTED);
    }
}
