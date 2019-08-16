package de.klaro.reformcloud2.executor.api.common.restapi;

import de.klaro.reformcloud2.executor.api.common.utility.operation.Operation;

import java.util.UUID;

public final class HttpOperation implements Operation {

    private boolean done = false;

    private final UUID uuid = UUID.randomUUID();

    @Override
    public UUID identifier() {
        return uuid;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void complete() {
        done = true;
    }
}
