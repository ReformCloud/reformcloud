package systems.reformcloud.reformcloud2.executor.api.common.api.client;

import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

public interface ClientAsyncAPI extends ClientSyncAPI {

    Task<Boolean> isClientConnectedAsync(String name);

    Task<String> getClientStartHostAsync(String name);

    Task<Integer> getMaxMemoryAsync(String name);

    Task<Integer> getMaxProcessesAsync(String name);

    Task<ClientRuntimeInformation> getClientInformationAsync(String name);
}
