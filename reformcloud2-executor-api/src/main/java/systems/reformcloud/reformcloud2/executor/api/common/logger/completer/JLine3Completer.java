package systems.reformcloud.reformcloud2.executor.api.common.logger.completer;

import org.jetbrains.annotations.NotNull;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JLine3Completer implements Completer {

    public JLine3Completer(@NotNull CommandManager commandManager) {
        this.commandManager = commandManager;
        this.consoleCommandSource = new ConsoleCommandSource(commandManager);
    }

    private final CommandManager commandManager;

    private final CommandSource consoleCommandSource;

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        list.clear();
        String buffer = parsedLine.line();

        if (buffer.lastIndexOf(' ') == -1) {
            List<String> commands = commandManager.getCommands()
                    .stream()
                    .map(Command::mainCommand)
                    .collect(Collectors.toList());
            list.addAll(sortSub(commands));
            return;
        }

        String[] splitBuffer = buffer.split(" ");
        String[] args = Arrays.copyOfRange(splitBuffer, 1, splitBuffer.length);

        Command command = commandManager.findCommand(splitBuffer[0]);
        if (command == null) {
            return;
        }

        List<String> completed = new ArrayList<>(command.complete(this.consoleCommandSource, parsedLine.line(), args));
        if (completed.isEmpty()) {
            return;
        }

        list.addAll(sortSub(completed));
    }

    private static List<Candidate> sortSub(List<String> in) {
        in.sort(String::compareToIgnoreCase);
        return in.stream().map(Candidate::new).collect(Collectors.toList());
    }
}
