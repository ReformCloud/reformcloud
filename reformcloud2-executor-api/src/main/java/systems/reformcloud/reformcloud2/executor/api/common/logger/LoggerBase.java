package systems.reformcloud.reformcloud2.executor.api.common.logger;

import jline.console.ConsoleReader;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.IOException;
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
    @Nonnull
    public abstract ConsoleReader getConsoleReader();

    /**
     * Clears the screen of the console
     */
    public void clearScreen() {
        try {
            getConsoleReader().clearScreen();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return Reads the current line of the console
     */
    @Nonnull
    public abstract String readLine();

    /**
     * @see #readLine()
     * @return Reads the current line without a prompt
     */
    @Nonnull
    public abstract String readLineNoPrompt();

    /**
     * Reads the console input and waits for the correct input
     *
     * @param predicate The predicate which should check if the console input is correct
     * @param invalidInputMessage The runnable if the input is invalid it get called
     * @return The string which was given in the console
     */
    @Nonnull
    public abstract String readString(
            @Nonnull Predicate<String> predicate,
            @Nonnull Runnable invalidInputMessage
    );

    /**
     * Reads the console input and waits for the correct input
     *
     * @param function The function which handles the console input and converts it into the needed object
     * @param invalidInputMessage The runnable if the input is invalid it get called
     * @param <T> The type parameter of the current needed object
     * @return The result of {@link Function#apply(Object)} to the given function
     */
    @Nonnull
    @CheckReturnValue
    public abstract <T> T read(
            @Nonnull Function<String, T> function,
            @Nonnull Runnable invalidInputMessage
    );

    /**
     * Logs a message to the console, also available with {@link System#out}
     *
     * @param message The message which should be logged into the console
     */
    public abstract void log(@Nonnull String message);

    /**
     * Logs a message to the console, also available with {@link System#out};
     * This replaces all colour codes in the message before printing it into the console
     *
     * @param message The message which should be logged into the console
     */
    public abstract void logRaw(@Nonnull String message);

    /**
     * Adds a log line handler
     *
     * @param handler The handler which should get registered
     * @return The current instance of this class
     */
    @Nonnull
    public abstract LoggerBase addLogLineHandler(@Nonnull LoggerLineHandler handler);

    /**
     * Get the debugger of the cloud
     *
     * @return The current {@link Debugger} of the cloud
     */
    @Nonnull
    public abstract Debugger getDebugger();

    /**
     * For internal use only
     */
    public abstract void handleReload();
}
