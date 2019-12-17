package systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands;

import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class CommandLeave extends Command {

  public CommandLeave(String name, List<String> aliases) {
    super(name, null, aliases.toArray(new String[0]));
  }

  @Override
  public void execute(CommandSender commandSender, String[] strings) {
    if (!(commandSender instanceof ProxiedPlayer)) {
      return;
    }

    final ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
    if (ExecutorAPI.getInstance()
            .getSyncAPI()
            .getProcessSyncAPI()
            .getProcess(proxiedPlayer.getServer().getInfo().getName())
            .isLobby()) {
      proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
          BungeeExecutor.getInstance().getMessages().format(
              BungeeExecutor.getInstance()
                  .getMessages()
                  .getAlreadyConnectedToHub())));
      return;
    }

    ProcessInformation lobby = BungeeExecutor.getBestLobbyForPlayer(
        BungeeExecutor.getInstance().getThisProcessInformation(), proxiedPlayer,
        proxiedPlayer::hasPermission);
    if (lobby != null) {
      proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
          BungeeExecutor.getInstance().getMessages().format(
              BungeeExecutor.getInstance().getMessages().getConnectingToHub(),
              lobby.getName())));
      proxiedPlayer.connect(
          ProxyServer.getInstance().getServerInfo(lobby.getName()));
      return;
    }

    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
        BungeeExecutor.getInstance().getMessages().format(
            BungeeExecutor.getInstance()
                .getMessages()
                .getNoHubServerAvailable())));
  }
}
