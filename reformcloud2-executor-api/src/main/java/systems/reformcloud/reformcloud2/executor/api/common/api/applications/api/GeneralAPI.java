package systems.reformcloud.reformcloud2.executor.api.common.api.applications.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;

public class GeneralAPI implements SyncAPI, AsyncAPI {

    public GeneralAPI(ExternalAPIImplementation externalAPIImplementation) {
        this.externalAPIImplementation = externalAPIImplementation;
    }

    private final ExternalAPIImplementation externalAPIImplementation;

    @NotNull
    @Override
    public ProcessAsyncAPI getProcessAsyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public GroupAsyncAPI getGroupAsyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public ApplicationAsyncAPI getApplicationAsyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public ConsoleAsyncAPI getConsoleAsyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public PlayerAsyncAPI getPlayerAsyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public PluginAsyncAPI getPluginAsyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public DatabaseAsyncAPI getDatabaseAsyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public MessageAsyncAPI getMessageAsyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public ProcessSyncAPI getProcessSyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public GroupSyncAPI getGroupSyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public ApplicationSyncAPI getApplicationSyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public ConsoleSyncAPI getConsoleSyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public PlayerSyncAPI getPlayerSyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public PluginSyncAPI getPluginSyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public DatabaseSyncAPI getDatabaseSyncAPI() {
        return externalAPIImplementation;
    }

    @NotNull
    @Override
    public MessageSyncAPI getMessageSyncAPI() {
        return externalAPIImplementation;
    }
}
