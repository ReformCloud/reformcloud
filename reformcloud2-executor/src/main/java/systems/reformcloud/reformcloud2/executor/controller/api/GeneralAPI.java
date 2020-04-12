package systems.reformcloud.reformcloud2.executor.controller.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
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
import systems.reformcloud.reformcloud2.executor.controller.api.applications.ApplicationAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.console.ConsoleAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.database.DatabaseAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.group.GroupAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.message.ChannelMessageAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.player.PlayerAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.plugins.PluginAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.process.ProcessAPIImplementation;

public class GeneralAPI implements SyncAPI, AsyncAPI {

    public GeneralAPI(
            ApplicationAPIImplementation applicationAPI,
            ConsoleAPIImplementation consoleAPI,
            DatabaseAPIImplementation databaseAPI,
            GroupAPIImplementation groupAPI,
            PlayerAPIImplementation playerAPI,
            PluginAPIImplementation pluginAPI,
            ProcessAPIImplementation processAPI,
            ChannelMessageAPIImplementation channelAPI
    ) {
        this.applicationAPI = applicationAPI;
        this.consoleAPI = consoleAPI;
        this.databaseAPI = databaseAPI;
        this.groupAPI = groupAPI;
        this.playerAPI = playerAPI;
        this.pluginAPI = pluginAPI;
        this.processAPI = processAPI;
        this.channelAPI = channelAPI;
    }

    private final ApplicationAPIImplementation applicationAPI;

    private final ConsoleAPIImplementation consoleAPI;

    private final DatabaseAPIImplementation databaseAPI;

    private final GroupAPIImplementation groupAPI;

    private final PlayerAPIImplementation playerAPI;

    private final PluginAPIImplementation pluginAPI;

    private final ProcessAPIImplementation processAPI;

    private final ChannelMessageAPIImplementation channelAPI;

    @NotNull
    @Override
    public ProcessAsyncAPI getProcessAsyncAPI() {
        return processAPI;
    }

    @NotNull
    @Override
    public GroupAsyncAPI getGroupAsyncAPI() {
        return groupAPI;
    }

    @NotNull
    @Override
    public ApplicationAsyncAPI getApplicationAsyncAPI() {
        return applicationAPI;
    }

    @NotNull
    @Override
    public ConsoleAsyncAPI getConsoleAsyncAPI() {
        return consoleAPI;
    }

    @NotNull
    @Override
    public PlayerAsyncAPI getPlayerAsyncAPI() {
        return playerAPI;
    }

    @NotNull
    @Override
    public PluginAsyncAPI getPluginAsyncAPI() {
        return pluginAPI;
    }

    @NotNull
    @Override
    public DatabaseAsyncAPI getDatabaseAsyncAPI() {
        return databaseAPI;
    }

    @NotNull
    @Override
    public MessageAsyncAPI getMessageAsyncAPI() {
        return this.channelAPI;
    }

    @NotNull
    @Override
    public ProcessSyncAPI getProcessSyncAPI() {
        return processAPI;
    }

    @NotNull
    @Override
    public GroupSyncAPI getGroupSyncAPI() {
        return groupAPI;
    }

    @NotNull
    @Override
    public ApplicationSyncAPI getApplicationSyncAPI() {
        return applicationAPI;
    }

    @NotNull
    @Override
    public ConsoleSyncAPI getConsoleSyncAPI() {
        return consoleAPI;
    }

    @NotNull
    @Override
    public PlayerSyncAPI getPlayerSyncAPI() {
        return playerAPI;
    }

    @NotNull
    @Override
    public PluginSyncAPI getPluginSyncAPI() {
        return pluginAPI;
    }

    @NotNull
    @Override
    public DatabaseSyncAPI getDatabaseSyncAPI() {
        return databaseAPI;
    }

    @NotNull
    @Override
    public MessageSyncAPI getMessageSyncAPI() {
        return this.channelAPI;
    }
}
