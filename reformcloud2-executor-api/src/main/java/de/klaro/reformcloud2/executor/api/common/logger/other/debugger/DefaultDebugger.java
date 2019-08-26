package de.klaro.reformcloud2.executor.api.common.logger.other.debugger;

import de.klaro.reformcloud2.executor.api.common.logger.Debugger;

public final class DefaultDebugger implements Debugger {

    public DefaultDebugger() {
        debugState = Boolean.getBoolean("reformcloud.executor.debug");
    }

    private boolean debugState;

    @Override
    public void debug(String message) {
        if (debugState) {
            System.out.println(message);
        }
    }

    @Override
    public void toggleDebugState() {
        debugState = !debugState;
    }

    @Override
    public void setDebugState(boolean debugState) {
        this.debugState = debugState;
    }
}
