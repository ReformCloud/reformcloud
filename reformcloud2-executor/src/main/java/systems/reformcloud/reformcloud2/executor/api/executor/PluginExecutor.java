package systems.reformcloud.reformcloud2.executor.api.executor;

import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;

public interface PluginExecutor {

    void installPlugin(InstallablePlugin installablePlugin);

    void uninstallPlugin(Plugin plugin);
}
