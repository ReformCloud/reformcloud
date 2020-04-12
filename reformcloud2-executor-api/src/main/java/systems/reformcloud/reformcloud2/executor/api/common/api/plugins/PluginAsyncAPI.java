package systems.reformcloud.reformcloud2.executor.api.common.api.plugins;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.Collection;

public interface PluginAsyncAPI {

    /**
     * Installs a plugin on a process
     *
     * @param process The process where the plugin should be installed
     * @param plugin  The plugin which should be installed
     * @return A task which will be completed after the packet sent
     */
    @NotNull
    Task<Void> installPluginAsync(@NotNull String process, @NotNull InstallablePlugin plugin);

    /**
     * Installs a plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be installed
     * @param plugin  The plugin which should be installed
     * @return A task which will be completed after the packet sent
     */
    @NotNull
    Task<Void> installPluginAsync(@NotNull ProcessInformation process, @NotNull InstallablePlugin plugin);

    /**
     * Unloads a plugin on a process
     *
     * @param process The process where the plugin should be unloaded
     * @param plugin  The plugin which should be unloaded
     * @return A task which will be completed after the packet sent
     */
    @NotNull
    Task<Void> unloadPluginAsync(@NotNull String process, @NotNull Plugin plugin);

    /**
     * Unloads a plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be unloaded
     * @param plugin  The plugin which should be unloaded
     * @return A task which will be completed after the packet sent
     */
    @NotNull
    Task<Void> unloadPluginAsync(@NotNull ProcessInformation process, @NotNull Plugin plugin);

    /**
     * Get an installed plugin on a process
     *
     * @param process The process where the plugin should be searched on
     * @param name    The name of the plugin
     * @return A task which will be completed with the installed plugin or {@code null} if the process is not installed
     */
    @NotNull
    Task<Plugin> getInstalledPluginAsync(@NotNull String process, @NotNull String name);

    /**
     * Get an installed plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be searched on
     * @param name    The name of the plugin
     * @return A task which will be completed with the installed plugin or {@code null} if the process is not installed
     */
    @NotNull
    Task<Plugin> getInstalledPluginAsync(@NotNull ProcessInformation process, @NotNull String name);

    /**
     * Gets all installed plugins on a process by a specific author
     *
     * @param process The process where the plugins should be searched on
     * @param author  The author of the plugin
     * @return A task which will be completed with all plugins by the author
     */
    @NotNull
    Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull String process, @NotNull String author);

    /**
     * Gets all installed plugins on a process by a specific author
     *
     * @param process The {@link ProcessInformation} of the process where the plugins should be searched on
     * @param author  The author of the plugin
     * @return A task which will be completed with all plugins by the author
     */
    @NotNull
    Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull ProcessInformation process, @NotNull String author);

    /**
     * Gets all installed plugins on a process
     *
     * @param process The process where the plugins should be searched on
     * @return A task which will be completed with all plugins on the process
     */
    @NotNull
    Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull String process);

    /**
     * Gets all installed plugins on a process
     *
     * @param processInformation The {@link ProcessInformation} of the process where the plugins should be searched on
     * @return A task which will be completed with all plugins on the process
     */
    @NotNull
    Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull ProcessInformation processInformation);
}
