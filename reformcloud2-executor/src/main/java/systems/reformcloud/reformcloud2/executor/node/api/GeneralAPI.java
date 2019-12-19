package systems.reformcloud.reformcloud2.executor.node.api;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.node.api.applications.ApplicationAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.client.ClientAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.console.ConsoleAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.database.DatabaseAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.group.GroupAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.player.PlayerAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.plugins.PluginAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.process.ProcessAPIImplementation;

public class GeneralAPI implements SyncAPI, AsyncAPI {

  public GeneralAPI(ApplicationAPIImplementation applicationAPI,
                    ClientAPIImplementation clientAPI,
                    ConsoleAPIImplementation consoleAPI,
                    DatabaseAPIImplementation databaseAPI,
                    GroupAPIImplementation groupAPI,
                    PlayerAPIImplementation playerAPI,
                    PluginAPIImplementation pluginAPI,
                    ProcessAPIImplementation processAPI) {
    this.applicationAPI = applicationAPI;
    this.clientAPI = clientAPI;
    this.consoleAPI = consoleAPI;
    this.databaseAPI = databaseAPI;
    this.groupAPI = groupAPI;
    this.playerAPI = playerAPI;
    this.pluginAPI = pluginAPI;
    this.processAPI = processAPI;
  }

  private final ApplicationAPIImplementation applicationAPI;

  private final ClientAPIImplementation clientAPI;

  private final ConsoleAPIImplementation consoleAPI;

  private final DatabaseAPIImplementation databaseAPI;

  private final GroupAPIImplementation groupAPI;

  private final PlayerAPIImplementation playerAPI;

  private final PluginAPIImplementation pluginAPI;

  private final ProcessAPIImplementation processAPI;

  @Nonnull
  @Override
  public ProcessAsyncAPI getProcessAsyncAPI() {
    return processAPI;
  }

  @Nonnull
  @Override
  public GroupAsyncAPI getGroupAsyncAPI() {
    return groupAPI;
  }

  @Nonnull
  @Override
  public ApplicationAsyncAPI getApplicationAsyncAPI() {
    return applicationAPI;
  }

  @Nonnull
  @Override
  public ConsoleAsyncAPI getConsoleAsyncAPI() {
    return consoleAPI;
  }

  @Nonnull
  @Override
  public PlayerAsyncAPI getPlayerAsyncAPI() {
    return playerAPI;
  }

  @Nonnull
  @Override
  public PluginAsyncAPI getPluginAsyncAPI() {
    return pluginAPI;
  }

  @Nonnull
  @Override
  public ClientAsyncAPI getClientAsyncAPI() {
    return clientAPI;
  }

  @Nonnull
  @Override
  public DatabaseAsyncAPI getDatabaseAsyncAPI() {
    return databaseAPI;
  }

  @Nonnull
  @Override
  public ProcessSyncAPI getProcessSyncAPI() {
    return processAPI;
  }

  @Nonnull
  @Override
  public GroupSyncAPI getGroupSyncAPI() {
    return groupAPI;
  }

  @Nonnull
  @Override
  public ApplicationSyncAPI getApplicationSyncAPI() {
    return applicationAPI;
  }

  @Nonnull
  @Override
  public ConsoleSyncAPI getConsoleSyncAPI() {
    return consoleAPI;
  }

  @Nonnull
  @Override
  public PlayerSyncAPI getPlayerSyncAPI() {
    return playerAPI;
  }

  @Nonnull
  @Override
  public PluginSyncAPI getPluginSyncAPI() {
    return pluginAPI;
  }

  @Nonnull
  @Override
  public ClientSyncAPI getClientSyncAPI() {
    return clientAPI;
  }

  @Nonnull
  @Override
  public DatabaseSyncAPI getDatabaseSyncAPI() {
    return databaseAPI;
  }
}
