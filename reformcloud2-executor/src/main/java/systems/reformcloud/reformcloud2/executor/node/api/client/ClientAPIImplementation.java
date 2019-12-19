package systems.reformcloud.reformcloud2.executor.node.api.client;

import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;

import javax.annotation.Nonnull;

public class ClientAPIImplementation implements ClientSyncAPI, ClientAsyncAPI {

    public ClientAPIImplementation(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

    private final NodeNetworkManager nodeNetworkManager;

    @Nonnull
    @Override
    public Task<Boolean> isClientConnectedAsync(String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.nodeNetworkManager.getCluster().getNode(name) != null));
        return task;
    }

    @Nonnull
    @Override
    public Task<String> getClientStartHostAsync(String name) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public Task<Integer> getMaxMemoryAsync(String name) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete((int) this.nodeNetworkManager.getCluster().getNode(name).getMaxMemory()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getMaxProcessesAsync(String name) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public Task<ClientRuntimeInformation> getClientInformationAsync(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClientConnected(@Nonnull String name) {
        return this.nodeNetworkManager.getCluster().getNode(name) != null;
    }

    @Override
    public String getClientStartHost(@Nonnull String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxMemory(@Nonnull String name) {
        if (!isClientConnected(name)) {
            return -1;
        }

        return (int) nodeNetworkManager.getCluster().getNode(name).getMaxMemory();
    }

    @Override
    public int getMaxProcesses(@Nonnull String name) {
        throw new UnsupportedOperationException("There is no config option for max node processes");
    }

    @Override
    public ClientRuntimeInformation getClientInformation(@Nonnull String name) {
        return null;
    }
}
