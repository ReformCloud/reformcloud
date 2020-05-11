/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.api.basic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.*;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query.*;
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
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server.PacketOutServerChallengeStart;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server.PacketOutServerGrantAccess;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.exception.WrongResultTypeException;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.NamedMessagePacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.ProxiedChannelMessage;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.TypeMessagePacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ExternalAPIImplementation extends ExecutorAPI implements
        ProcessSyncAPI, ProcessAsyncAPI,
        ConsoleSyncAPI, ConsoleAsyncAPI,
        DatabaseSyncAPI, DatabaseAsyncAPI,
        GroupSyncAPI, GroupAsyncAPI,
        PlayerSyncAPI, PlayerAsyncAPI,
        MessageSyncAPI, MessageAsyncAPI {

    protected void loadPacketHandlers() {
        this.packetHandler().registerNetworkHandlers(
                // API
                PacketAPIQueryCommandDispatchResult.class,
                PacketAPIQueryDatabaseContainsResult.class,
                PacketAPIQueryDatabaseGetDocumentResult.class,
                PacketAPIQueryGetDatabaseSizeResult.class,
                PacketAPIQueryGetMainGroupsResult.class,
                PacketAPIQueryGetProcessesResult.class,
                PacketAPIQueryGetProcessGroupsResult.class,
                PacketAPIQueryProcessStartNewResult.class,
                PacketAPIQueryProcessStartPreparedResult.class,
                PacketAPIQueryProcessStopResult.class,
                PacketAPIQueryRequestMainGroupResult.class,
                PacketAPIQueryRequestProcessGroupResult.class,
                PacketAPIQueryRequestProcessResult.class,
                // Auth
                PacketOutServerChallengeStart.class,
                PacketOutServerGrantAccess.class,
                // Channel messages
                ProxiedChannelMessage.class
        );
    }

    public static final int EXTERNAL_PACKET_ID = 600;

    public static final int EXTERNAL_PACKET_QUERY_RESULT_ID = 900;

    private final GeneralAPI generalAPI = new GeneralAPI(this);

    @Override
    public boolean isReady() {
        return DefaultChannelManager.INSTANCE.get("Controller").isPresent();
    }

    @NotNull
    @Override
    public Task<ProcessInformation> startProcessAsync(@NotNull ProcessInformation processInformation) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryProcessStartPrepared(processInformation.getProcessDetail().getProcessUniqueID()),
                packet -> {
                    if (packet instanceof PacketAPIQueryProcessStartPreparedResult) {
                        task.complete(((PacketAPIQueryProcessStartPreparedResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> prepareProcessAsync(@NotNull ProcessConfiguration configuration) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryProcessStartNew(configuration, false),
                packet -> {
                    if (packet instanceof PacketAPIQueryProcessStartNewResult) {
                        task.complete(((PacketAPIQueryProcessStartNewResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
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
    public Task<String> dispatchCommandAndGetResultAsync(@NotNull String commandLine) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryCommandDispatch(commandLine),
                packet -> {
                    if (packet instanceof PacketAPIQueryCommandDispatchResult) {
                        task.complete("SUCCESS");
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<String>> dispatchConsoleCommandAndGetResultAsync(@NotNull String commandLine) {
        Task<Collection<String>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryCommandDispatch(commandLine),
                packet -> {
                    if (packet instanceof PacketAPIQueryCommandDispatchResult) {
                        task.complete(((PacketAPIQueryCommandDispatchResult) packet).getResult());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @Override
    public String dispatchCommandAndGetResult(@NotNull String commandLine) {
        return this.dispatchCommandAndGetResultAsync(commandLine).getUninterruptedly();
    }

    @NotNull
    @Override
    public Collection<String> dispatchConsoleCommandAndGetResult(@NotNull String commandLine) {
        Collection<String> result = this.dispatchConsoleCommandAndGetResultAsync(commandLine).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> findAsync(@NotNull String table, @NotNull String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryDatabaseGetDocument(table, key, identifier),
                packet -> {
                    if (packet instanceof PacketAPIQueryDatabaseGetDocumentResult) {
                        task.complete(((PacketAPIQueryDatabaseGetDocumentResult) packet).getResult());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public <T> Task<T> findAsync(@NotNull String table, @NotNull String key, String identifier, @NotNull Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(function.apply(this.findAsync(table, key, identifier).getUninterruptedly())));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> insertAsync(@NotNull String table, @NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.sendPacket(new PacketAPIDatabaseInsertDocument(table, key, identifier, data));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Deprecated
    @Override
    public Task<Boolean> updateAsync(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData) {
        this.update(table, key, null, newData);
        return Task.completedTask(true);
    }

    @NotNull
    @Deprecated
    @Override
    public Task<Boolean> updateIfAbsentAsync(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData) {
        this.update(table, null, identifier, newData);
        return Task.completedTask(true);
    }

    @NotNull
    @Override
    public Task<Void> updateAsync(@NotNull String table, @Nullable String key, @Nullable String identifier, @NotNull JsonConfiguration newData) {
        Conditions.isTrue(key != null || identifier != null, "Can only update data with key or identifier given");

        return Task.supply(() -> {
            this.sendPacket(new PacketAPIDatabaseUpdateOrInsertDocument(table, key, identifier, newData));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> removeAsync(@NotNull String table, @NotNull String key) {
        this.remove(table, key, null);
        return Task.completedTask(null);
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsentAsync(@NotNull String table, @NotNull String identifier) {
        this.remove(table, null, identifier);
        return Task.completedTask(null);
    }

    @NotNull
    @Override
    public Task<Void> removeAsync(@NotNull String table, @Nullable String key, @Nullable String identifier) {
        Conditions.isTrue(key != null || identifier != null, "Unable to delete document without key or identifier given");

        return Task.supply(() -> {
            this.sendPacket(new PacketAPIDatabaseDeleteDocument(table, key, identifier));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Boolean> createDatabaseAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIDatabaseCreate(name));
            return true;
        });
    }

    @NotNull
    @Override
    public Task<Boolean> deleteDatabaseAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIDatabaseDelete(name));
            return true;
        });
    }

    @NotNull
    @Override
    public Task<Boolean> containsAsync(@NotNull String table, @NotNull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryDatabaseContains(table, key),
                packet -> {
                    if (packet instanceof PacketAPIQueryDatabaseContainsResult) {
                        task.complete(((PacketAPIQueryDatabaseContainsResult) packet).isResult());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<Integer> sizeAsync(@NotNull String table) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> sendPacketQuery(
                new PacketAPIQueryGetDatabaseSize(table),
                packet -> {
                    if (packet instanceof PacketAPIQueryGetDatabaseSizeResult) {
                        task.complete(((PacketAPIQueryGetDatabaseSizeResult) packet).getSize());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @Override
    public JsonConfiguration find(@NotNull String table, @NotNull String key, String identifier) {
        return this.findAsync(table, key, identifier).getUninterruptedly();
    }

    @Override
    public <T> T find(@NotNull String table, @NotNull String key, String identifier, @NotNull Function<JsonConfiguration, T> function) {
        return this.findAsync(table, key, identifier, function).getUninterruptedly();
    }

    @Override
    public void insert(@NotNull String table, @NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        this.insertAsync(table, key, identifier, data).awaitUninterruptedly();
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
    public void update(@NotNull String table, @Nullable String key, @Nullable String identifier, @NotNull JsonConfiguration newData) {
        this.updateAsync(table, key, identifier, newData).awaitUninterruptedly();
    }

    @Override
    public void remove(@NotNull String table, @NotNull String key) {
        this.removeAsync(table, key).awaitUninterruptedly();
    }

    @Override
    public void removeIfAbsent(@NotNull String table, @NotNull String identifier) {
        this.removeIfAbsentAsync(table, identifier).awaitUninterruptedly();
    }

    @Override
    public void remove(@NotNull String table, @Nullable String key, @Nullable String identifier) {
        this.removeAsync(table, key, identifier).awaitUninterruptedly();
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
        return this.createMainGroupAsync(name, new ArrayList<>());
    }

    @NotNull
    @Override
    public Task<MainGroup> createMainGroupAsync(@NotNull String name, @NotNull List<String> subgroups) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIGroupCreateMainGroup(name, subgroups));
            return new MainGroup(name, subgroups);
        });
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name) {
        return this.createProcessGroupAsync(name, new ArrayList<>());
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates) {
        return this.createProcessGroupAsync(name, templates, new StartupConfiguration(
                -1, 1, 1, 41000, "java", true, new ArrayList<>()
        ));
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration) {
        return this.createProcessGroupAsync(name, templates, startupConfiguration, new PlayerAccessConfiguration(
                "reformcloud.join.full", false, "reformcloud.join.maintenance",
                false, null, true, true, 50
        ));
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration) {
        return this.createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, false);
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
            task.complete(this.createProcessGroupAsync(processGroup).getUninterruptedly());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIGroupCreateProcessGroup(processGroup));
            return processGroup;
        });
    }

    @NotNull
    @Override
    public Task<MainGroup> updateMainGroupAsync(@NotNull MainGroup mainGroup) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIGroupUpdateMainGroup(mainGroup));
            return mainGroup;
        });
    }

    @NotNull
    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIGroupUpdateProcessGroup(processGroup));
            return processGroup;
        });
    }

    @NotNull
    @Override
    public Task<MainGroup> getMainGroupAsync(@NotNull String name) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryRequestMainGroup(name),
                packet -> {
                    if (packet instanceof PacketAPIQueryRequestMainGroupResult) {
                        task.complete(((PacketAPIQueryRequestMainGroupResult) packet).getMainGroup());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> getProcessGroupAsync(@NotNull String name) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryRequestProcessGroup(name),
                packet -> {
                    if (packet instanceof PacketAPIQueryRequestProcessGroupResult) {
                        task.complete(((PacketAPIQueryRequestProcessGroupResult) packet).getProcessGroup());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> deleteMainGroupAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIGroupDeleteMainGroup(name));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> deleteProcessGroupAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIGroupDeleteProcessGroup(name));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        Task<List<MainGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryGetMainGroups(),
                packet -> {
                    if (packet instanceof PacketAPIQueryGetMainGroupsResult) {
                        task.complete(((PacketAPIQueryGetMainGroupsResult) packet).getMainGroups());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        Task<List<ProcessGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryGetProcessGroups(),
                packet -> {
                    if (packet instanceof PacketAPIQueryGetProcessGroupsResult) {
                        task.complete(((PacketAPIQueryGetProcessGroupsResult) packet).getProcessGroups());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
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
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionSendMessage(player, message));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> kickPlayerAsync(@NotNull UUID player, @NotNull String message) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionKickPlayer(player, message));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> kickPlayerFromServerAsync(@NotNull UUID player, @NotNull String message) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionKickPlayerFromServer(player, message));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> playSoundAsync(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionPlaySound(player, sound, f1, f2));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> sendTitleAsync(@NotNull UUID player, @NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionSendTitle(player, title, subTitle, fadeIn, stay, fadeOut));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> playEffectAsync(@NotNull UUID player, @NotNull String entityEffect) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionPlayEntityEffect(player, entityEffect));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> teleportAsync(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionTeleportPlayer(player, world, x, y, z, yaw, pitch));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull String server) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionConnectPlayerToServer(player, server));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull ProcessInformation server) {
        return connectAsync(player, server.getProcessDetail().getName());
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull UUID target) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIActionConnectPlayerToPlayer(player, target));
            return null;
        });
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

    @NotNull
    @Override
    public Task<ProcessInformation> startProcessAsync(@NotNull ProcessConfiguration processConfiguration) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryProcessStartNew(processConfiguration, true),
                packet -> {
                    if (packet instanceof PacketAPIQueryProcessStartNewResult) {
                        task.complete(((PacketAPIQueryProcessStartNewResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@NotNull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryProcessStopByName(name),
                packet -> {
                    if (packet instanceof PacketAPIQueryProcessStopResult) {
                        task.complete(((PacketAPIQueryProcessStopResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@NotNull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryProcessStopByUniqueID(uniqueID),
                packet -> {
                    if (packet instanceof PacketAPIQueryProcessStopResult) {
                        task.complete(((PacketAPIQueryProcessStopResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> getProcessAsync(@NotNull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryRequestProcessByName(name),
                packet -> {
                    if (packet instanceof PacketAPIQueryRequestProcessResult) {
                        task.complete(((PacketAPIQueryRequestProcessResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> getProcessAsync(@NotNull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryRequestProcessByUniqueID(uniqueID),
                packet -> {
                    if (packet instanceof PacketAPIQueryRequestProcessResult) {
                        task.complete(((PacketAPIQueryRequestProcessResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryGetProcesses(),
                packet -> {
                    if (packet instanceof PacketAPIQueryGetProcessesResult) {
                        task.complete(((PacketAPIQueryGetProcessesResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessCopyByName(name, null, null, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessCopyByUniqueID(processUniqueId, null, null, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessCopyByName(name, targetTemplate, null, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessCopyByUniqueID(processUniqueId, targetTemplate, null, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessCopyByName(name, targetTemplate, targetTemplateStorage, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessCopyByUniqueID(processUniqueId, targetTemplate, targetTemplateStorage, null));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessCopyByName(name, targetTemplate, targetTemplateStorage, targetTemplateGroup));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessCopyByUniqueID(processUniqueId, targetTemplate, targetTemplateStorage, targetTemplateGroup));
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
        Task.EXECUTOR.execute(() -> this.sendPacketQuery(
                new PacketAPIQueryGetProcessesPerGroup(group),
                packet -> {
                    if (packet instanceof PacketAPIQueryGetProcessesResult) {
                        task.complete(((PacketAPIQueryGetProcessesResult) packet).getProcessInformation());
                    } else {
                        task.completeExceptionally(WrongResultTypeException.INSTANCE);
                    }
                }
        ));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> executeProcessCommandAsync(@NotNull String name, @NotNull String commandLine) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessExecuteCommand(name, commandLine));
            return null;
        });
    }

    @Override
    public Task<Void> updateAsync(@NotNull ProcessInformation processInformation) {
        return Task.supply(() -> {
            this.sendPacket(new PacketAPIProcessUpdateProcessInformation(processInformation));
            return null;
        });
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
        return Task.supply(() -> {
            this.sendPacket(new NamedMessagePacket(Arrays.asList(receivers), jsonConfiguration, errorReportHandling, baseChannel, subChannel));
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> sendChannelMessageAsync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel,
                                              @NotNull String subChannel, @NotNull ReceiverType... receiverTypes) {
        return Task.supply(() -> {
            this.sendPacket(new TypeMessagePacket(Arrays.asList(receiverTypes), configuration, baseChannel, subChannel));
            return null;
        });
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
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetHandler().getQueryHandler().sendQueryAsync(packetSender, packet).onFailure(e -> result.accept(null)).onComplete(result));
    }
}
