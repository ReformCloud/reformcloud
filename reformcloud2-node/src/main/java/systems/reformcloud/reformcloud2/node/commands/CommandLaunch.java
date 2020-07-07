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
package systems.reformcloud.reformcloud2.node.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessBuilder;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;

import java.util.*;

public final class CommandLaunch implements Command {

    @NotNull
    private static Collection<ProcessInclusion> parseInclusions(@NotNull String from) {
        Collection<ProcessInclusion> out = new ArrayList<>();
        for (String inclusion : from.split(";")) {
            String[] parts = inclusion.split(",");
            if (parts.length != 2) {
                continue;
            }

            out.add(new ProcessInclusion(parts[0], parts[1]));
        }

        return out;
    }

    public void describeCommandToSender(@NotNull CommandSender source) {
        source.sendMessages((
                "launch <group-name>            | Creates a new process bases on the group\n" +
                        " --template=[template]         | Uses a specific template for the startup (default: random)\n" +
                        " --unique-id=[unique-id]       | Sets the unique id of the new process (default: random)\n" +
                        " --display-name=[display-name] | Sets the display name of the new process (default: none)\n" +
                        " --max-memory=[memory]         | Sets the maximum amount of memory for the new process (default: group-based)\n" +
                        " --id=[id]                     | Sets the id of the new process (default: chosen from amount of online processes)\n" +
                        " --max-players=[max-players]   | Sets the maximum amount of players for the process (default: group-based)\n" +
                        " --inclusions=[url,name;...]   | Sets the inclusions of the process (default: none)\n" +
                        " --amount=[amount]             | Starts the specified amount of processes (default: 1)\n" +
                        " --prepare-only=[prepare-only] | Prepares the process but does not start it (default: false)"
        ).split("\n"));
    }

    @Override
    public void process(@NotNull CommandSender sender, String[] strings, @NotNull String commandLine) {
        if (strings.length == 0) {
            this.describeCommandToSender(sender);
            return;
        }

        Optional<ProcessGroup> base = ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(strings[0]);
        if (!base.isPresent()) {
            sender.sendMessage(LanguageManager.get("command-launch-start-not-possible-group-not-exists", strings[0]));
            return;
        }

        ProcessBuilder builder = ExecutorAPI.getInstance().getProcessProvider().createProcess().group(base.get());
        Properties properties = StringUtil.calcProperties(strings, 1);

        boolean prepareOnly = false;
        int amount = 1;

        if (properties.containsKey("template")) {
            Template baseTemplate = Streams.filter(base.get().getTemplates(), e -> e.getName().equals(properties.getProperty("template")));
            if (baseTemplate == null) {
                sender.sendMessage(LanguageManager.get("command-launch-template-not-exists", properties.getProperty("template"), base.get().getName()));
                return;
            }

            builder.template(baseTemplate);
        }

        if (properties.containsKey("unique-id")) {
            UUID uniqueID = CommonHelper.tryParse(properties.getProperty("unique-id"));
            if (uniqueID == null) {
                sender.sendMessage(LanguageManager.get("command-unique-id-failed", properties.getProperty("unique-id")));
                return;
            }

            builder.uniqueId(uniqueID);
        }

        if (properties.containsKey("display-name")) {
            builder.displayName(properties.getProperty("display-name"));
        }

        if (properties.containsKey("max-memory")) {
            Integer maxMemory = CommonHelper.fromString(properties.getProperty("max-memory"));
            if (maxMemory == null || maxMemory <= 100) {
                sender.sendMessage(LanguageManager.get("command-integer-failed", 100, properties.getProperty("max-memory")));
                return;
            }

            builder.memory(maxMemory);
        }

        if (properties.containsKey("id")) {
            Integer id = CommonHelper.fromString(properties.getProperty("id"));
            if (id == null || id <= 0) {
                sender.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("id")));
                return;
            }

            builder.id(id);
        }

        if (properties.containsKey("max-players")) {
            Integer maxPlayers = CommonHelper.fromString(properties.getProperty("max-players"));
            if (maxPlayers == null || maxPlayers <= 0) {
                sender.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("max-players")));
                return;
            }

            builder.maxPlayers(maxPlayers);
        }

        if (properties.containsKey("inclusions")) {
            builder.inclusions(parseInclusions(properties.getProperty("inclusions")));
        }

        if (properties.containsKey("prepare-only")) {
            Boolean prepare = CommonHelper.booleanFromString(properties.getProperty("prepare-only"));
            if (prepare == null) {
                sender.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("prepare-only")));
                return;
            }

            prepareOnly = prepare;
        }

        if (properties.containsKey("amount")) {
            Integer amountToStart = CommonHelper.fromString(properties.getProperty("amount"));
            if (amountToStart == null || amountToStart <= 0) {
                sender.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("amount")));
                return;
            }

            amount = amountToStart;
        }

        if (prepareOnly) {
            for (int i = 1; i <= amount; i++) {
                builder.prepare();
            }

            sender.sendMessage(LanguageManager.get("command-launch-prepared-processes", amount, base.get().getName()));
        } else {
            for (int i = 1; i <= amount; i++) {
                builder.prepare().thenAccept(e -> e.setRuntimeStateAsync(ProcessState.STARTED));
            }

            sender.sendMessage(LanguageManager.get("command-launch-started-processes", amount, base.get().getName()));
        }
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender commandSender, String[] strings, int bufferIndex, @NotNull String commandLine) {
        List<String> result = new ArrayList<>();
        if (bufferIndex == 0) {
            result.addAll(ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroupNames());
        } else if (bufferIndex >= 1) {
            result.addAll(Arrays.asList("--template=", "--unique-id=" + UUID.randomUUID(), "--display-name=",
                    "--max-memory=512", "--id=", "--max-players=20", "--inclusions=", "--amount=1", "--prepare-only=false"));
        }

        return result;
    }
}
