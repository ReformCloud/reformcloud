package systems.reformcloud.reformcloud2.executor.api.common.api.basic;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.api.GeneralAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out.*;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.client.ClientSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupEnvironment;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.DefaultMessageJsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ExternalAPIImplementation extends ExecutorAPI implements
        ProcessSyncAPI, ProcessAsyncAPI,
        ApplicationSyncAPI, ApplicationAsyncAPI,
        ClientSyncAPI, ClientAsyncAPI,
        ConsoleSyncAPI, ConsoleAsyncAPI,
        DatabaseSyncAPI, DatabaseAsyncAPI,
        GroupSyncAPI, GroupAsyncAPI,
        PlayerSyncAPI, PlayerAsyncAPI,
        PluginSyncAPI, PluginAsyncAPI,
        MessageSyncAPI, MessageAsyncAPI {

    public static final int EXTERNAL_PACKET_ID = 600;

    private final GeneralAPI generalAPI = new GeneralAPI(this);

    @Override
    public boolean isReady() {
        return DefaultChannelManager.INSTANCE.get("Controller").isPresent();
    }

    @Nonnull
    @Override
    public Task<Boolean> loadApplicationAsync(@Nonnull InstallableApplication application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutLoadApplication(application), packet -> task.complete(packet.content().getBoolean("installed"))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> unloadApplicationAsync(@Nonnull LoadedApplication application) {
        return unloadApplicationAsync(application.applicationConfig().getName());
    }

    @Nonnull
    @Override
    public Task<Boolean> unloadApplicationAsync(@Nonnull String application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutUnloadApplication(application), packet -> task.complete(packet.content().getBoolean("uninstalled"))));
        return task;
    }

    @Nonnull
    @Override
    public Task<LoadedApplication> getApplicationAsync(@Nonnull String name) {
        Task<LoadedApplication> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetLoadedApplication(name), packet -> task.complete(packet.content().get("result", LoadedApplication.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<LoadedApplication>> getApplicationsAsync() {
        Task<List<LoadedApplication>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetApplications(), packet -> task.complete(new ArrayList<>(packet.content().get("result", new TypeToken<List<DefaultLoadedApplication>>() {
        })))));
        return task;
    }

    @Override
    public boolean loadApplication(@Nonnull InstallableApplication application) {
        return loadApplicationAsync(application).getUninterruptedly();
    }

    @Override
    public boolean unloadApplication(@Nonnull LoadedApplication application) {
        return unloadApplicationAsync(application).getUninterruptedly();
    }

    @Override
    public boolean unloadApplication(@Nonnull String application) {
        return unloadApplicationAsync(application).getUninterruptedly();
    }

    @Override
    public LoadedApplication getApplication(@Nonnull String name) {
        return getApplicationAsync(name).getUninterruptedly();
    }

    @Override
    public List<LoadedApplication> getApplications() {
        return getApplicationsAsync().getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<Boolean> isClientConnectedAsync(String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getClientInformationAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5) != null));
        return task;
    }

    @Nonnull
    @Override
    public Task<String> getClientStartHostAsync(String name) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getClientInformationAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5).startHost()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getMaxMemoryAsync(String name) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getClientInformationAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5).maxMemory()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getMaxProcessesAsync(String name) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getClientInformationAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5).maxProcessCount()));
        return task;
    }

    @Nonnull
    @Override
    public Task<ClientRuntimeInformation> getClientInformationAsync(String name) {
        Task<ClientRuntimeInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetClientRuntimeInformation(name), packet -> task.complete(packet.content().get("result", ClientRuntimeInformation.TYPE))));
        return task;
    }

    @Override
    public boolean isClientConnected(@Nonnull String name) {
        return isClientConnectedAsync(name).getUninterruptedly();
    }

    @Override
    public String getClientStartHost(@Nonnull String name) {
        return getClientStartHostAsync(name).getUninterruptedly();
    }

    @Override
    public int getMaxMemory(@Nonnull String name) {
        return getMaxMemoryAsync(name).getUninterruptedly();
    }

    @Override
    public int getMaxProcesses(@Nonnull String name) {
        return getMaxProcessesAsync(name).getUninterruptedly();
    }

    @Override
    public ClientRuntimeInformation getClientInformation(@Nonnull String name) {
        return getClientInformationAsync(name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<Void> sendColouredLineAsync(@Nonnull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutSendColouredLine(line));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> sendRawLineAsync(@Nonnull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutSendRawLine(line));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<String> dispatchCommandAndGetResultAsync(@Nonnull String commandLine) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDispatchControllerCommand(commandLine), packet -> task.complete(packet.content().getString("result"))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Command> getCommandAsync(@Nonnull String name) {
        Task<Command> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetCommand(name), packet -> task.complete(packet.content().get("result", Command.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> isCommandRegisteredAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getCommandAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5) != null));
        return task;
    }

    @Override
    public void sendColouredLine(@Nonnull String line) {
        sendColouredLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public void sendRawLine(@Nonnull String line) {
        sendRawLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public String dispatchCommandAndGetResult(@Nonnull String commandLine) {
        return dispatchCommandAndGetResultAsync(commandLine).getUninterruptedly();
    }

    @Override
    public Command getCommand(@Nonnull String name) {
        return getCommandAsync(name).getUninterruptedly();
    }

    @Override
    public boolean isCommandRegistered(@Nonnull String name) {
        return isCommandRegisteredAsync(name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<JsonConfiguration> findAsync(@Nonnull String table, @Nonnull String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseFindDocument(table, key, identifier), packet -> task.complete(packet.content().get("result"))));
        return task;
    }

    @Nonnull
    @Override
    public <T> Task<T> findAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(function.apply(findAsync(table, key, identifier).getUninterruptedly())));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> insertAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDatabaseInsertDocument(table, key, identifier, data));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> updateAsync(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseUpdateDocument(table, key, newData,true), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> updateIfAbsentAsync(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseUpdateDocument(table, identifier, newData,false), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> removeAsync(@Nonnull String table, @Nonnull String key) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDatabaseRemoveDocument(table, key,true));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> removeIfAbsentAsync(@Nonnull String table, @Nonnull String identifier) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDatabaseRemoveDocument(table, identifier,false));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> createDatabaseAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.CREATE, name), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> deleteDatabaseAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.DELETE, name), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> containsAsync(@Nonnull String table, @Nonnull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseContainsDocument(table, key), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> sizeAsync(@Nonnull String table) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.SIZE, table), packet -> task.complete(packet.content().getInteger("result"))));
        return task;
    }

    @Override
    public JsonConfiguration find(@Nonnull String table, @Nonnull String key, String identifier) {
        return findAsync(table, key, identifier).getUninterruptedly();
    }

    @Override
    public <T> T find(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull Function<JsonConfiguration, T> function) {
        return findAsync(table, key, identifier, function).getUninterruptedly();
    }

    @Override
    public void insert(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
        insertAsync(table, key, identifier, data).awaitUninterruptedly();
    }

    @Override
    public boolean update(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData) {
        return updateAsync(table, key, newData).getUninterruptedly();
    }

    @Override
    public boolean updateIfAbsent(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        return updateIfAbsentAsync(table, identifier, newData).getUninterruptedly();
    }

    @Override
    public void remove(@Nonnull String table, @Nonnull String key) {
        removeAsync(table, key).awaitUninterruptedly();
    }

    @Override
    public void removeIfAbsent(@Nonnull String table, @Nonnull String identifier) {
        removeIfAbsentAsync(table, identifier).awaitUninterruptedly();
    }

    @Override
    public boolean createDatabase(@Nonnull String name) {
        return createDatabaseAsync(name).getUninterruptedly();
    }

    @Override
    public boolean deleteDatabase(@Nonnull String name) {
        return deleteDatabaseAsync(name).getUninterruptedly();
    }

    @Override
    public boolean contains(@Nonnull String table, @Nonnull String key) {
        return containsAsync(table, key).getUninterruptedly();
    }

    @Override
    public int size(@Nonnull String table) {
        return sizeAsync(table).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<MainGroup> createMainGroupAsync(@Nonnull String name) {
        return createMainGroupAsync(name, new ArrayList<>());
    }

    @Nonnull
    @Override
    public Task<MainGroup> createMainGroupAsync(@Nonnull String name, @Nonnull List<String> subgroups) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutCreateMainGroup(new MainGroup(name, subgroups)), packet -> task.complete(packet.content().get("result", MainGroup.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name) {
        return createProcessGroupAsync(name, new ArrayList<>());
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates) {
        return createProcessGroupAsync(name, templates, new StartupConfiguration(
                -1, 1, 1, 41000, StartupEnvironment.JAVA_RUNTIME, true, new ArrayList<>()
        ));
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, new PlayerAccessConfiguration(
                "reformcloud.join.full",false, "reformcloud.join.maintenance",
                false, null, true, true, true, 50
        ));
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration, @Nonnull PlayerAccessConfiguration playerAccessConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, false);
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration, @Nonnull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessGroup processGroup = new ProcessGroup(
                    name,
                    true,
                    startupConfiguration,
                    templates,
                    playerAccessConfiguration,
                    staticGroup
            );
            task.complete(createProcessGroupAsync(processGroup).getUninterruptedly());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutCreateProcessGroup(processGroup), packet -> task.complete(packet.content().get("result", ProcessGroup.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<MainGroup> updateMainGroupAsync(@Nonnull MainGroup mainGroup) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutUpdateMainGroup(mainGroup));
            task.complete(mainGroup);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(@Nonnull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutUpdateProcessGroup(processGroup));
            task.complete(processGroup);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<MainGroup> getMainGroupAsync(@Nonnull String name) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetMainGroup(name), packet -> task.complete(packet.content().get("result", MainGroup.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> getProcessGroupAsync(@Nonnull String name) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcessGroup(name), packet -> task.complete(packet.content().get("result", ProcessGroup.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> deleteMainGroupAsync(@Nonnull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDeleteMainGroup(name));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> deleteProcessGroupAsync(@Nonnull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDeleteProcessGroup(name));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        Task<List<MainGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetMainGroups(), packet -> task.complete(packet.content().get("result", new TypeToken<List<MainGroup>>() {
        }))));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        Task<List<ProcessGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcessGroups(), packet -> task.complete(packet.content().get("result", new TypeToken<List<ProcessGroup>>() {
        }))));
        return task;
    }

    @Nonnull
    @Override
    public MainGroup createMainGroup(@Nonnull String name) {
        return createMainGroupAsync(name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public MainGroup createMainGroup(@Nonnull String name, @Nonnull List<String> subgroups) {
        return createMainGroupAsync(name, subgroups).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name) {
        return createProcessGroupAsync(name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates) {
        return createProcessGroupAsync(name, templates).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration, @Nonnull PlayerAccessConfiguration playerAccessConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration, @Nonnull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        return createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, staticGroup).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull ProcessGroup processGroup) {
        return createProcessGroupAsync(processGroup).getUninterruptedly();
    }

    @Nonnull
    @Override
    public MainGroup updateMainGroup(@Nonnull MainGroup mainGroup) {
        return updateMainGroupAsync(mainGroup).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup updateProcessGroup(@Nonnull ProcessGroup processGroup) {
        return updateProcessGroupAsync(processGroup).getUninterruptedly();
    }

    @Nullable
    @Override
    public MainGroup getMainGroup(@Nonnull String name) {
        return getMainGroupAsync(name).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessGroup getProcessGroup(@Nonnull String name) {
        return getProcessGroupAsync(name).getUninterruptedly();
    }

    @Override
    public void deleteMainGroup(@Nonnull String name) {
        deleteMainGroupAsync(name).awaitUninterruptedly();
    }

    @Override
    public void deleteProcessGroup(@Nonnull String name) {
        deleteProcessGroupAsync(name).awaitUninterruptedly();
    }

    @Nonnull
    @Override
    public List<MainGroup> getMainGroups() {
        return getMainGroupsAsync().getUninterruptedly();
    }

    @Nonnull
    @Override
    public List<ProcessGroup> getProcessGroups() {
        return getProcessGroupsAsync().getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<Void> sendMessageAsync(@Nonnull UUID player, @Nonnull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SEND_MESSAGE, player, message));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> kickPlayerAsync(@Nonnull UUID player, @Nonnull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.KICK_PLAYER, player, message));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> kickPlayerFromServerAsync(@Nonnull UUID player, @Nonnull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.KICK_SERVER, player, message));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> playSoundAsync(@Nonnull UUID player, @Nonnull String sound, float f1, float f2) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_SOUND, player, sound, f1, f2));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> sendTitleAsync(@Nonnull UUID player, @Nonnull String title, @Nonnull String subTitle, int fadeIn, int stay, int fadeOut) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SEND_TITLE, player, title, subTitle, fadeIn, stay, fadeOut));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> playEffectAsync(@Nonnull UUID player, @Nonnull String entityEffect) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_ENTITY_EFFECT, player, entityEffect));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public <T> Task<Void> playEffectAsync(@Nonnull UUID player, @Nonnull String effect, T data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_EFFECT, player, effect, data));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> respawnAsync(@Nonnull UUID player) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.RESPAWN, player));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> teleportAsync(@Nonnull UUID player, @Nonnull String world, double x, double y, double z, float yaw, float pitch) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.LOCATION_TELEPORT, player, world, x, y, z, yaw, pitch));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> connectAsync(@Nonnull UUID player, @Nonnull String server) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.CONNECT, player, server));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> connectAsync(@Nonnull UUID player, @Nonnull ProcessInformation server) {
        return connectAsync(player, server.getName());
    }

    @Nonnull
    @Override
    public Task<Void> connectAsync(@Nonnull UUID player, @Nonnull UUID target) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.CONNECT_PLAYER, player, target));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> setResourcePackAsync(@Nonnull UUID player, @Nonnull String pack) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SET_RESOURCE_PACK, player, pack));
            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendMessage(@Nonnull UUID player, @Nonnull String message) {
        sendMessageAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayer(@Nonnull UUID player, @Nonnull String message) {
        kickPlayerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayerFromServer(@Nonnull UUID player, @Nonnull String message) {
        kickPlayerFromServerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void playSound(@Nonnull UUID player, @Nonnull String sound, float f1, float f2) {
        playSoundAsync(player, sound, f1, f2).awaitUninterruptedly();
    }

    @Override
    public void sendTitle(@Nonnull UUID player, @Nonnull String title, @Nonnull String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitleAsync(player, title, subTitle, fadeIn, stay, fadeOut).awaitUninterruptedly();
    }

    @Override
    public void playEffect(@Nonnull UUID player, @Nonnull String entityEffect) {
        playEffectAsync(player, entityEffect).awaitUninterruptedly();
    }

    @Override
    public <T> void playEffect(@Nonnull UUID player, @Nonnull String effect, T data) {
        playEffectAsync(player, effect, data).awaitUninterruptedly();
    }

    @Override
    public void respawn(@Nonnull UUID player) {
        respawnAsync(player).awaitUninterruptedly();
    }

    @Override
    public void teleport(@Nonnull UUID player, @Nonnull String world, double x, double y, double z, float yaw, float pitch) {
        teleportAsync(player, world, x, y, z, yaw, pitch).awaitUninterruptedly();
    }

    @Override
    public void connect(@Nonnull UUID player, @Nonnull String server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@Nonnull UUID player, @Nonnull ProcessInformation server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@Nonnull UUID player, @Nonnull UUID target) {
        connectAsync(player, target).awaitUninterruptedly();
    }

    @Override
    public void setResourcePack(@Nonnull UUID player, @Nonnull String pack) {
        setResourcePackAsync(player, pack).awaitUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutInstallPlugin(plugin, process));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        return installPluginAsync(process.getName(), plugin);
    }

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull String process, @Nonnull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutUnloadPlugin(plugin, process));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        return unloadPluginAsync(process.getName(), plugin);
    }

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull String process, @Nonnull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetInstalledPlugin(name, process), packet -> task.complete(packet.content().get("result", Plugin.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull ProcessInformation process, @Nonnull String name) {
        return getInstalledPluginAsync(process.getName(), name);
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process, @Nonnull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.allOf(getPlugins(process), plugin -> plugin.author().equals(author))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation process, @Nonnull String author) {
        return getPluginsAsync(process.getName(), author);
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetPlugins(process), packet -> task.complete(packet.content().get("result", new TypeToken<Collection<DefaultPlugin>>() {
        }))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation.getName());
    }

    @Override
    public void installPlugin(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void installPlugin(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@Nonnull String process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull String process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull ProcessInformation process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process) {
        return getPluginsAsync(process).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName) {
        return startProcessAsync(groupName, null);
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, String template) {
        return startProcessAsync(groupName, template, new JsonConfiguration());
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, String template, @Nonnull JsonConfiguration configurable) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutStartProcess(groupName, template, configurable), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutStopProcess(name), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutStopProcess(uniqueID), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcess(name), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcess(uniqueID), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcesses(), packet -> task.complete(packet.content().get("result", new TypeToken<List<ProcessInformation>>() {
        }))));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(@Nonnull String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcesses(group), packet -> task.complete(packet.content().get("result", new TypeToken<List<ProcessInformation>>() {
        }))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> executeProcessCommandAsync(@Nonnull String name, @Nonnull String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutExecuteProcessCommand(name, commandLine));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getGlobalOnlineCountAsync(@Nonnull Collection<String> ignoredProxies) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetOnlineCount(ignoredProxies), packet -> task.complete(packet.content().getInteger("result"))));
        return task;
    }

    @Override
    public Task<Void> updateAsync(@Nonnull ProcessInformation processInformation) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutUpdateProcessInformation(processInformation));
            task.complete(null);
        });
        return task;
    }

    @Nullable
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName) {
        return startProcessAsync(groupName).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName, String template) {
        return startProcessAsync(groupName, template).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName, String template, @Nonnull JsonConfiguration configurable) {
        return startProcessAsync(groupName, template, configurable).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(@Nonnull String name) {
        return stopProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(@Nonnull UUID uniqueID) {
        return stopProcessAsync(uniqueID).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@Nonnull String name) {
        return getProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@Nonnull UUID uniqueID) {
        return getProcessAsync(uniqueID).getUninterruptedly();
    }

    @Nonnull
    @Override
    public List<ProcessInformation> getAllProcesses() {
        return getAllProcessesAsync().getUninterruptedly();
    }

    @Nonnull
    @Override
    public List<ProcessInformation> getProcesses(@Nonnull String group) {
        return getProcessesAsync(group).getUninterruptedly();
    }

    @Override
    public void executeProcessCommand(@Nonnull String name, @Nonnull String commandLine) {
        executeProcessCommandAsync(name, commandLine).awaitUninterruptedly();
    }

    @Override
    public int getGlobalOnlineCount(@Nonnull Collection<String> ignoredProxies) {
        return getGlobalOnlineCountAsync(ignoredProxies).getUninterruptedly();
    }

    @Override
    public void update(@Nonnull ProcessInformation processInformation) {
        updateAsync(processInformation).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getThisProcessInformationAsync() {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getThisProcessInformation()));
        return task;
    }

    @Nonnull
    @Override
    public PacketHandler getPacketHandler() {
        return packetHandler();
    }

    @Nonnull
    @Override
    public Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull String baseChannel,
                                              @Nonnull String subChannel, @Nonnull ErrorReportHandling errorReportHandling, @Nonnull String... receivers) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(
                    new DefaultMessageJsonPacket(jsonConfiguration, Arrays.asList(receivers), errorReportHandling, baseChannel, subChannel)
            ));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration configuration, @Nonnull String baseChannel,
                                              @Nonnull String subChannel, @Nonnull ReceiverType... receiverTypes) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(
                    new DefaultMessageJsonPacket(Arrays.asList(receiverTypes), configuration, baseChannel, subChannel)
            ));
            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendChannelMessageSync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull String baseChannel,
                                       @Nonnull String subChannel, @Nonnull ErrorReportHandling errorReportHandling, @Nonnull String... receivers) {
        sendChannelMessageAsync(jsonConfiguration, baseChannel, subChannel, errorReportHandling, receivers).awaitUninterruptedly();
    }

    @Override
    public void sendChannelMessageSync(@Nonnull JsonConfiguration configuration, @Nonnull String baseChannel, @Nonnull String subChannel, @Nonnull ReceiverType... receiverTypes) {
        sendChannelMessageAsync(configuration, baseChannel, subChannel, receiverTypes).awaitUninterruptedly();
    }

    @Nonnull
    @Override
    public SyncAPI getSyncAPI() {
        return generalAPI;
    }

    @Nonnull
    @Override
    public AsyncAPI getAsyncAPI() {
        return generalAPI;
    }

    /* ============== */

    protected abstract PacketHandler packetHandler();

    private void sendPacket(Packet packet) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(packet));
    }

    private void sendPacketQuery(Packet packet, Consumer<Packet> result) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetHandler().getQueryHandler().sendQueryAsync(packetSender, packet).getTask().onFailure(e -> result.accept(null)).onComplete(result));
    }
}
