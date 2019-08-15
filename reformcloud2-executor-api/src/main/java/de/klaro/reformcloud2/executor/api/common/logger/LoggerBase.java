package de.klaro.reformcloud2.executor.api.common.logger;

import jline.console.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class LoggerBase extends Logger implements AutoCloseable {

    public LoggerBase() throws IOException {
        super("ReformCloudBaseLogger", null);

        AnsiConsole.systemInstall();
        if (!Files.exists(Paths.get("logs"))) {
            Files.createDirectories(Paths.get("logs"));
        }
    }

    public abstract ConsoleReader getConsoleReader();

    public void clearScreen() {
        try {
            getConsoleReader().clearScreen();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public abstract String readLine();

    public abstract String readString(Predicate<String> predicate, Runnable invalidInputMessage);

    public abstract <T> T read(Function<String, T> function, Runnable invalidInputMessage);

    public abstract void log(String message);

    public abstract void logRaw(String message);

    public abstract LoggerBase addLogLineHandler(LoggerLineHandler handler);

    public abstract Debugger getDebugger();
}
