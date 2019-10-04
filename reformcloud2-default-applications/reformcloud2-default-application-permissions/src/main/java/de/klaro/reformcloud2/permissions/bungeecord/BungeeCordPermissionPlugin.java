package de.klaro.reformcloud2.permissions.bungeecord;

import de.klaro.reformcloud2.permissions.bungeecord.listener.BungeeCordPermissionListener;
import de.klaro.reformcloud2.permissions.packets.PacketHelper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCordPermissionPlugin extends Plugin {

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeCordPermissionListener());
    }

    @Override
    public void onDisable() {
        PacketHelper.unregisterAPIPackets();
        ProxyServer.getInstance().getScheduler().cancel(this);
    }
}
