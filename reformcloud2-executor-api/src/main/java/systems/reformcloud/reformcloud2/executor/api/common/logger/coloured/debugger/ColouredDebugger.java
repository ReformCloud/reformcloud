package systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.debugger;

import systems.reformcloud.reformcloud2.executor.api.common.logger.Debugger;

public final class ColouredDebugger implements Debugger {

    public ColouredDebugger() {
        debugState = Boolean.getBoolean("reformcloud.executor.debug");
    }

    private boolean debugState;

    @Override
    public void debug(String message) {
        if (debugState) {
            System.out.println("&b[DEBUG] " + message);
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
