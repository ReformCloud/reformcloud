package systems.reformcloud.reformcloud2.permissions.nukkit;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import systems.reformcloud.reformcloud2.permissions.nukkit.listeners.NukkitPermissionListener;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.util.PermissionPluginUtil;

public class NukkitPermissionPlugin extends PluginBase {

    @Override
    public void onEnable() {
        Server.getInstance().getPluginManager().registerEvents(new NukkitPermissionListener(), this);
        PermissionPluginUtil.awaitConnection();
    }

    @Override
    public void onDisable() {
        PacketHelper.unregisterAPIPackets();
        Server.getInstance().getScheduler().cancelTask(this);
    }
}
