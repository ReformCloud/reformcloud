package de.klaro.reformcloud2.executor.api.bungee.event;

import de.klaro.reformcloud2.executor.api.bungee.BungeeExecutor;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.packets.out.APIPacketOutGetBestLobbyForPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import org.bukkit.event.EventHandler;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class ConnectHandler implements Listener {

    private final int id = BungeeExecutor.getInstance().getThisProcessInformation().getTemplate().getVersion().equals(Version.WATERDOG) ? 2 : 1;

    @EventHandler
    public void handle(final ServerConnectEvent event) {
        final ProxiedPlayer proxiedPlayer = event.getPlayer();
        if (proxiedPlayer.getServer() == null) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
                @Override
                public void accept(PacketSender packetSender) {
                    Packet result = BungeeExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender,
                            new APIPacketOutGetBestLobbyForPlayer(proxiedPlayer.getPermissions(), id)
                    ).getTask().getUninterruptedly(TimeUnit.SECONDS, 3);
                    if (result != null) {
                        ProcessInformation info = result.content().get("result", ProcessInformation.TYPE);
                        if (info != null && ProxyServer.getInstance().getServers().containsKey(info.getName())) {
                            event.setTarget(ProxyServer.getInstance().getServerInfo(info.getName()));
                        }
                    }
                }
            });
        }
    }
}
