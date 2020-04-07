package systems.reformcloud.reformcloud2.executor.api.common.logger;

import org.jetbrains.annotations.NotNull;

public interface LoggerLineHandler {

    /**
     * Gets called when the console should handle a non-raw message
     *
     * @param line The current log line which got printed
     * @param base The logger base which printed the line
     */
    default void handleLine(@NotNull String line, @NotNull LoggerBase base) {
    }

    /**
     * Gets called when the console should handle a raw message
     *
     * @param line The current log line which got printed
     * @param base The logger base which printed the line
     */
    default void handleRaw(@NotNull String line, @NotNull LoggerBase base) {
    }
}
