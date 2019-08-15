package de.klaro.reformcloud2.executor.api.common.logger;

public interface Debugger {

    void debug(String message);

    void toggleDebugState();

    void setDebugState(boolean debugState);
}
