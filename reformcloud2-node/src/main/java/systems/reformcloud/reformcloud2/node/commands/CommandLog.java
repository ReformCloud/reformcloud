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
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CommandLog implements Command {

    @Override
    public void process(@NotNull CommandSender sender, String[] strings, @NotNull String commandLine) {
        if (strings.length == 0) {
            sender.sendMessage("log <name>");
            return;
        }

        Optional<ProcessWrapper> wrapper = ExecutorAPI.getInstance().getProcessProvider().getProcessByName(strings[0]);
        if (!wrapper.isPresent()) {
            sender.sendMessage(LanguageManager.get("command-process-process-unknown", strings[0]));
            return;
        }

        if (!wrapper.get().getProcessInformation().getProcessDetail().getProcessState().isStartedOrOnline()) {
            sender.sendMessage(LanguageManager.get("command-log-process-not-started", wrapper.get().getProcessInformation().getProcessDetail().getName()));
            return;
        }

        Optional<String> logUrl = wrapper.get().uploadLog();
        if (logUrl.isPresent()) {
            sender.sendMessage(logUrl.get());
        } else {
            sender.sendMessage(LanguageManager.get("command-log-upload-log-failed"));
        }
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender commandSender, String[] strings, int bufferIndex, @NotNull String commandLine) {
        List<String> result = new ArrayList<>();
        if (bufferIndex == 0) {
            result.addAll(Streams.map(
                    Streams.allOf(ExecutorAPI.getInstance().getProcessProvider().getProcesses(), info -> info.getProcessDetail().getProcessState().isStartedOrOnline()),
                    info -> info.getProcessDetail().getName()
            ));
        }

        return result;
    }
}
