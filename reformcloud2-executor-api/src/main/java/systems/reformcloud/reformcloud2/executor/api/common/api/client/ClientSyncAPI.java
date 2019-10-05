package systems.reformcloud.reformcloud2.executor.api.common.api.client;

import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;

public interface ClientSyncAPI {

    boolean isClientConnected(String name);

    String getClientStartHost(String name);

    int getMaxMemory(String name);

    int getMaxProcesses(String name);

    ClientRuntimeInformation getClientInformation(String name);
}
