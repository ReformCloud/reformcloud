package systems.reformcloud.reformcloud2.commands.plugin.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.plugin.internal.InternalReformCloudCommand;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

import java.util.List;

public class CommandReformCloud implements Command {

    public CommandReformCloud(@NotNull List<String> aliases) {
        this.aliases = aliases;
    }

    private final List<String> aliases;

    @Override
    public void execute(CommandSource commandSender, @NotNull String[] strings) {
        String prefix = VelocityExecutor.getInstance().getMessages().getPrefix() + " ";
        InternalReformCloudCommand.execute(
                message -> commandSender.sendMessage(TextComponent.of(message)),
                strings,
                prefix,
                this.getCommandSuccessMessage(),
                aliases.isEmpty() ? "rc" : aliases.get(0)
        );
    }

    @NotNull
    private String getCommandSuccessMessage() {
        String message = VelocityExecutor.getInstance().getMessages().getCommandExecuteSuccess();
        return VelocityExecutor.getInstance().getMessages().format(message);
    }

    @NotNull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean hasPermission(CommandSource source, @NotNull String[] args) {
        return source.hasPermission("reformcloud.command.reformcloud");
    }
}
