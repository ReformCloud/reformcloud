package systems.reformcloud.reformcloud2.executor.api.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.spigot.event.ExtraListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.spigot.event.PlayerListenerHandler;

public final class SpigotLauncher extends JavaPlugin {

    @Override
    public void onLoad() {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();
        StringUtil.sendHeader();
    }

    @Override
    public void onEnable() {
        new SpigotExecutor(this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListenerHandler(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ExtraListenerHandler(), this);
    }

    @Override
    public void onDisable() {
        SpigotExecutor.getInstance().getNetworkClient().disconnect();
        Bukkit.getScheduler().cancelTasks(this);

        Bukkit.getOnlinePlayers().forEach(e -> e.kickPlayer(""));
    }
}
