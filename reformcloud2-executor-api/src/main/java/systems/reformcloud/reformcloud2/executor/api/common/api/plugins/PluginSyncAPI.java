package systems.reformcloud.reformcloud2.executor.api.common.api.plugins;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;

/**
 * This class represents the sync api for plugins
 *
 * @see SyncAPI#getPluginSyncAPI()
 */
public interface PluginSyncAPI {

    /**
     * Installs a plugin on a process
     *
     * @param process The process where the plugin should be installed
     * @param plugin  The plugin which should be installed
     */
    void installPlugin(@NotNull String process, @NotNull InstallablePlugin plugin);

    /**
     * Installs a plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be installed
     * @param plugin  The plugin which should be installed
     */
    void installPlugin(@NotNull ProcessInformation process, @NotNull InstallablePlugin plugin);

    /**
     * Unloads a plugin on a process
     *
     * @param process The process where the plugin should be unloaded
     * @param plugin  The plugin which should be unloaded
     */
    void unloadPlugin(@NotNull String process, @NotNull Plugin plugin);

    /**
     * Unloads a plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be unloaded
     * @param plugin  The plugin which should be unloaded
     */
    void unloadPlugin(@NotNull ProcessInformation process, @NotNull Plugin plugin);

    /**
     * Get an installed plugin on a process
     *
     * @param process The process where the plugin should be searched on
     * @param name    The name of the plugin
     * @return The installed plugin or {@code null} if the process is not installed
     */
    @Nullable
    Plugin getInstalledPlugin(@NotNull String process, @NotNull String name);

    /**
     * Get an installed plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be searched on
     * @param name    The name of the plugin
     * @return The installed plugin or {@code null} if the process is not installed
     */
    @Nullable
    Plugin getInstalledPlugin(@NotNull ProcessInformation process, @NotNull String name);

    /**
     * Gets all installed plugins on a process by a specific author
     *
     * @param process The process where the plugins should be searched on
     * @param author  The author of the plugin
     * @return All plugins by the author
     */
    @NotNull
    Collection<DefaultPlugin> getPlugins(@NotNull String process, @NotNull String author);

    /**
     * Gets all installed plugins on a process by a specific author
     *
     * @param process The {@link ProcessInformation} of the process where the plugins should be searched on
     * @param author  The author of the plugin
     * @return All plugins by the author
     */
    @NotNull
    Collection<DefaultPlugin> getPlugins(@NotNull ProcessInformation process, @NotNull String author);

    /**
     * Gets all installed plugins on a process
     *
     * @param process The process where the plugins should be searched on
     * @return All plugins on the process
     */
    @NotNull
    Collection<DefaultPlugin> getPlugins(@NotNull String process);

    /**
     * Gets all installed plugins on a process
     *
     * @param processInformation The {@link ProcessInformation} of the process where the plugins should be searched on
     * @return All plugins on the process
     */
    @NotNull
    Collection<DefaultPlugin> getPlugins(@NotNull ProcessInformation processInformation);
}
