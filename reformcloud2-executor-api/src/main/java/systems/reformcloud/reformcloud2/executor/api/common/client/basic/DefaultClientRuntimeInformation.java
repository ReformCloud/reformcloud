package systems.reformcloud.reformcloud2.executor.api.common.client.basic;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;

public final class DefaultClientRuntimeInformation
    implements ClientRuntimeInformation {

  public DefaultClientRuntimeInformation(String startHost, int maxMemory,
                                         int maxProcesses, String name) {
    this.startHost = startHost;
    this.maxMemory = maxMemory;
    this.maxProcesses = maxProcesses;
    this.name = name;
  }

  private String startHost;

  private int maxMemory;

  private int maxProcesses;

  private String name;

  @Nonnull
  @Override
  public String startHost() {
    return startHost;
  }

  @Override
  public int maxMemory() {
    return maxMemory;
  }

  @Override
  public int maxProcessCount() {
    return maxProcesses;
  }

  @Nonnull
  @Override
  public String getName() {
    return name;
  }
}
