package de.klaro.reformcloud2.executor.api.common.event.priority;

public enum EventPriority {

    FIRST((byte) -64),

    SECOND((byte) -32),

    MONITOR((byte) -1),

    NORMAL((byte) 0),

    PENULTIMATE((byte) 32),

    LAST((byte) 64);

    EventPriority(byte priorityInJava) {
        this.priority = priorityInJava;
    }

    private final byte priority;

    public byte getPriority() {
        return priority;
    }
}
