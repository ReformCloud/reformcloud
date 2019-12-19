package systems.reformcloud.reformcloud2.permissions.nukkit;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.nukkit.listeners.NukkitPermissionListener;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

public class NukkitPermissionPlugin extends PluginBase {

    @Override
    public void onEnable() {
        Server.getInstance().getPluginManager().registerEvents(new NukkitPermissionListener(), this);
        PermissionAPI.handshake();
        PacketHelper.addAPIPackets();
    }

    @Override
    public void onDisable() {
        PacketHelper.unregisterAPIPackets();
        Server.getInstance().getScheduler().cancelTask(this);
    }
}
