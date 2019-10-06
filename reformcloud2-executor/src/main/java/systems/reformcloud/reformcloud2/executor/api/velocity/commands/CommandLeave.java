package systems.reformcloud.reformcloud2.executor.api.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

public class CommandLeave implements Command {

    @Override
    public void execute(CommandSource commandSource, @NonNull String[] strings) {
        if (!(commandSource instanceof Player)) {
            return;
        }

        final Player player = (Player) commandSource;
        if (ExecutorAPI.getInstance().getThisProcessInformation().isLobby()) {
            player.sendMessage(TextComponent.of(
                    VelocityExecutor.getInstance().getMessages().format(
                            VelocityExecutor.getInstance().getMessages().getAlreadyConnectedToHub()
                    )
            ));
            return;
        }

        ProcessInformation lobby = VelocityExecutor.getBestLobbyForPlayer(VelocityExecutor.getInstance().getThisProcessInformation(),
                player::hasPermission);
        if (lobby != null) {
            player.sendMessage(TextComponent.of(
                    VelocityExecutor.getInstance().getMessages().format(
                            VelocityExecutor.getInstance().getMessages().getConnectingToHub(), lobby.getName()
                    )
            ));
            player.createConnectionRequest(VelocityExecutor.getInstance().getProxyServer().getServer(lobby.getName()).get()).fireAndForget();
            return;
        }

        player.sendMessage(TextComponent.of(
                VelocityExecutor.getInstance().getMessages().format(
                        VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
                )
        ));
    }
}
