package systems.reformcloud.reformcloud2.permissions.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import systems.reformcloud.reformcloud2.permissions.bukkit.listeners.BukkitPermissionListener;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

public class BukkitPermissionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BukkitPermissionListener(), this);
    }

    @Override
    public void onDisable() {
        PacketHelper.unregisterAPIPackets();
        Bukkit.getScheduler().cancelTasks(this);
    }
}
