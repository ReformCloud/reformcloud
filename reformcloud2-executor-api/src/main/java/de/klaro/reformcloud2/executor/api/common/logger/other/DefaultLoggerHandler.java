package de.klaro.reformcloud2.executor.api.common.logger.other;

import de.klaro.reformcloud2.executor.api.common.logger.Debugger;
import de.klaro.reformcloud2.executor.api.common.logger.HandlerBase;
import de.klaro.reformcloud2.executor.api.common.logger.LoggerBase;
import de.klaro.reformcloud2.executor.api.common.logger.LoggerLineHandler;
import de.klaro.reformcloud2.executor.api.common.logger.coloured.formatter.LogFileFormatter;
import de.klaro.reformcloud2.executor.api.common.logger.other.debugger.DefaultDebugger;
import de.klaro.reformcloud2.executor.api.common.logger.other.fornatter.DefaultLogFormatter;
import de.klaro.reformcloud2.executor.api.common.logger.other.handler.DefaultConsoleHandler;
import de.klaro.reformcloud2.executor.api.common.logger.stream.OutputStream;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public final class DefaultLoggerHandler extends LoggerBase {

    private static final String PROMPT = System.getProperty("user.name") + "@ReformCloud2~# ";

    public DefaultLoggerHandler() throws IOException {
        this.consoleReader = new ConsoleReader(System.in, System.out);
        this.consoleReader.setExpandEvents(false);

        FileHandler fileHandler = new FileHandler("logs/cloud.log", 70000000, 8, true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new LogFileFormatter(this));
        addHandler(fileHandler);

        HandlerBase consoleHandler = new DefaultConsoleHandler(this);
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new DefaultLogFormatter(this));
        addHandler(consoleHandler);

        System.setOut(new PrintStream(new OutputStream(this, Level.INFO), true));
        System.setErr(new PrintStream(new OutputStream(this, Level.SEVERE), true));
    }

    private final ConsoleReader consoleReader;

    private final Debugger debugger = new DefaultDebugger();

    private final List<LoggerLineHandler> handlers = new ArrayList<>();

    @Override
    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }

    @Override
    public String readLine() {
        try {
            return consoleReader.readLine(PROMPT);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public String readLineNoPrompt() {
        try {
            return consoleReader.readLine();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public String readString(Predicate<String> predicate, Runnable invalidInputMessage) {
        String line = readLine();
        while (line == null || !predicate.test(line)) {
            invalidInputMessage.run();
            line = readLine();
        }

        return line;
    }

    @Override
    public <T> T read(Function<String, T> function, Runnable invalidInputMessage) {
        String line = readLine();
        T result;
        while (line == null || (result = function.apply(line)) == null) {
            invalidInputMessage.run();
            line = readLine();
        }

        return result;
    }

    @Override
    public void log(String message) {
        handleLine(message);

        try {
            consoleReader.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE + message + Ansi.ansi().reset().toString());
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void logRaw(String message) {
        handleLine(message);

        try {
            consoleReader.print(message + Ansi.ansi().reset().toString());
            consoleReader.drawLine();
            consoleReader.flush();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public LoggerBase addLogLineHandler(LoggerLineHandler handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public Debugger getDebugger() {
        return debugger;
    }

    @Override
    public void close() throws Exception {
        consoleReader.drawLine();
        consoleReader.flush();
        consoleReader.close();
    }

    private void handleLine(String line) {
        handlers.forEach(handler -> {
            handler.handleLine(line, DefaultLoggerHandler.this);
            handler.handleRaw(line, DefaultLoggerHandler.this); //Line is always raw because no colour is used
        });
    }
}
