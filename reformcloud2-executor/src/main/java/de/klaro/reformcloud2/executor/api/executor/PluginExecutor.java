package de.klaro.reformcloud2.executor.api.executor;

import de.klaro.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;

public interface PluginExecutor {

    void installPlugin(InstallablePlugin installablePlugin);

    void uninstallPlugin(Plugin plugin);
}
