package de.klaro.reformcloud2.executor.api.bungee.commands;

import de.klaro.reformcloud2.executor.api.bungee.BungeeExecutor;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.packets.out.APIPacketOutGetBestLobbyForPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

public class CommandLeave extends Command {

    private final Version version = BungeeExecutor.getInstance().getThisProcessInformation().getTemplate().getVersion();

    public CommandLeave() {
        super("leave", null, "lobby", "l", "hub", "quit");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        if (ExecutorAPI.getInstance().getProcess(proxiedPlayer.getServer().getInfo().getName()).isLobby()) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
                    BungeeExecutor.getInstance().getMessages().format(
                            BungeeExecutor.getInstance().getMessages().getAlreadyConnectedToHub()
                    )
            ));
            return;
        }

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> {
            Packet result = BungeeExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender,
                    new APIPacketOutGetBestLobbyForPlayer(proxiedPlayer.getPermissions(), version)
            ).getTask().getUninterruptedly(TimeUnit.SECONDS, 3);
            if (result != null) {
                ProcessInformation info = result.content().get("result", ProcessInformation.TYPE);
                if (info != null && ProxyServer.getInstance().getServers().containsKey(info.getName())) {
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
                            BungeeExecutor.getInstance().getMessages().format(
                                    BungeeExecutor.getInstance().getMessages().getConnectingToHub(), info.getName()
                            )
                    ));
                    proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(info.getName()));
                    return;
                }

                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
                        BungeeExecutor.getInstance().getMessages().format(
                                BungeeExecutor.getInstance().getMessages().getNoHubServerAvailable()
                        )
                ));
            }
        });
    }
}
