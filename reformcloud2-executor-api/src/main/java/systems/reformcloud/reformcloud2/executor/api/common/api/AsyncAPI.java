package systems.reformcloud.reformcloud2.executor.api.common.api;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;

import javax.annotation.Nonnull;

/**
 * This class represents the wrapper for all async api methods in the cloud system
 *
 * @see ExecutorAPI#getAsyncAPI()
 */
public interface AsyncAPI {

    /**
     * @return The current process async api instance
     */
    @Nonnull
    ProcessAsyncAPI getProcessAsyncAPI();

    /**
     * @return The current groups async api instance
     */
    @Nonnull
    GroupAsyncAPI getGroupAsyncAPI();

    /**
     * @return The current application async api instance
     */
    @Nonnull
    ApplicationAsyncAPI getApplicationAsyncAPI();

    /**
     * @return The current console async api instance
     */
    @Nonnull
    ConsoleAsyncAPI getConsoleAsyncAPI();

    /**
     * @return The current player async api instance
     */
    @Nonnull
    PlayerAsyncAPI getPlayerAsyncAPI();

    /**
     * @return The current plugins async api instance
     */
    @Nonnull
    PluginAsyncAPI getPluginAsyncAPI();

    /**
     * @return The current client async api instance
     */
    @Nonnull
    ClientAsyncAPI getClientAsyncAPI();

    /**
     * @return The current database async api instance
     */
    @Nonnull
    DatabaseAsyncAPI getDatabaseAsyncAPI();
}
