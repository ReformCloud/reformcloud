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
package systems.reformcloud.reformcloud2.commands.plugin.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.commands.plugin.internal.InternalReformCloudCommand;

import java.util.List;

public class CommandReformCloud implements Command {

    private final List<String> aliases;

    public CommandReformCloud(@NotNull List<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public void execute(CommandSource commandSender, @NotNull String[] strings) {
        String prefix = Embedded.getInstance().getIngameMessages().getPrefix();
        InternalReformCloudCommand.execute(
                message -> commandSender.sendMessage(LegacyComponentSerializer.legacyLinking().deserialize(message)),
                strings,
                prefix.endsWith(" ") ? prefix : prefix + " ",
                this.getCommandSuccessMessage(),
                this.aliases.isEmpty() ? "rc" : this.aliases.get(0)
        );
    }

    @NotNull
    private String getCommandSuccessMessage() {
        String message = Embedded.getInstance().getIngameMessages().getCommandExecuteSuccess();
        return Embedded.getInstance().getIngameMessages().format(message);
    }

    @NotNull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public boolean hasPermission(CommandSource source, @NotNull String[] args) {
        return source.hasPermission("reformcloud.command.reformcloud");
    }
}
