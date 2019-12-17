package systems.reformcloud.reformcloud2.executor.api.common.api;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;

/**
 * This class is representing the wrapper for all sync api handler
 *
 * @see ExecutorAPI#getSyncAPI()
 */
public interface SyncAPI {

  /**
   * @return The current process sync api instance
   */
  @Nonnull ProcessSyncAPI getProcessSyncAPI();

  /**
   * @return The current groups sync api instance
   */
  @Nonnull GroupSyncAPI getGroupSyncAPI();

  /**
   * @return The current application sync api instance
   */
  @Nonnull ApplicationSyncAPI getApplicationSyncAPI();

  /**
   * @return The current console sync api instance
   */
  @Nonnull ConsoleSyncAPI getConsoleSyncAPI();

  /**
   * @return The current player sync api instance
   */
  @Nonnull PlayerSyncAPI getPlayerSyncAPI();

  /**
   * @return The current plugins sync api instance
   */
  @Nonnull PluginSyncAPI getPluginSyncAPI();

  /**
   * @return The current client sync api instance
   */
  @Nonnull ClientSyncAPI getClientSyncAPI();

  /**
   * @return The current database sync api instance
   */
  @Nonnull DatabaseSyncAPI getDatabaseSyncAPI();
}
