package de.klaro.reformcloud2.executor.api.spigot.plugins;

import de.klaro.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.api.executor.PluginExecutor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class PluginExecutorContainer implements PluginExecutor {

    @Override
    public void installPlugin(InstallablePlugin installablePlugin) {
        if (Files.exists(Paths.get("plugins/" + installablePlugin.getName()))
                || Bukkit.getPluginManager().getPlugin(installablePlugin.getName()) != null) {
            return;
        }

        DownloadHelper.downloadAndDisconnect(installablePlugin.getDownloadURL(), "plugins/" + installablePlugin.getName() + ".jar");

        try {
            org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().loadPlugin(Paths.get("plugins/" + installablePlugin.getName() + ".jar").toFile());
            assert plugin != null;
            Bukkit.getPluginManager().enablePlugin(plugin);
        } catch (final InvalidPluginException | InvalidDescriptionException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void uninstallPlugin(Plugin plugin) {
        org.bukkit.plugin.Plugin plugin1 = Bukkit.getPluginManager().getPlugin(plugin.getName());
        if (plugin1 != null) {
            Bukkit.getPluginManager().disablePlugin(plugin1);
        }
    }
}
