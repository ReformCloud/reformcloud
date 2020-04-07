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
