package systems.reformcloud.reformcloud2.executor.api.common.logger.terminal;

import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import org.fusesource.jansi.AnsiConsole;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TerminalLineHandler {

    private TerminalLineHandler() {
        throw new UnsupportedOperationException();
    }

    public static Terminal newTerminal(boolean jansi) throws IOException {
        wrapStreams(jansi);

        return TerminalBuilder
                .builder()
                //.system(false)
                //.streams(System.in, System.out)
                .system(true)
                .encoding(StandardCharsets.UTF_8)
                .build();
    }

    public static LineReader newLineReader(@Nonnull Terminal terminal, @Nullable Completer completer) {
        return LineReaderBuilder
                .builder()
                .completer(completer)
                .terminal(terminal)
                //.variable(LineReader.INDENTATION, 2)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .option(LineReader.Option.INSERT_TAB, false)
                .build();
    }

    private static void wrapStreams(boolean colour) {
        if (colour) {
            AnsiConsole.systemInstall();
        }
    }
}
