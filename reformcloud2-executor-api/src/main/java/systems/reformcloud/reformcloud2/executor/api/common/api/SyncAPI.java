package systems.reformcloud.reformcloud2.executor.api.common.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
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
    @NotNull
    ProcessSyncAPI getProcessSyncAPI();

    /**
     * @return The current groups sync api instance
     */
    @NotNull
    GroupSyncAPI getGroupSyncAPI();

    /**
     * @return The current console sync api instance
     */
    @NotNull
    ConsoleSyncAPI getConsoleSyncAPI();

    /**
     * @return The current player sync api instance
     */
    @NotNull
    PlayerSyncAPI getPlayerSyncAPI();

    /**
     * @return The current database sync api instance
     */
    @NotNull
    DatabaseSyncAPI getDatabaseSyncAPI();

    /**
     * @return The current messaging sync api instance
     */
    @NotNull
    MessageSyncAPI getMessageSyncAPI();
}
