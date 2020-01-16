package systems.reformcloud.reformcloud2.executor.api.common.logger.terminal;

import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TerminalLineHandler {

    private TerminalLineHandler() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static Terminal newTerminal(boolean jansi) throws IOException {
        wrapStreams(jansi);

        return TerminalBuilder
                .builder()
                .system(true)
                .encoding(StandardCharsets.UTF_8)
                .build();
    }

    @Nonnull
    public static LineReader newLineReader(@Nonnull Terminal terminal, @Nullable Completer completer) {
        return LineReaderBuilder
                .builder()
                .completer(completer)
                .terminal(terminal)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .build();
    }

    @Nonnull
    public static String readLine(@Nonnull LineReader reader, @Nullable String prompt) {
        try {
            return reader.readLine(prompt);
        } catch (final EndOfFileException ignored) {
        } catch (final UserInterruptException ex) {
            System.err.println(LanguageManager.get("logger.interrupt.not.supported"));
        }

        return "";
    }

    public static void tryRedisplay(@Nonnull LineReader lineReader) {
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
