package de.klaro.reformcloud2.executor.api.velocity.plugins;

import de.klaro.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.api.executor.PluginExecutor;
import de.klaro.reformcloud2.executor.api.velocity.VelocityExecutor;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class PluginExecutorContainer implements PluginExecutor {
    @Override
    public void installPlugin(InstallablePlugin installablePlugin) {
        if (Files.exists(Paths.get("plugins/" + installablePlugin.getName()))
                || VelocityExecutor.getInstance().getProxyServer().getPluginManager().isLoaded(installablePlugin.getName())) {
            return;
        }

        DownloadHelper.downloadAndDisconnect(installablePlugin.getDownloadURL(), "plugins/" + installablePlugin.getName() + ".jar");
        System.err.println("THE CLOUD DOWNLOADED THE PLUGIN BUT IT IS NOT SUPPORTED TO ENABLE / LOAD THE PLUGIN ON VELOCITY");
    }

    @Override
    public void uninstallPlugin(Plugin plugin) {
        throw new UnsupportedOperationException("Not supported on velocity");
    }
}
