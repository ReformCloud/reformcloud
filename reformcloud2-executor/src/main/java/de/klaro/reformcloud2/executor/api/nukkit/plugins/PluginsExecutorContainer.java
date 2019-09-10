package de.klaro.reformcloud2.executor.api.nukkit.plugins;

import cn.nukkit.Server;
import de.klaro.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.api.executor.PluginExecutor;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class PluginsExecutorContainer implements PluginExecutor {

    @Override
    public void installPlugin(InstallablePlugin installablePlugin) {
        if (Files.exists(Paths.get("plugins/" + installablePlugin.getName()))
                || Server.getInstance().getPluginManager().getPlugin(installablePlugin.getName()) != null) {
            return;
        }

        DownloadHelper.downloadAndDisconnect(installablePlugin.getDownloadURL(), "plugins/" + installablePlugin.getName() + ".jar");
        final cn.nukkit.plugin.Plugin plugin = Server.getInstance().getPluginManager().loadPlugin("plugins/" + installablePlugin.getName() + ".jar");
        Server.getInstance().getPluginManager().enablePlugin(plugin);
    }

    @Override
    public void uninstallPlugin(Plugin plugin) {
        if (Server.getInstance().getPluginManager().getPlugin(plugin.getName()) != null) {
            Server.getInstance().getPluginManager().disablePlugin(
                    Server.getInstance().getPluginManager().getPlugin(plugin.getName())
            );
        }
    }
}
