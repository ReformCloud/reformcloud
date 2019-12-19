package systems.reformcloud.reformcloud2.executor.controller.api.client;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;

public class ClientAPIImplementation implements ClientSyncAPI, ClientAsyncAPI {

  @Nonnull
  @Override
  public Task<Boolean> isClientConnectedAsync(String name) {
    Task<Boolean> task = new DefaultTask<>();
    Task.EXECUTOR.execute(
        ()
            -> task.complete(
                Links
                    .filterToReference(
                        ClientManager.INSTANCE.getClientRuntimeInformation(),
                        clientRuntimeInformation
                        -> clientRuntimeInformation.getName().equals(name))
                    .isPresent()));
    return task;
  }

  @Nonnull
  @Override
  public Task<String> getClientStartHostAsync(String name) {
    Task<String> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ClientRuntimeInformation information =
          Links.filter(ClientManager.INSTANCE.getClientRuntimeInformation(),
                       clientRuntimeInformation
                       -> clientRuntimeInformation.getName().equals(name));
      if (information == null) {
        task.complete(null);
      } else {
        task.complete(information.startHost());
      }
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Integer> getMaxMemoryAsync(String name) {
    Task<Integer> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ClientRuntimeInformation information =
          Links.filter(ClientManager.INSTANCE.getClientRuntimeInformation(),
                       clientRuntimeInformation
                       -> clientRuntimeInformation.getName().equals(name));
      if (information == null) {
        task.complete(null);
      } else {
        task.complete(information.maxMemory());
      }
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Integer> getMaxProcessesAsync(String name) {
    Task<Integer> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ClientRuntimeInformation information =
          Links.filter(ClientManager.INSTANCE.getClientRuntimeInformation(),
                       clientRuntimeInformation
                       -> clientRuntimeInformation.getName().equals(name));
      if (information == null) {
        task.complete(null);
      } else {
        task.complete(information.maxProcessCount());
      }
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<ClientRuntimeInformation> getClientInformationAsync(String name) {
    Task<ClientRuntimeInformation> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ClientRuntimeInformation information =
          Links.filter(ClientManager.INSTANCE.getClientRuntimeInformation(),
                       clientRuntimeInformation
                       -> clientRuntimeInformation.getName().equals(name));
      task.complete(information);
    });
    return task;
  }

  @Override
  public boolean isClientConnected(@Nonnull String name) {
    Boolean aBoolean = isClientConnectedAsync(name).getUninterruptedly();
    return aBoolean != null && aBoolean;
  }

  @Override
  public String getClientStartHost(@Nonnull String name) {
    return getClientStartHostAsync(name).getUninterruptedly();
  }

  @Override
  public int getMaxMemory(@Nonnull String name) {
    Integer integer = getMaxMemoryAsync(name).getUninterruptedly();
    return integer == null ? -1 : integer;
  }

  @Override
  public int getMaxProcesses(@Nonnull String name) {
    Integer integer = getMaxProcessesAsync(name).getUninterruptedly();
    return integer == null ? -1 : integer;
  }

  @Override
  public ClientRuntimeInformation getClientInformation(@Nonnull String name) {
    return getClientInformationAsync(name).getUninterruptedly();
  }
}
