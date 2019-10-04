package de.klaro.reformcloud2.permissions.bukkit;

import de.klaro.reformcloud2.permissions.bukkit.listeners.BukkitPermissionListener;
import de.klaro.reformcloud2.permissions.packets.PacketHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
