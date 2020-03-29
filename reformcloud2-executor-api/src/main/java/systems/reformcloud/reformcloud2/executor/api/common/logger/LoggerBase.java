package systems.reformcloud.reformcloud2.executor.api.common.logger;

import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.utils.InfoCmp;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class LoggerBase extends Logger implements AutoCloseable {

    public LoggerBase() {
        super("ReformCloudBaseLogger", null);
    }

    /**
     * @return The current console reader of the cloud
     */
    @NotNull
    public abstract LineReader getLineReader();

    /**
     * Clears the screen of the console
     */
    public void clearScreen() {
        getLineReader().getTerminal().puts(InfoCmp.Capability.clear_screen);
        getLineReader().getTerminal().flush();
    }

    /**
     * @return Reads the current line of the console
     */
    @NotNull
    public abstract String readLine();

    /**
     * @return Reads the current line without a prompt
     * @see #readLine()
     */
    @NotNull
    public abstract String readLineNoPrompt();

    /**
     * Reads the console input and waits for the correct input
     *
     * @param predicate           The predicate which should check if the console input is correct
     * @param invalidInputMessage The runnable if the input is invalid it get called
     * @return The string which was given in the console
     */
    @NotNull
    public abstract String readString(
            @NotNull Predicate<String> predicate,
            @NotNull Runnable invalidInputMessage
    );

    /**
     * Reads the console input and waits for the correct input
     *
     * @param function            The function which handles the console input and converts it into the needed object
     * @param invalidInputMessage The runnable if the input is invalid it get called
     * @param <T>                 The type parameter of the current needed object
     * @return The result of {@link Function#apply(Object)} to the given function
     */
    @NotNull
    public abstract <T> T read(
            @NotNull Function<String, T> function,
            @NotNull Runnable invalidInputMessage
    );

    /**
     * Logs a message to the console, also available with {@link System#out}
     *
     * @param message The message which should be logged into the console
     */
    public abstract void log(@NotNull String message);

    /**
     * Logs a message to the console, also available with {@link System#out};
     * This replaces all colour codes in the message before printing it into the console
     *
     * @param message The message which should be logged into the console
     */
    public abstract void logRaw(@NotNull String message);

    /**
     * Adds a log line handler
     *
     * @param handler The handler which should get registered
     * @return The current instance of this class
     */
    @NotNull
    public abstract LoggerBase addLogLineHandler(@NotNull LoggerLineHandler handler);

    /**
     * Removes a specific log line handler
     *
     * @param handler The handler which should get unregistered
     * @return If the log line handler was found and unregistered
     */
    public abstract boolean removeLogLineHandler(@NotNull LoggerLineHandler handler);

    /**
     * Get the debugger of the cloud
     *
     * @return The current {@link Debugger} of the cloud
     */
    @NotNull
    public abstract Debugger getDebugger();

    /**
     * For internal use only
     */
    public abstract void handleReload();
}
