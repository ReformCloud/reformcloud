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
package systems.reformcloud.reformcloud2.executor.api.common.logger.terminal;

import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TerminalLineHandler {

    private TerminalLineHandler() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Terminal newTerminal(boolean jansi) throws IOException {
        wrapStreams(jansi);

        return TerminalBuilder
                .builder()
                .system(true)
                .encoding(StandardCharsets.UTF_8)
                .build();
    }

    @NotNull
    public static LineReader newLineReader(@NotNull Terminal terminal, @Nullable Completer completer) {
        return LineReaderBuilder
                .builder()
                .completer(completer)
                .terminal(terminal)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .build();
    }

    @NotNull
    public static String readLine(@NotNull LineReader reader, @Nullable String prompt) {
        try {
            return reader.readLine(prompt);
        } catch (final EndOfFileException ignored) {
        } catch (final UserInterruptException ex) {
            System.err.println(LanguageManager.get("logger.interrupt.not.supported"));
            System.exit(-1);
        }

        return "";
    }

    public static void tryRedisplay(@NotNull LineReader lineReader) {
        if (!lineReader.isReading()) {
            // We cannot call widgets while the line reader is not reading
            return;
        }

        lineReader.callWidget(LineReader.REDRAW_LINE);
        lineReader.callWidget(LineReader.REDISPLAY);
    }

    private static void wrapStreams(boolean colour) {
        if (colour) {
            AnsiConsole.systemInstall();
        }
    }
}
