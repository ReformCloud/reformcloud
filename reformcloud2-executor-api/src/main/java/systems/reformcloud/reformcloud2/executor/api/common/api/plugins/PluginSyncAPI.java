package systems.reformcloud.reformcloud2.executor.api.common.api.plugins;

import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    void installPlugin(@Nonnull String process, @Nonnull InstallablePlugin plugin);

    /**
     * Installs a plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be installed
     * @param plugin  The plugin which should be installed
     */
    void installPlugin(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin);

    /**
     * Unloads a plugin on a process
     *
     * @param process The process where the plugin should be unloaded
     * @param plugin  The plugin which should be unloaded
     */
    void unloadPlugin(@Nonnull String process, @Nonnull Plugin plugin);

    /**
     * Unloads a plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be unloaded
     * @param plugin  The plugin which should be unloaded
     */
    void unloadPlugin(@Nonnull ProcessInformation process, @Nonnull Plugin plugin);

    /**
     * Get an installed plugin on a process
     *
     * @param process The process where the plugin should be searched on
     * @param name    The name of the plugin
     * @return The installed plugin or {@code null} if the process is not installed
     */
    @Nullable
    Plugin getInstalledPlugin(@Nonnull String process, @Nonnull String name);

    /**
     * Get an installed plugin on a process
     *
     * @param process The {@link ProcessInformation} of the process where the plugin should be searched on
     * @param name    The name of the plugin
     * @return The installed plugin or {@code null} if the process is not installed
     */
    @Nullable
    Plugin getInstalledPlugin(@Nonnull ProcessInformation process, @Nonnull String name);

    /**
     * Gets all installed plugins on a process by a specific author
     *
     * @param process The process where the plugins should be searched on
     * @param author  The author of the plugin
     * @return All plugins by the author
     */
    @Nonnull
    Collection<DefaultPlugin> getPlugins(@Nonnull String process, @Nonnull String author);

    /**
     * Gets all installed plugins on a process by a specific author
     *
     * @param process The {@link ProcessInformation} of the process where the plugins should be searched on
     * @param author  The author of the plugin
     * @return All plugins by the author
     */
    @Nonnull
    Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation process, @Nonnull String author);

    /**
     * Gets all installed plugins on a process
     *
     * @param process The process where the plugins should be searched on
     * @return All plugins on the process
     */
    @Nonnull
    Collection<DefaultPlugin> getPlugins(@Nonnull String process);

    /**
     * Gets all installed plugins on a process
     *
     * @param processInformation The {@link ProcessInformation} of the process where the plugins should be searched on
     * @return All plugins on the process
     */
    @Nonnull
    Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation processInformation);
}
