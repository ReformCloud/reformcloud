package systems.reformcloud.reformcloud2.executor.api.common.api.plugins;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

public interface PluginAsyncAPI {

  /**
   * Installs a plugin on a process
   *
   * @param process The process where the plugin should be installed
   * @param plugin The plugin which should be installed
   * @return A task which will be completed after the packet sent
   */
  @Nonnull
  @CheckReturnValue
  Task<Void> installPluginAsync(@Nonnull String process,
                                @Nonnull InstallablePlugin plugin);

  /**
   * Installs a plugin on a process
   *
   * @param process The {@link ProcessInformation} of the process where the
   *     plugin should be installed
   * @param plugin The plugin which should be installed
   * @return A task which will be completed after the packet sent
   */
  @Nonnull
  @CheckReturnValue
  Task<Void> installPluginAsync(@Nonnull ProcessInformation process,
                                @Nonnull InstallablePlugin plugin);

  /**
   * Unloads a plugin on a process
   *
   * @param process The process where the plugin should be unloaded
   * @param plugin The plugin which should be unloaded
   * @return A task which will be completed after the packet sent
   */
  @Nonnull
  @CheckReturnValue
  Task<Void> unloadPluginAsync(@Nonnull String process, @Nonnull Plugin plugin);

  /**
   * Unloads a plugin on a process
   *
   * @param process The {@link ProcessInformation} of the process where the
   *     plugin should be unloaded
   * @param plugin The plugin which should be unloaded
   * @return A task which will be completed after the packet sent
   */
  @Nonnull
  @CheckReturnValue
  Task<Void> unloadPluginAsync(@Nonnull ProcessInformation process,
                               @Nonnull Plugin plugin);

  /**
   * Get an installed plugin on a process
   *
   * @param process The process where the plugin should be searched on
   * @param name The name of the plugin
   * @return A task which will be completed with the installed plugin or {@code
   *     null} if the process is not installed
   */
  @Nonnull
  @CheckReturnValue
  Task<Plugin> getInstalledPluginAsync(@Nonnull String process,
                                       @Nonnull String name);

  /**
   * Get an installed plugin on a process
   *
   * @param process The {@link ProcessInformation} of the process where the
   *     plugin should be searched on
   * @param name The name of the plugin
   * @return A task which will be completed with the installed plugin or {@code
   *     null} if the process is not installed
   */
  @Nonnull
  @CheckReturnValue
  Task<Plugin> getInstalledPluginAsync(@Nonnull ProcessInformation process,
                                       @Nonnull String name);

  /**
   * Gets all installed plugins on a process by a specific author
   *
   * @param process The process where the plugins should be searched on
   * @param author The author of the plugin
   * @return A task which will be completed with all plugins by the author
   */
  @Nonnull
  @CheckReturnValue
  Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process,
                                                  @Nonnull String author);

  /**
   * Gets all installed plugins on a process by a specific author
   *
   * @param process The {@link ProcessInformation} of the process where the
   *     plugins should be searched on
   * @param author The author of the plugin
   * @return A task which will be completed with all plugins by the author
   */
  @Nonnull
  @CheckReturnValue
  Task<Collection<DefaultPlugin>>
  getPluginsAsync(@Nonnull ProcessInformation process, @Nonnull String author);

  /**
   * Gets all installed plugins on a process
   *
   * @param process The process where the plugins should be searched on
   * @return A task which will be completed with all plugins on the process
   */
  @Nonnull
  @CheckReturnValue
  Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process);

  /**
   * Gets all installed plugins on a process
   *
   * @param processInformation The {@link ProcessInformation} of the process
   *     where the plugins should be searched on
   * @return A task which will be completed with all plugins on the process
   */
  @Nonnull
  @CheckReturnValue
  Task<Collection<DefaultPlugin>>
  getPluginsAsync(@Nonnull ProcessInformation processInformation);
}
