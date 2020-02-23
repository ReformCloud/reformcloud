package systems.reformcloud.reformcloud2.executor.api.common.logger.other;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.logger.Debugger;
import systems.reformcloud.reformcloud2.executor.api.common.logger.HandlerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerLineHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.formatter.LogFileFormatter;
import systems.reformcloud.reformcloud2.executor.api.common.logger.completer.JLine3Completer;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.debugger.DefaultDebugger;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.fornatter.DefaultLogFormatter;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.handler.DefaultConsoleHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.stream.OutputStream;
import systems.reformcloud.reformcloud2.executor.api.common.logger.terminal.TerminalLineHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public final class DefaultLoggerHandler extends LoggerBase {

    private static String prompt = StringUtil.getConsolePrompt();

    public DefaultLoggerHandler(@Nonnull CommandManager commandManager) throws IOException {
        Terminal terminal = TerminalLineHandler.newTerminal(false);
        this.lineReader = TerminalLineHandler.newLineReader(terminal, new JLine3Completer(commandManager));

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

    private final LineReader lineReader;

    private final Debugger debugger = new DefaultDebugger();

    private final List<LoggerLineHandler> handlers = new ArrayList<>();

    @Nonnull
    @Override
    public LineReader getLineReader() {
        return lineReader;
    }

    @Nonnull
    @Override
    public String readLine() {
        return TerminalLineHandler.readLine(lineReader, prompt);
    }

    @Nonnull
    @Override
    public String readLineNoPrompt() {
        return TerminalLineHandler.readLine(lineReader, null);
    }

    @Nonnull
    @Override
    public String readString(@Nonnull Predicate<String> predicate, @Nonnull Runnable invalidInputMessage) {
        String line = readLine();
        while (!predicate.test(line)) {
            invalidInputMessage.run();
            line = readLine();
        }

        return line;
    }

    @Nonnull
    @Override
    public <T> T read(@Nonnull Function<String, T> function, @Nonnull Runnable invalidInputMessage) {
        String line = readLine();
        T result;
        while ((result = function.apply(line)) == null) {
            invalidInputMessage.run();
            line = readLine();
        }

        return result;
    }

    @Override
    public void log(@Nonnull String message) {
        message += '\r';
        handleLine(message);

        lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        lineReader.getTerminal().writer().print(message);
        lineReader.getTerminal().flush();

        TerminalLineHandler.tryRedisplay(lineReader);
    }

    @Override
    public void logRaw(@Nonnull String message) {
        message += '\r';
        handleLine(message);

        lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        lineReader.getTerminal().writer().print(message);
        lineReader.getTerminal().flush();

        TerminalLineHandler.tryRedisplay(lineReader);
    }

    @Nonnull
    @Override
    public LoggerBase addLogLineHandler(@Nonnull LoggerLineHandler handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public boolean removeLogLineHandler(@Nonnull LoggerLineHandler handler) {
        return this.handlers.remove(handler);
    }

    @Nonnull
    @Override
    public Debugger getDebugger() {
        return debugger;
    }

    @Override
    public void handleReload() {
        prompt = StringUtil.getConsolePrompt();
    }

    @Override
    public void close() throws Exception {
        lineReader.getTerminal().flush();
        lineReader.getTerminal().close();
    }

    private void handleLine(String line) {
        handlers.forEach(handler -> {
            handler.handleLine(line, DefaultLoggerHandler.this);
            handler.handleRaw(line, DefaultLoggerHandler.this); //Line is always raw because no colour is used
        });
    }
}
