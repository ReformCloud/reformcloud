package systems.reformcloud.reformcloud2.executor.api.common.api.client;

import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface ClientAsyncAPI extends ClientSyncAPI {

    /**
     * Checks if a client is connected
     *
     * @param name The name of the client
     * @return A task which will be completed with the value if a client is connected
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> isClientConnectedAsync(String name);

    /**
     * Gets the start host of a client
     *
     * @param name The name of the client
     * @return A task which will be completed with the start host of the client or {@code null}
     * if the client is not connected
     */
    @Nonnull
    @CheckReturnValue
    Task<String> getClientStartHostAsync(String name);

    /**
     * Gets the maximal memory memory of a client
     *
     * @param name The name of the client
     * @return A task which will be completed with the maximal memory of the client or {@code -1}
     * if the client is not connected
     */
    @Nonnull
    @CheckReturnValue
    Task<Integer> getMaxMemoryAsync(String name);

    /**
     * Gets the max processes of a client
     *
     * @param name The name of the client
     * @return A task which will be completed with the max processes of the client or {@code -1}
     * if the client is not connected
     */
    @Nonnull
    @CheckReturnValue
    Task<Integer> getMaxProcessesAsync(String name);

    /**
     * Gets the client runtime information of a client
     *
     * @param name The name of the client
     * @return A task which will be completed with the client runtime info or {@code null} if the client
     * is not connected
     */
    @Nonnull
    @CheckReturnValue
    Task<ClientRuntimeInformation> getClientInformationAsync(String name);
}
