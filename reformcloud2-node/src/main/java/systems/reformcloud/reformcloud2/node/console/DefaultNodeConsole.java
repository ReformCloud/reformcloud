/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.node.console;

import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import systems.reformcloud.reformcloud2.executor.api.console.Console;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.node.logger.ConsoleColour;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

public class DefaultNodeConsole implements Console {

    private static final String USER = System.getProperty("user.name");
    private static final String VERSION = System.getProperty("reformcloud.runner.version");

    private final ConsoleReadThread consoleReadThread = new ConsoleReadThread(this);
    private final Terminal terminal;
    private final LineReader lineReader;

    private String prompt = System.getProperty("systems.reformcloud.console-prompt-pattern", "[&crc&7-&b{0}&7@&b{1} &f~&r]$ ");

    public DefaultNodeConsole() {
        System.setProperty("library.jansi.version", "ReformCloud");
        AnsiConsole.systemInstall();

        this.prompt = ConsoleColour.toColouredString('&', this.prompt);
        this.prompt = MessageFormat.format(this.prompt, USER, VERSION);

        try {
            this.terminal = TerminalBuilder.builder().system(true).encoding(StandardCharsets.UTF_8).build();
            this.lineReader = LineReaderBuilder.builder()
                .completer(new DefaultNodeCommandCompleter())
                .terminal(this.terminal)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .build();
        } catch (IOException exception) {
            System.err.println("Unable to create terminal or line reader");
            throw new RuntimeException(exception);
        }

        this.consoleReadThread.start();
    }

    @NotNull
    @Override
    public Task<String> readString() {
        return this.consoleReadThread.getCurrentTask();
    }

    @NotNull
    @Override
    public String getPrompt() {
        return this.prompt;
    }

    @Override
    public void setPrompt(@NotNull String prompt) {
        this.prompt = ConsoleColour.toColouredString('&', prompt);
    }

    @Override
    public void clearScreen() {
        this.terminal.puts(InfoCmp.Capability.clear_screen);
        this.terminal.flush();
    }

    @Override
    public void close() throws Exception {
        this.consoleReadThread.interrupt();
        this.terminal.flush();
        this.terminal.close();

        AnsiConsole.systemUninstall();
    }

    @NotNull
    public LineReader getLineReader() {
        return this.lineReader;
    }
}
