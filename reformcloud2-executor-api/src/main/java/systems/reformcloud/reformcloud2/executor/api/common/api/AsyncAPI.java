package systems.reformcloud.reformcloud2.executor.api.common.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;

/**
 * This class represents the wrapper for all async api methods in the cloud system
 *
 * @see ExecutorAPI#getAsyncAPI()
 */
public interface AsyncAPI {

    /**
     * @return The current process async api instance
     */
    @NotNull
    ProcessAsyncAPI getProcessAsyncAPI();

    /**
     * @return The current groups async api instance
     */
    @NotNull
    GroupAsyncAPI getGroupAsyncAPI();

    /**
     * @return The current console async api instance
     */
    @NotNull
    ConsoleAsyncAPI getConsoleAsyncAPI();

    /**
     * @return The current player async api instance
     */
    @NotNull
    PlayerAsyncAPI getPlayerAsyncAPI();

    /**
     * @return The current database async api instance
     */
    @NotNull
    DatabaseAsyncAPI getDatabaseAsyncAPI();

    /**
     * @return The current messaging async api instance
     */
    @NotNull
    MessageAsyncAPI getMessageAsyncAPI();
}
