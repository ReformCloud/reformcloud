package systems.reformcloud.reformcloud2.executor.api.common.api.basic;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.api.GeneralAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.PacketAPIGroupCreateMainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.PacketAPIGroupCreateProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query.PacketAPIQueryRequestMainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query.PacketAPIQueryRequestProcessByName;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query.PacketAPIQueryRequestProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out.*;
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
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupEnvironment;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.TypeMessagePacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ExternalAPIImplementation extends ExecutorAPI implements
        ProcessSyncAPI, ProcessAsyncAPI,
        ApplicationSyncAPI, ApplicationAsyncAPI,
        ConsoleSyncAPI, ConsoleAsyncAPI,
        DatabaseSyncAPI, DatabaseAsyncAPI,
        GroupSyncAPI, GroupAsyncAPI,
        PlayerSyncAPI, PlayerAsyncAPI,
        PluginSyncAPI, PluginAsyncAPI,
        MessageSyncAPI, MessageAsyncAPI {

    public static final int EXTERNAL_PACKET_ID = 600;

    public static final int EXTERNAL_PACKET_QUERY_RESULT_ID = 800;

    private final GeneralAPI generalAPI = new GeneralAPI(this);

    @Override
    public boolean isReady() {
        return DefaultChannelManager.INSTANCE.get("Controller").isPresent();
    }

    @NotNull
    @Override
    public Task<ProcessInformation> startProcessAsync(@NotNull ProcessInformation processInformation) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutStartProcess(processInformation), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> prepareProcessAsync(@NotNull ProcessConfiguration configuration) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutStartProcess(configuration, false), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public ProcessInformation startProcess(@NotNull ProcessInformation processInformation) {
        ProcessInformation information = this.startProcessAsync(processInformation).getUninterruptedly(TimeUnit.SECONDS, 5);
        return information == null ? processInformation : information;
    }

    @Nullable
    @Override
    public ProcessInformation prepareProcess(@NotNull ProcessConfiguration configuration) {
        return this.prepareProcessAsync(configuration).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @NotNull
    @Override
    public Task<Boolean> loadApplicationAsync(@NotNull InstallableApplication application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutLoadApplication(application), packet -> task.complete(packet.content().getBoolean("installed"))));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> unloadApplicationAsync(@NotNull LoadedApplication application) {
        return unloadApplicationAsync(application.applicationConfig().getName());
    }

    @NotNull
    @Override
    public Task<Boolean> unloadApplicationAsync(@NotNull String application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutUnloadApplication(application), packet -> task.complete(packet.content().getBoolean("uninstalled"))));
        return task;
    }

    @NotNull
    @Override
    public Task<LoadedApplication> getApplicationAsync(@NotNull String name) {
        Task<LoadedApplication> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetLoadedApplication(name), packet -> task.complete(packet.content().get("result", LoadedApplication.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<List<LoadedApplication>> getApplicationsAsync() {
        Task<List<LoadedApplication>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetApplications(), packet -> task.complete(new ArrayList<>(packet.content().getOrDefault("result", new TypeToken<List<DefaultLoadedApplication>>() {
        }.getType(), new ArrayList<>())))));
        return task;
    }

    @Override
    public boolean loadApplication(@NotNull InstallableApplication application) {
        Boolean result = loadApplicationAsync(application).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean unloadApplication(@NotNull LoadedApplication application) {
        Boolean result = unloadApplicationAsync(application).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean unloadApplication(@NotNull String application) {
        Boolean result = unloadApplicationAsync(application).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public LoadedApplication getApplication(@NotNull String name) {
        return getApplicationAsync(name).getUninterruptedly();
    }

    @Override
    public List<LoadedApplication> getApplications() {
        return getApplicationsAsync().getUninterruptedly();
    }

    @NotNull
    @Override
    public Task<Void> sendColouredLineAsync(@NotNull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutSendColouredLine(line));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> sendRawLineAsync(@NotNull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutSendRawLine(line));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<String> dispatchCommandAndGetResultAsync(@NotNull String commandLine) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDispatchControllerCommand(commandLine), packet -> task.complete("SUCESS")));
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<String>> dispatchConsoleCommandAndGetResultAsync(@NotNull String commandLine) {
        Task<Collection<String>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new ExternalAPIPacketOutDispatchControllerCommand(commandLine),
                result -> {
                    Collection<String> messages = result.content().get("result", new TypeToken<Collection<String>>() {
                    });
                    if (messages == null) {
                        messages = new ArrayList<>();
                    }

                    task.complete(messages);
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<Command> getCommandAsync(@NotNull String name) {
        Task<Command> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetCommand(name), packet -> task.complete(packet.content().get("result", Command.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> isCommandRegisteredAsync(@NotNull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getCommandAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5) != null));
        return task;
    }

    @Override
    public void sendColouredLine(@NotNull String line) {
        sendColouredLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public void sendRawLine(@NotNull String line) {
        sendRawLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public String dispatchCommandAndGetResult(@NotNull String commandLine) {
        return dispatchCommandAndGetResultAsync(commandLine).getUninterruptedly();
    }

    @NotNull
    @Override
    public Collection<String> dispatchConsoleCommandAndGetResult(@NotNull String commandLine) {
        Collection<String> result = this.dispatchConsoleCommandAndGetResultAsync(commandLine).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @Override
    public Command getCommand(@NotNull String name) {
        return getCommandAsync(name).getUninterruptedly();
    }

    @Override
    public boolean isCommandRegistered(@NotNull String name) {
        Boolean result = isCommandRegisteredAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> findAsync(@NotNull String table, @NotNull String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseFindDocument(table, key, identifier), packet -> task.complete(packet.content().get("result"))));
        return task;
    }

    @NotNull
    @Override
    public <T> Task<T> findAsync(@NotNull String table, @NotNull String key, String identifier, @NotNull Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(function.apply(findAsync(table, key, identifier).getUninterruptedly())));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> insertAsync(@NotNull String table, @NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDatabaseInsertDocument(table, key, identifier, data));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> updateAsync(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseUpdateDocument(table, key, newData, true), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> updateIfAbsentAsync(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseUpdateDocument(table, identifier, newData, false), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> removeAsync(@NotNull String table, @NotNull String key) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDatabaseRemoveDocument(table, key, true));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsentAsync(@NotNull String table, @NotNull String identifier) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDatabaseRemoveDocument(table, identifier, false));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> createDatabaseAsync(@NotNull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.CREATE, name), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> deleteDatabaseAsync(@NotNull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.DELETE, name), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> containsAsync(@NotNull String table, @NotNull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseContainsDocument(table, key), packet -> task.complete(packet.content().getBoolean("result"))));
        return task;
    }

    @NotNull
    @Override
    public Task<Integer> sizeAsync(@NotNull String table) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.SIZE, table), packet -> task.complete(packet.content().getInteger("result"))));
        return task;
    }

    @Override
    public JsonConfiguration find(@NotNull String table, @NotNull String key, String identifier) {
        return findAsync(table, key, identifier).getUninterruptedly();
    }

    @Override
    public <T> T find(@NotNull String table, @NotNull String key, String identifier, @NotNull Function<JsonConfiguration, T> function) {
        return findAsync(table, key, identifier, function).getUninterruptedly();
    }

    @Override
    public void insert(@NotNull String table, @NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        insertAsync(table, key, identifier, data).awaitUninterruptedly();
    }

    @Override
    public boolean update(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData) {
        Boolean result = updateAsync(table, key, newData).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean updateIfAbsent(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData) {
        Boolean result = updateIfAbsentAsync(table, identifier, newData).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public void remove(@NotNull String table, @NotNull String key) {
        removeAsync(table, key).awaitUninterruptedly();
    }

    @Override
    public void removeIfAbsent(@NotNull String table, @NotNull String identifier) {
        removeIfAbsentAsync(table, identifier).awaitUninterruptedly();
    }

    @Override
    public boolean createDatabase(@NotNull String name) {
        Boolean result = createDatabaseAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean deleteDatabase(@NotNull String name) {
        Boolean result = deleteDatabaseAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean contains(@NotNull String table, @NotNull String key) {
        Boolean result = containsAsync(table, key).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public int size(@NotNull String table) {
        Integer result = sizeAsync(table).getUninterruptedly();
        return result == null ? 0 : result;
    }

    @NotNull
    @Override
    public Task<MainGroup> createMainGroupAsync(@NotNull String name) {
        return createMainGroupAsync(name, new ArrayList<>());
    }

    @NotNull
    @Override
    public Task<MainGroup> createMainGroupAsync(@NotNull String name, @NotNull List<String> subgroups) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new PacketAPIGroupCreateMainGroup(new MainGroup(name, subgroups)), packet -> task.complete(packet.content().get("result", MainGroup.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name) {
        return createProcessGroupAsync(name, new ArrayList<>());
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates) {
        return createProcessGroupAsync(name, templates, new StartupConfiguration(
                -1, 1, 1, 41000, StartupEnvironment.JAVA_RUNTIME, true, new ArrayList<>()
        ));
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, new PlayerAccessConfiguration(
                "reformcloud.join.full", false, "reformcloud.join.maintenance",
                false, null, true, true, 50
        ));
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, false);
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
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

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new PacketAPIGroupCreateProcessGroup(processGroup), packet -> task.complete(packet.content().get("result", ProcessGroup.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<MainGroup> updateMainGroupAsync(@NotNull MainGroup mainGroup) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutUpdateMainGroup(mainGroup));
            task.complete(mainGroup);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutUpdateProcessGroup(processGroup));
            task.complete(processGroup);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<MainGroup> getMainGroupAsync(@NotNull String name) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new PacketAPIQueryRequestMainGroup(name), packet -> task.complete(packet.content().get("result", MainGroup.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> getProcessGroupAsync(@NotNull String name) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new PacketAPIQueryRequestProcessGroup(name), packet -> task.complete(packet.content().get("result", ProcessGroup.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> deleteMainGroupAsync(@NotNull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDeleteMainGroup(name));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> deleteProcessGroupAsync(@NotNull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutDeleteProcessGroup(name));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        Task<List<MainGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetMainGroups(), packet -> task.complete(packet.content().get("result", new TypeToken<List<MainGroup>>() {
        }))));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        Task<List<ProcessGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcessGroups(), packet -> task.complete(packet.content().get("result", new TypeToken<List<ProcessGroup>>() {
        }))));
        return task;
    }

    @NotNull
    @Override
    public MainGroup createMainGroup(@NotNull String name) {
        return Objects.requireNonNull(createMainGroupAsync(name).getUninterruptedly());
    }

    @NotNull
    @Override
    public MainGroup createMainGroup(@NotNull String name, @NotNull List<String> subgroups) {
        return Objects.requireNonNull(createMainGroupAsync(name, subgroups).getUninterruptedly());
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name) {
        return Objects.requireNonNull(createProcessGroupAsync(name).getUninterruptedly());
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates) {
        return Objects.requireNonNull(createProcessGroupAsync(name, templates).getUninterruptedly());
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration) {
        return Objects.requireNonNull(createProcessGroupAsync(name, templates, startupConfiguration).getUninterruptedly());
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration) {
        return Objects.requireNonNull(createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration).getUninterruptedly());
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        return Objects.requireNonNull(createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, staticGroup).getUninterruptedly());
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull ProcessGroup processGroup) {
        return Objects.requireNonNull(createProcessGroupAsync(processGroup).getUninterruptedly());
    }

    @NotNull
    @Override
    public MainGroup updateMainGroup(@NotNull MainGroup mainGroup) {
        return Objects.requireNonNull(updateMainGroupAsync(mainGroup).getUninterruptedly());
    }

    @NotNull
    @Override
    public ProcessGroup updateProcessGroup(@NotNull ProcessGroup processGroup) {
        return Objects.requireNonNull(updateProcessGroupAsync(processGroup).getUninterruptedly());
    }

    @Nullable
    @Override
    public MainGroup getMainGroup(@NotNull String name) {
        return getMainGroupAsync(name).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessGroup getProcessGroup(@NotNull String name) {
        return getProcessGroupAsync(name).getUninterruptedly();
    }

    @Override
    public void deleteMainGroup(@NotNull String name) {
        deleteMainGroupAsync(name).awaitUninterruptedly();
    }

    @Override
    public void deleteProcessGroup(@NotNull String name) {
        deleteProcessGroupAsync(name).awaitUninterruptedly();
    }

    @NotNull
    @Override
    public List<MainGroup> getMainGroups() {
        List<MainGroup> result = getMainGroupsAsync().getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public List<ProcessGroup> getProcessGroups() {
        List<ProcessGroup> result = getProcessGroupsAsync().getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Task<Void> sendMessageAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SEND_MESSAGE, player, message));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> kickPlayerAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.KICK_PLAYER, player, message));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> kickPlayerFromServerAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.KICK_SERVER, player, message));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> playSoundAsync(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_SOUND, player, sound, f1, f2));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> sendTitleAsync(@NotNull UUID player, @NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SEND_TITLE, player, title, subTitle, fadeIn, stay, fadeOut));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> playEffectAsync(@NotNull UUID player, @NotNull String entityEffect) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_ENTITY_EFFECT, player, entityEffect));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public <T> Task<Void> playEffectAsync(@NotNull UUID player, @NotNull String effect, T data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_EFFECT, player, effect, data));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> respawnAsync(@NotNull UUID player) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.RESPAWN, player));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> teleportAsync(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.LOCATION_TELEPORT, player, world, x, y, z, yaw, pitch));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull String server) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.CONNECT, player, server));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull ProcessInformation server) {
        return connectAsync(player, server.getProcessDetail().getName());
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull UUID target) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.CONNECT_PLAYER, player, target));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> setResourcePackAsync(@NotNull UUID player, @NotNull String pack) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SET_RESOURCE_PACK, player, pack));
            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendMessage(@NotNull UUID player, @NotNull String message) {
        sendMessageAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayer(@NotNull UUID player, @NotNull String message) {
        kickPlayerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayerFromServer(@NotNull UUID player, @NotNull String message) {
        kickPlayerFromServerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void playSound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
        playSoundAsync(player, sound, f1, f2).awaitUninterruptedly();
    }

    @Override
    public void sendTitle(@NotNull UUID player, @NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitleAsync(player, title, subTitle, fadeIn, stay, fadeOut).awaitUninterruptedly();
    }

    @Override
    public void playEffect(@NotNull UUID player, @NotNull String entityEffect) {
        playEffectAsync(player, entityEffect).awaitUninterruptedly();
    }

    @Override
    public <T> void playEffect(@NotNull UUID player, @NotNull String effect, T data) {
        playEffectAsync(player, effect, data).awaitUninterruptedly();
    }

    @Override
    public void respawn(@NotNull UUID player) {
        respawnAsync(player).awaitUninterruptedly();
    }

    @Override
    public void teleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
        teleportAsync(player, world, x, y, z, yaw, pitch).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull String server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull ProcessInformation server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull UUID target) {
        connectAsync(player, target).awaitUninterruptedly();
    }

    @Override
    public void setResourcePack(@NotNull UUID player, @NotNull String pack) {
        setResourcePackAsync(player, pack).awaitUninterruptedly();
    }

    @NotNull
    @Override
    public Task<Void> installPluginAsync(@NotNull String process, @NotNull InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutInstallPlugin(plugin, process));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> installPluginAsync(@NotNull ProcessInformation process, @NotNull InstallablePlugin plugin) {
        return installPluginAsync(process.getProcessDetail().getName(), plugin);
    }

    @NotNull
    @Override
    public Task<Void> unloadPluginAsync(@NotNull String process, @NotNull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutUnloadPlugin(plugin, process));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> unloadPluginAsync(@NotNull ProcessInformation process, @NotNull Plugin plugin) {
        return unloadPluginAsync(process.getProcessDetail().getName(), plugin);
    }

    @NotNull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@NotNull String process, @NotNull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetInstalledPlugin(name, process), packet -> task.complete(packet.content().get("result", Plugin.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@NotNull ProcessInformation process, @NotNull String name) {
        return getInstalledPluginAsync(process.getProcessDetail().getName(), name);
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull String process, @NotNull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.allOf(getPlugins(process), plugin -> plugin.author() != null && plugin.author().equals(author))));
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull ProcessInformation process, @NotNull String author) {
        return getPluginsAsync(process.getProcessDetail().getName(), author);
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull String process) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetPlugins(process), packet -> task.complete(packet.content().get("result", new TypeToken<Collection<DefaultPlugin>>() {
        }))));
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation.getProcessDetail().getName());
    }

    @Override
    public void installPlugin(@NotNull String process, @NotNull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void installPlugin(@NotNull ProcessInformation process, @NotNull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@NotNull String process, @NotNull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@NotNull ProcessInformation process, @NotNull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@NotNull String process, @NotNull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@NotNull ProcessInformation process, @NotNull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @NotNull
    @Override
    public Collection<DefaultPlugin> getPlugins(@NotNull String process, @NotNull String author) {
        Collection<DefaultPlugin> result = getPluginsAsync(process, author).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Collection<DefaultPlugin> getPlugins(@NotNull ProcessInformation process, @NotNull String author) {
        Collection<DefaultPlugin> result = getPluginsAsync(process, author).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Collection<DefaultPlugin> getPlugins(@NotNull String process) {
        Collection<DefaultPlugin> result = getPluginsAsync(process).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Collection<DefaultPlugin> getPlugins(@NotNull ProcessInformation processInformation) {
        Collection<DefaultPlugin> result = getPluginsAsync(processInformation).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> startProcessAsync(@NotNull ProcessConfiguration processConfiguration) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutStartProcess(processConfiguration, true), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@NotNull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutStopProcess(name), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@NotNull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutStopProcess(uniqueID), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> getProcessAsync(@NotNull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new PacketAPIQueryRequestProcessByName(name), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> getProcessAsync(@NotNull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new PacketAPIQueryRequestProcessByName(uniqueID), packet -> task.complete(packet.content().get("result", ProcessInformation.TYPE))));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcesses(), packet -> task.complete(packet.content().get("result", new TypeToken<List<ProcessInformation>>() {
        }))));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.sendPacket(new ExternalAPIPacketOutCopyProcess(name, null, null, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId) {
        return Task.supply(() -> {
            this.sendPacket(new ExternalAPIPacketOutCopyProcess(processUniqueId, null, null, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate) {
        return Task.supply(() -> {
            this.sendPacket(new ExternalAPIPacketOutCopyProcess(name, targetTemplate, null, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate) {
        return Task.supply(() -> {
            this.sendPacket(new ExternalAPIPacketOutCopyProcess(processUniqueId, targetTemplate, null, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        return Task.supply(() -> {
            this.sendPacket(new ExternalAPIPacketOutCopyProcess(name, targetTemplate, targetTemplateStorage, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        return Task.supply(() -> {
            this.sendPacket(new ExternalAPIPacketOutCopyProcess(processUniqueId, targetTemplate, targetTemplateStorage, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        return Task.supply(() -> {
            this.sendPacket(new ExternalAPIPacketOutCopyProcess(name, targetTemplate, targetTemplateStorage, targetTemplateGroup));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        return Task.supply(() -> {
            this.sendPacket(new ExternalAPIPacketOutCopyProcess(processUniqueId, targetTemplate, targetTemplateStorage, targetTemplateGroup));
            return null;
        });
    }

    @Override
    public void copyProcess(@NotNull String name, @NotNull String targetTemplate) {
        this.copyProcessAsync(name, targetTemplate).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull UUID processUniqueId, @NotNull String targetTemplate) {
        this.copyProcessAsync(processUniqueId, targetTemplate).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        this.copyProcessAsync(name, targetTemplate, targetTemplateStorage).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        this.copyProcessAsync(processUniqueId, targetTemplate, targetTemplateStorage).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        this.copyProcessAsync(name, targetTemplate, targetTemplateStorage, targetTemplateGroup).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        this.copyProcessAsync(processUniqueId, targetTemplate, targetTemplateStorage, targetTemplateGroup).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull String name) {
        this.copyProcessAsync(name).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull UUID processUniqueId) {
        this.copyProcessAsync(processUniqueId).awaitUninterruptedly();
    }

    @NotNull
    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(@NotNull String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetProcesses(group), packet -> task.complete(packet.content().get("result", new TypeToken<List<ProcessInformation>>() {
        }))));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> executeProcessCommandAsync(@NotNull String name, @NotNull String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutExecuteProcessCommand(name, commandLine));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Integer> getGlobalOnlineCountAsync(@NotNull Collection<String> ignoredProxies) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(new ExternalAPIPacketOutGetOnlineCount(ignoredProxies), packet -> task.complete(packet.content().getInteger("result"))));
        return task;
    }

    @Override
    public Task<Void> updateAsync(@NotNull ProcessInformation processInformation) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            sendPacket(new ExternalAPIPacketOutUpdateProcessInformation(processInformation));
            task.complete(null);
        });
        return task;
    }

    @Nullable
    @Override
    public ProcessInformation startProcess(@NotNull ProcessConfiguration processConfiguration) {
        return startProcessAsync(processConfiguration).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(@NotNull String name) {
        return stopProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(@NotNull UUID uniqueID) {
        return stopProcessAsync(uniqueID).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@NotNull String name) {
        return getProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@NotNull UUID uniqueID) {
        return getProcessAsync(uniqueID).getUninterruptedly();
    }

    @NotNull
    @Override
    public List<ProcessInformation> getAllProcesses() {
        List<ProcessInformation> result = getAllProcessesAsync().getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public List<ProcessInformation> getProcesses(@NotNull String group) {
        List<ProcessInformation> result = getProcessesAsync(group).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @Override
    public void executeProcessCommand(@NotNull String name, @NotNull String commandLine) {
        executeProcessCommandAsync(name, commandLine).awaitUninterruptedly();
    }

    @Override
    public int getGlobalOnlineCount(@NotNull Collection<String> ignoredProxies) {
        Integer result = getGlobalOnlineCountAsync(ignoredProxies).getUninterruptedly();
        return result == null ? 0 : result;
    }

    @Override
    public void update(@NotNull ProcessInformation processInformation) {
        updateAsync(processInformation).getUninterruptedly();
    }

    @NotNull
    @Override
    public PacketHandler getPacketHandler() {
        return packetHandler();
    }

    @NotNull
    @Override
    public Task<Void> sendChannelMessageAsync(@NotNull JsonConfiguration jsonConfiguration, @NotNull String baseChannel,
                                              @NotNull String subChannel, @NotNull ErrorReportHandling errorReportHandling, @NotNull String... receivers) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(
                    new TypeMessagePacket(jsonConfiguration, Arrays.asList(receivers), errorReportHandling, baseChannel, subChannel)
            ));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> sendChannelMessageAsync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel,
                                              @NotNull String subChannel, @NotNull ReceiverType... receiverTypes) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(
                    new TypeMessagePacket(Arrays.asList(receiverTypes), configuration, baseChannel, subChannel)
            ));
            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendChannelMessageSync(@NotNull JsonConfiguration jsonConfiguration, @NotNull String baseChannel,
                                       @NotNull String subChannel, @NotNull ErrorReportHandling errorReportHandling, @NotNull String... receivers) {
        sendChannelMessageAsync(jsonConfiguration, baseChannel, subChannel, errorReportHandling, receivers).awaitUninterruptedly();
    }

    @Override
    public void sendChannelMessageSync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel, @NotNull String subChannel, @NotNull ReceiverType... receiverTypes) {
        sendChannelMessageAsync(configuration, baseChannel, subChannel, receiverTypes).awaitUninterruptedly();
    }

    @NotNull
    @Override
    public SyncAPI getSyncAPI() {
        return generalAPI;
    }

    @NotNull
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
