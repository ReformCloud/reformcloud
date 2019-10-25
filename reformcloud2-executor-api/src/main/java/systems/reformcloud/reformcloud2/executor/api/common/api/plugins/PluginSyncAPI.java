package systems.reformcloud.reformcloud2.executor.api.common.api.plugins;

import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;

public interface PluginSyncAPI {

    /**
     * Installs a plugin on a process
     *
     * @param process The process where the plugin should be installed
     * @param plugin The plugin which should be installed
     */
    void installPlugin(String process, InstallablePlugin plugin);

    /**
     * Installs a plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be installed
     * @param plugin The plugin which should be installed
     */
    void installPlugin(ProcessInformation process, InstallablePlugin plugin);

    /**
     * Unloads a plugin on a process
     *
     * @param process The process where the plugin should be unloaded
     * @param plugin The plugin which should be unloaded
     */
    void unloadPlugin(String process, Plugin plugin);

    /**
     * Unloads a plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be unloaded
     * @param plugin The plugin which should be unloaded
     */
    void unloadPlugin(ProcessInformation process, Plugin plugin);

    /**
     * Get an installed plugin on a process
     *
     * @param process The process where the plugin should be searched on
     * @param name The name of the plugin
     * @return The installed plugin or {@code null} if the process is not installed
     */
    Plugin getInstalledPlugin(String process, String name);

    /**
     * Get an installed plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be searched on
     * @param name The name of the plugin
     * @return The installed plugin or {@code null} if the process is not installed
     */
    Plugin getInstalledPlugin(ProcessInformation process, String name);

    /**
     * Gets all installed plugins on a process by a specific author
     *
     * @param process The process where the plugins should be searched on
     * @param author The author of the plugin
     * @return All plugins by the author
     */
    Collection<DefaultPlugin> getPlugins(String process, String author);

    /**
     * Gets all installed plugins on a process by a specific author
     *
     * @param process The {@link ProcessInformation} of the process where the plugins should be searched on
     * @param author The author of the plugin
     * @return All plugins by the author
     */
    Collection<DefaultPlugin> getPlugins(ProcessInformation process, String author);

    /**
     * Gets all installed plugins on a process
     *
     * @param process The process where the plugins should be searched on
     * @return All plugins on the process
     */
    Collection<DefaultPlugin> getPlugins(String process);

    /**
     * Gets all installed plugins on a process
     *
     * @param processInformation The {@link ProcessInformation} of the process where the plugins should be searched on
     * @return All plugins on the process
     */
    Collection<DefaultPlugin> getPlugins(ProcessInformation processInformation);
}
