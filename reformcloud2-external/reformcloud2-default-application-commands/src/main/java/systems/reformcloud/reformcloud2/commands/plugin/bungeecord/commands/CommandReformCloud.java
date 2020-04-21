package systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.plugin.internal.InternalReformCloudCommand;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;

import java.util.List;

public class CommandReformCloud extends Command {

    public CommandReformCloud(@NotNull String name, @NotNull List<String> aliases) {
        super(name, "reformcloud.command.reformcloud", aliases.toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        String prefix = BungeeExecutor.getInstance().getMessages().getPrefix() + " ";
        InternalReformCloudCommand.execute(
                message -> commandSender.sendMessage(TextComponent.fromLegacyText(message)),
                strings,
                prefix,
                this.getCommandSuccessMessage(),
                super.getName()
        );
    }

    @NotNull
    private String getCommandSuccessMessage() {
        String message = BungeeExecutor.getInstance().getMessages().getCommandExecuteSuccess();
        return BungeeExecutor.getInstance().getMessages().format(message);
    }
}
