package de.klaro.reformcloud2.executor.api.spigot;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import de.klaro.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import de.klaro.reformcloud2.executor.api.spigot.event.ExtraListenerHandler;
import de.klaro.reformcloud2.executor.api.spigot.event.PlayerListenerHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotLauncher extends JavaPlugin {

    @Override
    public void onLoad() {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();
        StringUtil.sendHeader();

        new SpigotExecutor(this);
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListenerHandler(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ExtraListenerHandler(), this);
    }

    @Override
    public void onDisable() {
        SpigotExecutor.getInstance().getNetworkClient().disconnect();
        Bukkit.getScheduler().cancelTasks(this);
    }
}
