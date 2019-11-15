package systems.reformcloud.reformcloud2.permissions.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.bungeecord.listener.BungeeCordPermissionListener;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

public class BungeeCordPermissionPlugin extends Plugin {

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeCordPermissionListener());
        PermissionAPI.handshake();
        PacketHelper.addAPIPackets();
    }

    @Override
    public void onDisable() {
        PacketHelper.unregisterAPIPackets();
        ProxyServer.getInstance().getScheduler().cancel(this);
    }
}
