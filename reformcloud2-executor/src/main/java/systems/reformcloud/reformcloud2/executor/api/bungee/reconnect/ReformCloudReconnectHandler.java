package systems.reformcloud.reformcloud2.executor.api.bungee.reconnect;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class ReformCloudReconnectHandler implements ReconnectHandler {

    @Override
    public ServerInfo getServer(ProxiedPlayer proxiedPlayer) {
        ProcessInformation information = BungeeExecutor.getBestLobbyForPlayer(
                API.getInstance().getCurrentProcessInformation(),
                proxiedPlayer::hasPermission
        );

        return information == null ? null : ProxyServer.getInstance().getServerInfo(information.getProcessDetail().getName());
    }

    @Override
    public void setServer(ProxiedPlayer proxiedPlayer) {
    }

    @Override
    public void save() {
    }

    @Override
    public void close() {
    }
}
