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
package systems.reformcloud.reformcloud2.node.console;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.command.CommandContainer;
import systems.reformcloud.reformcloud2.executor.api.command.CommandManager;
import systems.reformcloud.reformcloud2.shared.command.sources.ConsoleCommandSender;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultNodeCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        CommandManager commandManager = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(CommandManager.class);
        String buffer = parsedLine.line();

        if (buffer.lastIndexOf(' ') == -1) {
            list.addAll(commandManager.getCommands()
                    .stream()
                    .map(CommandContainer::getAliases)
                    .flatMap(Collection::stream)
                    .filter(candidate -> buffer.isEmpty() || candidate.startsWith(buffer))
                    .sorted()
                    .map(Candidate::new)
                    .collect(Collectors.toList()));
            return;
        }

        String[] split = buffer.split(" ");
        String beginTypeArgument = split.length <= 1 || buffer.endsWith(" ") ? null : split[split.length - 1].toLowerCase().trim();

        list.addAll(commandManager.suggest(buffer, ConsoleCommandSender.INSTANCE)
                .stream()
                .filter(candidate -> beginTypeArgument == null || candidate.toLowerCase().startsWith(beginTypeArgument))
                .sorted()
                .map(Candidate::new)
                .collect(Collectors.toList()));
    }
}
