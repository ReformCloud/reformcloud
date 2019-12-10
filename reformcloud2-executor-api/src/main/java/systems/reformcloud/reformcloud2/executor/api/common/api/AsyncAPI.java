package systems.reformcloud.reformcloud2.executor.api.common.api;

import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;

import javax.annotation.Nonnull;

public interface AsyncAPI {

    @Nonnull
    ProcessAsyncAPI getProcessAsyncAPI();

    @Nonnull
    GroupAsyncAPI getGroupAsyncAPI();

    @Nonnull
    ApplicationAsyncAPI getApplicationAsyncAPI();

    @Nonnull
    ConsoleAsyncAPI getConsoleAsyncAPI();

    @Nonnull
    PlayerAsyncAPI getPlayerAsyncAPI();

    @Nonnull
    PluginAsyncAPI getPluginAsyncAPI();

    @Nonnull
    ClientAsyncAPI getClientAsyncAPI();

    @Nonnull
    DatabaseAsyncAPI getDatabaseAsyncAPI();
}
