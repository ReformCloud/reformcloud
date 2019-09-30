package de.klaro.reformcloud2.permissions.nukkit;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import de.klaro.reformcloud2.permissions.nukkit.listeners.NukkitPermissionListener;

public class NukkitPermissionPlugin extends PluginBase {

    @Override
    public void onEnable() {
        Server.getInstance().getPluginManager().registerEvents(new NukkitPermissionListener(), this);
    }

    @Override
    public void onDisable() {
        Server.getInstance().getScheduler().cancelTask(this);
    }
}
