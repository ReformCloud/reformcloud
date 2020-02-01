package systems.reformcloud.reformcloud2.permissions.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import systems.reformcloud.reformcloud2.permissions.bukkit.listeners.BukkitPermissionListener;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.util.PermissionPluginUtil;

public class BukkitPermissionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BukkitPermissionListener(), this);
        PermissionPluginUtil.awaitConnection();
    }

    @Override
    public void onDisable() {
        PacketHelper.unregisterAPIPackets();
        Bukkit.getScheduler().cancelTasks(this);
    }
}
