package systems.reformcloud.reformcloud2.executor.api.common.api.plugins;

import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;

public interface PluginSyncAPI {

    void installPlugin(String process, InstallablePlugin plugin);

    void installPlugin(ProcessInformation process, InstallablePlugin plugin);

    void unloadPlugin(String process, Plugin plugin);

    void unloadPlugin(ProcessInformation process, Plugin plugin);

    Plugin getInstalledPlugin(String process, String name);

    Plugin getInstalledPlugin(ProcessInformation process, String name);

    Collection<DefaultPlugin> getPlugins(String process, String author);

    Collection<DefaultPlugin> getPlugins(ProcessInformation process, String author);

    Collection<DefaultPlugin> getPlugins(String process);

    Collection<DefaultPlugin> getPlugins(ProcessInformation processInformation);
}
