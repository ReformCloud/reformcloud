package systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.List;

public class CommandLeave extends Command {

    public CommandLeave(String name, List<String> aliases) {
        super(name, null, aliases.toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        ProcessInformation process = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(proxiedPlayer.getServer().getInfo().getName());
        if (process == null || process.isLobby()) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
                    BungeeExecutor.getInstance().getMessages().format(
                            BungeeExecutor.getInstance().getMessages().getAlreadyConnectedToHub()
                    )
            ));
            return;
        }

        ProcessInformation lobby = BungeeExecutor.getBestLobbyForPlayer(
                API.getInstance().getCurrentProcessInformation(),
                proxiedPlayer::hasPermission,
                null
        );
        if (lobby != null) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
                    BungeeExecutor.getInstance().getMessages().format(
                            BungeeExecutor.getInstance().getMessages().getConnectingToHub(), lobby.getProcessDetail().getName()
                    )
            ));
            proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(lobby.getProcessDetail().getName()));
            return;
        }

        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
                BungeeExecutor.getInstance().getMessages().format(
                        BungeeExecutor.getInstance().getMessages().getNoHubServerAvailable()
                )
        ));
    }
}
