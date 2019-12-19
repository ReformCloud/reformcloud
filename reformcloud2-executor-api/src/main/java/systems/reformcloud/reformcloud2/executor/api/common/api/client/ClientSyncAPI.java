package systems.reformcloud.reformcloud2.executor.api.common.api.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;

public interface ClientSyncAPI {

  /**
   * Checks if a client is connected
   *
   * @param name The name of the client
   * @return {@code true} if the client is connected else {@code false}
   */
  boolean isClientConnected(@Nonnull String name);

  /**
   * Gets the start host of a client
   *
   * @param name The name of the client
   * @return The start host of the client or {@code null} if the client is not
   *     connected
   */
  @Nullable String getClientStartHost(@Nonnull String name);

  /**
   * Gets the maximal memory memory of a client
   *
   * @param name The name of the client
   * @return The maximal memory of the client or {@code -1} if the client is not
   *     connected
   */
  int getMaxMemory(@Nonnull String name);

  /**
   * Gets the max processes of a client
   *
   * @param name The name of the client
   * @return The max processes of the client or {@code -1} if the client is not
   *     connected
   */
  int getMaxProcesses(@Nonnull String name);

  /**
   * Gets the client runtime information of a client
   *
   * @param name The name of the client
   * @return The client runtime info or {@code null} if the client is not
   *     connected
   */
  @Nullable ClientRuntimeInformation getClientInformation(@Nonnull String name);
}
