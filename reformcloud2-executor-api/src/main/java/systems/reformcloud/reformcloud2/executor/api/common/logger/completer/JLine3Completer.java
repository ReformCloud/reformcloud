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

    private final CommandManager commandManager;
    private final CommandSource consoleCommandSource;

    public JLine3Completer(@NotNull CommandManager commandManager) {
        this.commandManager = commandManager;
        this.consoleCommandSource = new ConsoleCommandSource(commandManager);
    }

    private static List<Candidate> sortSub(List<String> in) {
        in.sort(String::compareToIgnoreCase);
        return in.stream().map(Candidate::new).collect(Collectors.toList());
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        list.clear();
        String buffer = parsedLine.line();

        if (buffer.lastIndexOf(' ') == -1) {
            List<String> commands = this.commandManager.getCommands()
                    .stream()
                    .map(Command::mainCommand)
                    .collect(Collectors.toList());
            list.addAll(sortSub(commands));
            return;
        }

        String[] splitBuffer = buffer.split(" ");
        String[] args = Arrays.copyOfRange(splitBuffer, 1, splitBuffer.length);

        Command command = this.commandManager.findCommand(splitBuffer[0]);
        if (command == null) {
            return;
        }

        List<String> completed = new ArrayList<>(command.complete(this.consoleCommandSource, parsedLine.line(), args));
        if (completed.isEmpty()) {
            return;
        }

        list.addAll(sortSub(completed));
    }
}
