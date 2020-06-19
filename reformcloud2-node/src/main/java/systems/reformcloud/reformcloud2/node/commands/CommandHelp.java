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
import systems.reformcloud.reformcloud2.executor.api.command.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class CommandHelp implements Command {

    @Override
    public void process(@NotNull CommandSender sender, @NotNull String[] strings, @NotNull String commandLine) {
        sender.sendMessage("ReformCloud git:runner:"
                + System.getProperty("reformcloud.runner.version", "c-build")
                + ":"
                + CommandHelp.class.getPackage().getSpecificationVersion()
                + " by derklaro and ReformCloud-Community"
        );
        sender.sendMessage("Discord: https://discord.gg/uskXdVZ");
        sender.sendMessage(" ");

        Collection<Duo<String, String>> map = Streams.map(
                ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(CommandManager.class).getCommands(),
                container -> new Duo<>(String.join(", ", container.getAliases()), container.getDescription())
        );
        sender.sendMessages(this.formatHelp(map));
    }

    @NotNull
    private String[] formatHelp(@NotNull Collection<Duo<String, String>> messages) {
        int longest = 0;
        for (Duo<String, String> message : messages) {
            if (message.getFirst().length() > longest) {
                longest = message.getFirst().length();
            }
        }

        Collection<String> result = new ArrayList<>(messages.size());
        for (Duo<String, String> message : messages) {
            String s = String.join("", Collections.nCopies(Math.max(longest - message.getFirst().length(), 0), " "));
            result.add(message.getFirst() + s + " | " + message.getSecond());
        }

        return result.toArray(new String[0]);
    }
}
