/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.logger.coloured;

import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.logger.Debugger;
import systems.reformcloud.reformcloud2.executor.api.common.logger.HandlerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerLineHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.debugger.ColouredDebugger;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.formatter.ColouredLogFormatter;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.formatter.LogFileFormatter;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.handler.ColouredConsoleHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.completer.JLine3Completer;
import systems.reformcloud.reformcloud2.executor.api.common.logger.stream.OutputStream;
import systems.reformcloud.reformcloud2.executor.api.common.logger.terminal.TerminalLineHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public final class ColouredLoggerHandler extends LoggerBase {

    private static String prompt;

    static {
        prompt = Colours.coloured(StringUtil.getConsolePrompt());
    }

    private final LineReader lineReader;
    private final List<LoggerLineHandler> handlers = new ArrayList<>();
    private final Debugger debugger = new ColouredDebugger();

    public ColouredLoggerHandler(@NotNull CommandManager commandManager) throws IOException {
        Terminal terminal = TerminalLineHandler.newTerminal(true);
        this.lineReader = TerminalLineHandler.newLineReader(terminal, new JLine3Completer(commandManager));

        if (!Files.exists(Paths.get("logs"))) {
            Files.createDirectories(Paths.get("logs"));
        }

        FileHandler fileHandler = new FileHandler("logs/cloud.log", 70000000, 8, true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new LogFileFormatter(this));
        this.addHandler(fileHandler);

        HandlerBase consoleHandler = new ColouredConsoleHandler(this);
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new ColouredLogFormatter(this));
        this.addHandler(consoleHandler);

        System.setOut(new PrintStream(new OutputStream(this, Level.INFO), true));
        System.setErr(new PrintStream(new OutputStream(this, Level.SEVERE), true));
    }

    @NotNull
    @Override
    public LineReader getLineReader() {
        return this.lineReader;
    }

    @NotNull
    @Override
    public String readLine() {
        return TerminalLineHandler.readLine(this.lineReader, prompt);
    }

    @NotNull
    @Override
    public String readLineNoPrompt() {
        return TerminalLineHandler.readLine(this.lineReader, null);
    }

    @NotNull
    @Override
    public String readString(@NotNull Predicate<String> predicate, @NotNull Runnable invalidInputMessage) {
        String line = this.readLine();
        while (!predicate.test(line)) {
            invalidInputMessage.run();
            line = this.readLine();
        }

        return line;
    }

    @NotNull
    @Override
    public <T> T read(@NotNull Function<String, T> function, @NotNull Runnable invalidInputMessage) {
        String line = this.readLine();
        T result;
        while ((result = function.apply(line)) == null) {
            invalidInputMessage.run();
            line = this.readLine();
        }

        return result;
    }

    @Override
    public void log(@NotNull String message) {
        message = Colours.coloured(message);
        message += '\r';
        this.handleLine(message);

        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        this.lineReader.getTerminal().puts(InfoCmp.Capability.clr_eol);
        this.lineReader.getTerminal().writer().print(message);
        this.lineReader.getTerminal().writer().flush();

        TerminalLineHandler.tryRedisplay(this.lineReader);
    }

    @Override
    public void logRaw(@NotNull String message) {
        message = Colours.stripColor(message);
        message += '\r';
        this.handleLine(message);

        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        this.lineReader.getTerminal().puts(InfoCmp.Capability.clr_eol);
        this.lineReader.getTerminal().writer().print(message);
        this.lineReader.getTerminal().writer().flush();

        TerminalLineHandler.tryRedisplay(this.lineReader);
    }

    @NotNull
    @Override
    public LoggerBase addLogLineHandler(@NotNull LoggerLineHandler handler) {
        this.handlers.add(handler);
        return this;
    }

    @Override
    public boolean removeLogLineHandler(@NotNull LoggerLineHandler handler) {
        return this.handlers.remove(handler);
    }

    @NotNull
    @Override
    public Debugger getDebugger() {
        return this.debugger;
    }

    @Override
    public void handleReload() {
        prompt = StringUtil.getConsolePrompt();
    }

    @Override
    public void close() throws Exception {
        this.lineReader.getTerminal().flush();
        this.lineReader.getTerminal().close();
    }

    private void handleLine(String line) {
        this.handlers.forEach(handler -> {
            handler.handleLine(line, ColouredLoggerHandler.this);
            handler.handleRaw(Colours.stripColor(line), ColouredLoggerHandler.this);
        });
    }
}
