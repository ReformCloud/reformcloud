package systems.reformcloud.reformcloud2.permissions.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.permissions.bungeecord.command.CommandCloudPerms;
import systems.reformcloud.reformcloud2.permissions.bungeecord.listener.BungeeCordPermissionListener;
import systems.reformcloud.reformcloud2.permissions.util.PermissionPluginUtil;

public class BungeeCordPermissionPlugin extends Plugin {

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeCordPermissionListener());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandCloudPerms());

        PermissionPluginUtil.awaitConnection();
    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().getScheduler().cancel(this);
    }
}
