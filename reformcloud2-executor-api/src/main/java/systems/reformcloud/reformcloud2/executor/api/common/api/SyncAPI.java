package systems.reformcloud.reformcloud2.executor.api.common.api;

import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;

import javax.annotation.Nonnull;

public interface SyncAPI {

    @Nonnull
    ProcessSyncAPI getProcessSyncAPI();

    @Nonnull
    GroupSyncAPI getGroupSyncAPI();

    @Nonnull
    ApplicationSyncAPI getApplicationSyncAPI();

    @Nonnull
    ConsoleSyncAPI getConsoleSyncAPI();

    @Nonnull
    PlayerSyncAPI getPlayerSyncAPI();

    @Nonnull
    PluginSyncAPI getPluginSyncAPI();

    @Nonnull
    ClientSyncAPI getClientSyncAPI();

    @Nonnull
    DatabaseSyncAPI getDatabaseSyncAPI();
}
