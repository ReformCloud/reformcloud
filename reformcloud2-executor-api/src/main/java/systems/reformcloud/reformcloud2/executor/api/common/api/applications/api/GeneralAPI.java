package systems.reformcloud.reformcloud2.executor.api.common.api.applications.api;

import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientSyncAPI;
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

import javax.annotation.Nonnull;

public class GeneralAPI implements SyncAPI, AsyncAPI {

    public GeneralAPI(ExternalAPIImplementation externalAPIImplementation) {
        this.externalAPIImplementation = externalAPIImplementation;
    }

    private final ExternalAPIImplementation externalAPIImplementation;

    @Nonnull
    @Override
    public ProcessAsyncAPI getProcessAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public GroupAsyncAPI getGroupAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public ApplicationAsyncAPI getApplicationAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public ConsoleAsyncAPI getConsoleAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public PlayerAsyncAPI getPlayerAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public PluginAsyncAPI getPluginAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public ClientAsyncAPI getClientAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public DatabaseAsyncAPI getDatabaseAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public MessageAsyncAPI getMessageAsyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public ProcessSyncAPI getProcessSyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public GroupSyncAPI getGroupSyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public ApplicationSyncAPI getApplicationSyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public ConsoleSyncAPI getConsoleSyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public PlayerSyncAPI getPlayerSyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public PluginSyncAPI getPluginSyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public ClientSyncAPI getClientSyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public DatabaseSyncAPI getDatabaseSyncAPI() {
        return externalAPIImplementation;
    }

    @Nonnull
    @Override
    public MessageSyncAPI getMessageSyncAPI() {
        return externalAPIImplementation;
    }
}
