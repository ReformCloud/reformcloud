package systems.reformcloud.reformcloud2.executor.api.common.api.plugins;

import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.Collection;

public interface PluginAsyncAPI extends PluginSyncAPI {

    Task<Void> installPluginAsync(String process, InstallablePlugin plugin);

    Task<Void> installPluginAsync(ProcessInformation process, InstallablePlugin plugin);

    Task<Void> unloadPluginAsync(String process, Plugin plugin);

    Task<Void> unloadPluginAsync(ProcessInformation process, Plugin plugin);

    Task<Plugin> getInstalledPluginAsync(String process, String name);

    Task<Plugin> getInstalledPluginAsync(ProcessInformation process, String name);

    Task<Collection<DefaultPlugin>> getPluginsAsync(String process, String author);

    Task<Collection<DefaultPlugin>> getPluginsAsync(ProcessInformation process, String author);

    Task<Collection<DefaultPlugin>> getPluginsAsync(String process);

    Task<Collection<DefaultPlugin>> getPluginsAsync(ProcessInformation processInformation);
}
