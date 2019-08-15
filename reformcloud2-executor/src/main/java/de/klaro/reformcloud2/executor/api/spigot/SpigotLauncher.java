package de.klaro.reformcloud2.executor.api.spigot;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotLauncher extends JavaPlugin {

    @Override
    public void onLoad() {
        DependencyLoader.doLoad();
    }
}
