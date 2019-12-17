package systems.reformcloud.reformcloud2.executor.api.common.logger;

public interface Debugger {

    /**
     * @param message Debugs a message to the log file if the debug state is {@code true}
     */
    void debug(String message);

    /**
     * Toggles the current debug state ({@code true} to {@code false} / {@code false} to {@code true})
     */
    void toggleDebugState();

    /**
     * Sets the current debug state
     *
     * @param debugState The new debug state which should be used
     */
    void setDebugState(boolean debugState);
}
