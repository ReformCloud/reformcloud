package de.klaro.reformcloud2.executor.api.common.api.basic;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.packets.out.*;
import de.klaro.reformcloud2.executor.api.common.application.InstallableApplication;
import de.klaro.reformcloud2.executor.api.common.application.LoadedApplication;
import de.klaro.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import de.klaro.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.commands.Command;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.MainGroup;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.utils.*;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;
import de.klaro.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import de.klaro.reformcloud2.executor.api.common.utility.task.excpetion.TaskCompletionException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ExternalAPIImplementation extends ExecutorAPI {

    public static final int EXTERNAL_PACKET_ID = 600;

    @Override
    public Task<Boolean> loadApplicationAsync(InstallableApplication application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutLoadApplication(application), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getBoolean("installed"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Boolean> unloadApplicationAsync(LoadedApplication application) {
        return unloadApplicationAsync(application.applicationConfig().getName());
    }

    @Override
    public Task<Boolean> unloadApplicationAsync(String application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutUnloadApplication(application), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getBoolean("uninstalled"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<LoadedApplication> getApplicationAsync(String name) {
        Task<LoadedApplication> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetLoadedApplication(name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", LoadedApplication.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<List<LoadedApplication>> getApplicationsAsync() {
        Task<List<LoadedApplication>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetApplications(), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(new ArrayList<>(packet.content().get("result", new TypeToken<List<DefaultLoadedApplication>>() {})));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public boolean loadApplication(InstallableApplication application) {
        return loadApplicationAsync(application).getUninterruptedly();
    }

    @Override
    public boolean unloadApplication(LoadedApplication application) {
        return unloadApplicationAsync(application).getUninterruptedly();
    }

    @Override
    public boolean unloadApplication(String application) {
        return unloadApplicationAsync(application).getUninterruptedly();
    }

    @Override
    public LoadedApplication getApplication(String name) {
        return getApplicationAsync(name).getUninterruptedly();
    }

    @Override
    public List<LoadedApplication> getApplications() {
        return getApplicationsAsync().getUninterruptedly();
    }

    @Override
    public Task<Boolean> isClientConnectedAsync(String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                task.complete(getClientInformationAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5) != null);
            }
        });
        return task;
    }

    @Override
    public Task<String> getClientStartHostAsync(String name) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                task.complete(getClientInformationAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5).startHost());
            }
        });
        return task;
    }

    @Override
    public Task<Integer> getMaxMemoryAsync(String name) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                task.complete(getClientInformationAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5).maxMemory());
            }
        });
        return task;
    }

    @Override
    public Task<Integer> getMaxProcessesAsync(String name) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                task.complete(getClientInformationAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5).maxProcessCount());
            }
        });
        return task;
    }

    @Override
    public Task<ClientRuntimeInformation> getClientInformationAsync(String name) {
        Task<ClientRuntimeInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetClientRuntimeInformation(name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", ClientRuntimeInformation.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public boolean isClientConnected(String name) {
        return isClientConnectedAsync(name).getUninterruptedly();
    }

    @Override
    public String getClientStartHost(String name) {
        return getClientStartHostAsync(name).getUninterruptedly();
    }

    @Override
    public int getMaxMemory(String name) {
        return getMaxMemoryAsync(name).getUninterruptedly();
    }

    @Override
    public int getMaxProcesses(String name) {
        return getMaxProcessesAsync(name).getUninterruptedly();
    }

    @Override
    public ClientRuntimeInformation getClientInformation(String name) {
        return getClientInformationAsync(name).getUninterruptedly();
    }

    @Override
    public Task<Void> sendColouredLineAsync(String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutSendColouredLine(line));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> sendRawLineAsync(String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutSendRawLine(line));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<String> dispatchCommandAndGetResultAsync(String commandLine) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutDispatchControllerCommand(commandLine), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getString("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Command> getControllerCommandAsync(String name) {
        Task<Command> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetCommand(name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", Command.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Boolean> isControllerCommandRegisteredAsync(String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                task.complete(getControllerCommandAsync(name).getUninterruptedly(TimeUnit.SECONDS, 5) != null);
            }
        });
        return task;
    }

    @Override
    public void sendColouredLine(String line) {
        sendColouredLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public void sendRawLine(String line) {
        sendRawLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public String dispatchCommandAndGetResult(String commandLine) {
        return dispatchCommandAndGetResultAsync(commandLine).getUninterruptedly();
    }

    @Override
    public Command getControllerCommand(String name) {
        return getControllerCommandAsync(name).getUninterruptedly();
    }

    @Override
    public boolean isControllerCommandRegistered(String name) {
        return isControllerCommandRegisteredAsync(name).getUninterruptedly();
    }

    @Override
    public Task<JsonConfiguration> findAsync(String table, String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutDatabaseFindDocument(table, key, identifier), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public <T> Task<T> findAsync(String table, String key, String identifier, Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                task.complete(function.apply(findAsync(table, key, identifier).getUninterruptedly()));
            }
        });
        return task;
    }

    @Override
    public Task<Void> insertAsync(String table, String key, String identifier, JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutDatabaseInsertDocument(table, key, identifier, data));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Boolean> updateAsync(String table, String key, JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutDatabaseUpdateDocument(table, key, newData,true), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getBoolean("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Boolean> updateIfAbsentAsync(String table, String identifier, JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutDatabaseUpdateDocument(table, identifier, newData,false), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getBoolean("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Void> removeAsync(String table, String key) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutDatabaseRemoveDocument(table, key,true));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> removeIfAbsentAsync(String table, String identifier) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutDatabaseRemoveDocument(table, identifier,false));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Boolean> createDatabaseAsync(String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.CREATE, name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getBoolean("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Boolean> deleteDatabaseAsync(String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.DELETE, name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getBoolean("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Boolean> containsAsync(String table, String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutDatabaseContainsDocument(table, key), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getBoolean("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Integer> sizeAsync(String table) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutDatabaseAction(ExternalAPIPacketOutDatabaseAction.DatabaseAction.SIZE, table), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getInteger("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public JsonConfiguration find(String table, String key, String identifier) {
        return findAsync(table, key, identifier).getUninterruptedly();
    }

    @Override
    public <T> T find(String table, String key, String identifier, Function<JsonConfiguration, T> function) {
        return findAsync(table, key, identifier, function).getUninterruptedly();
    }

    @Override
    public void insert(String table, String key, String identifier, JsonConfiguration data) {
        insertAsync(table, key, identifier, data).awaitUninterruptedly();
    }

    @Override
    public boolean update(String table, String key, JsonConfiguration newData) {
        return updateAsync(table, key, newData).getUninterruptedly();
    }

    @Override
    public boolean updateIfAbsent(String table, String identifier, JsonConfiguration newData) {
        return updateIfAbsentAsync(table, identifier, newData).getUninterruptedly();
    }

    @Override
    public void remove(String table, String key) {
        removeAsync(table, key).awaitUninterruptedly();
    }

    @Override
    public void removeIfAbsent(String table, String identifier) {
        removeIfAbsentAsync(table, identifier).awaitUninterruptedly();
    }

    @Override
    public boolean createDatabase(String name) {
        return createDatabaseAsync(name).getUninterruptedly();
    }

    @Override
    public boolean deleteDatabase(String name) {
        return deleteDatabaseAsync(name).getUninterruptedly();
    }

    @Override
    public boolean contains(String table, String key) {
        return containsAsync(table, key).getUninterruptedly();
    }

    @Override
    public int size(String table) {
        return sizeAsync(table).getUninterruptedly();
    }

    @Override
    public Task<MainGroup> createMainGroupAsync(String name) {
        return createMainGroupAsync(name, new ArrayList<>());
    }

    @Override
    public Task<MainGroup> createMainGroupAsync(String name, List<String> subgroups) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutCreateMainGroup(new MainGroup(name, subgroups)), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", MainGroup.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name) {
        return createProcessGroupAsync(name, null);
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent) {
        return createProcessGroupAsync(name, parent, Collections.singletonList(
                new Template(0, "default", "#", null, new RuntimeConfiguration(
                        512, new ArrayList<>(), new HashMap<>()
                ), Version.PAPER_1_8_8)
        ));
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates) {
        return createProcessGroupAsync(name, parent, templates, new StartupConfiguration(
                -1, 1, 1, 41000, StartupEnvironment.JAVA_RUNTIME, true, new ArrayList<>()
        ));
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration) {
        return createProcessGroupAsync(name, parent, templates, startupConfiguration, new PlayerAccessConfiguration(
                false, "reformcloud.join.maintenance", false,
                null, true, true, true, 50
        ));
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration) {
        return createProcessGroupAsync(name, parent, templates, startupConfiguration, playerAccessConfiguration, false);
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                ProcessGroup processGroup = new ProcessGroup(
                        name,
                        true,
                        parent,
                        startupConfiguration,
                        templates,
                        playerAccessConfiguration,
                        staticGroup
                );
                task.complete(createProcessGroupAsync(processGroup).getUninterruptedly());
            }
        });
        return task;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutCreateProcessGroup(processGroup), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", ProcessGroup.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<MainGroup> updateMainGroupAsync(MainGroup mainGroup) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutUpdateMainGroup(mainGroup));
                task.complete(mainGroup);
            }
        });
        return task;
    }

    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutUpdateProcessGroup(processGroup));
                task.complete(processGroup);
            }
        });
        return task;
    }

    @Override
    public Task<MainGroup> getMainGroupAsync(String name) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetMainGroup(name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", MainGroup.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<ProcessGroup> getProcessGroupAsync(String name) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetProcessGroup(name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", ProcessGroup.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Void> deleteMainGroupAsync(String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutDeleteMainGroup(name));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> deleteProcessGroupAsync(String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutDeleteProcessGroup(name));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        Task<List<MainGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetMainGroups(), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", new TypeToken<List<MainGroup>>() {}));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        Task<List<ProcessGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetProcessGroups(), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", new TypeToken<List<ProcessGroup>>() {}));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public MainGroup createMainGroup(String name) {
        return createMainGroupAsync(name).getUninterruptedly();
    }

    @Override
    public MainGroup createMainGroup(String name, List<String> subgroups) {
        return createMainGroupAsync(name, subgroups).getUninterruptedly();
    }

    @Override
    public ProcessGroup createProcessGroup(String name) {
        return createProcessGroupAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent) {
        return createProcessGroupAsync(name, parent).getUninterruptedly();
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent, List<Template> templates) {
        return createProcessGroupAsync(name, parent, templates).getUninterruptedly();
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration) {
        return createProcessGroupAsync(name, parent, templates, startupConfiguration).getUninterruptedly();
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration) {
        return createProcessGroupAsync(name, parent, templates, startupConfiguration, playerAccessConfiguration).getUninterruptedly();
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        return createProcessGroupAsync(name, parent, templates, startupConfiguration, playerAccessConfiguration, staticGroup).getUninterruptedly();
    }

    @Override
    public ProcessGroup createProcessGroup(ProcessGroup processGroup) {
        return createProcessGroupAsync(processGroup).getUninterruptedly();
    }

    @Override
    public MainGroup updateMainGroup(MainGroup mainGroup) {
        return updateMainGroupAsync(mainGroup).getUninterruptedly();
    }

    @Override
    public ProcessGroup updateProcessGroup(ProcessGroup processGroup) {
        return updateProcessGroupAsync(processGroup).getUninterruptedly();
    }

    @Override
    public MainGroup getMainGroup(String name) {
        return getMainGroupAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessGroup getProcessGroup(String name) {
        return getProcessGroupAsync(name).getUninterruptedly();
    }

    @Override
    public void deleteMainGroup(String name) {
        deleteMainGroupAsync(name).awaitUninterruptedly();
    }

    @Override
    public void deleteProcessGroup(String name) {
        deleteProcessGroupAsync(name).awaitUninterruptedly();
    }

    @Override
    public List<MainGroup> getMainGroups() {
        return getMainGroupsAsync().getUninterruptedly();
    }

    @Override
    public List<ProcessGroup> getProcessGroups() {
        return getProcessGroupsAsync().getUninterruptedly();
    }

    @Override
    public Task<Void> sendMessageAsync(UUID player, String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SEND_MESSAGE, player, message));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> kickPlayerAsync(UUID player, String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.KICK_PLAYER, player, message));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> kickPlayerFromServerAsync(UUID player, String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.KICK_SERVER, player, message));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> playSoundAsync(UUID player, String sound, float f1, float f2) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_SOUND, player, sound, f1, f2));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> sendTitleAsync(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SEND_TITLE, player, title, subTitle, fadeIn, stay, fadeOut));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> playEffectAsync(UUID player, String entityEffect) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_ENTITY_EFFECT, player, entityEffect));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public <T> Task<Void> playEffectAsync(UUID player, String effect, T data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.PLAY_EFFECT, player, effect, data));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> respawnAsync(UUID player) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.RESPAWN, player));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> teleportAsync(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.LOCATION_TELEPORT, player, world, x, y, z, yaw, pitch));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> connectAsync(UUID player, String server) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.CONNECT, player, server));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> connectAsync(UUID player, ProcessInformation server) {
        return connectAsync(player, server.getName());
    }

    @Override
    public Task<Void> connectAsync(UUID player, UUID target) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.CONNECT_PLAYER, player, target));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> setResourcePackAsync(UUID player, String pack) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutAPIAction(ExternalAPIPacketOutAPIAction.APIAction.SET_RESOURCE_PACK, player, pack));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public void sendMessage(UUID player, String message) {
        sendMessageAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayer(UUID player, String message) {
        kickPlayerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayerFromServer(UUID player, String message) {
        kickPlayerFromServerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void playSound(UUID player, String sound, float f1, float f2) {
        playSoundAsync(player, sound, f1, f2).awaitUninterruptedly();
    }

    @Override
    public void sendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitleAsync(player, title, subTitle, fadeIn, stay, fadeOut).awaitUninterruptedly();
    }

    @Override
    public void playEffect(UUID player, String entityEffect) {
        playEffectAsync(player, entityEffect).awaitUninterruptedly();
    }

    @Override
    public <T> void playEffect(UUID player, String effect, T data) {
        playEffectAsync(player, effect, data).awaitUninterruptedly();
    }

    @Override
    public void respawn(UUID player) {
        respawnAsync(player).awaitUninterruptedly();
    }

    @Override
    public void teleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        teleportAsync(player, world, x, y, z, yaw, pitch).awaitUninterruptedly();
    }

    @Override
    public void connect(UUID player, String server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(UUID player, ProcessInformation server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(UUID player, UUID target) {
        connectAsync(player, target).awaitUninterruptedly();
    }

    @Override
    public void setResourcePack(UUID player, String pack) {
        setResourcePackAsync(player, pack).awaitUninterruptedly();
    }

    @Override
    public Task<Void> installPluginAsync(String process, InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutInstallPlugin(plugin, process));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> installPluginAsync(ProcessInformation process, InstallablePlugin plugin) {
        return installPluginAsync(process.getName(), plugin);
    }

    @Override
    public Task<Void> unloadPluginAsync(String process, Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutUnloadPlugin(plugin, process));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Void> unloadPluginAsync(ProcessInformation process, Plugin plugin) {
        return unloadPluginAsync(process.getName(), plugin);
    }

    @Override
    public Task<Plugin> getInstalledPluginAsync(String process, String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetInstalledPlugin(name, process), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", Plugin.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Plugin> getInstalledPluginAsync(ProcessInformation process, String name) {
        return getInstalledPluginAsync(process.getName(), name);
    }

    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(String process, String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                task.complete(Links.allOf(getPlugins(process), new Predicate<DefaultPlugin>() {
                    @Override
                    public boolean test(DefaultPlugin plugin) {
                        return plugin.author().equals(author);
                    }
                }));
            }
        });
        return task;
    }

    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(ProcessInformation process, String author) {
        return getPluginsAsync(process.getName(), author);
    }

    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(String process) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetPlugins(process), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", new TypeToken<Collection<DefaultPlugin>>() {}));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(ProcessInformation processInformation) {
        return getPluginsAsync(processInformation.getName());
    }

    @Override
    public void installPlugin(String process, InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void installPlugin(ProcessInformation process, InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(String process, Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(ProcessInformation process, Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(String process, String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(ProcessInformation process, String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Override
    public Collection<DefaultPlugin> getPlugins(String process, String author) {
        return getPluginsAsync(process, author).getUninterruptedly();
    }

    @Override
    public Collection<DefaultPlugin> getPlugins(ProcessInformation process, String author) {
        return getPluginsAsync(process, author).getUninterruptedly();
    }

    @Override
    public Collection<DefaultPlugin> getPlugins(String process) {
        return getPluginsAsync(process).getUninterruptedly();
    }

    @Override
    public Collection<DefaultPlugin> getPlugins(ProcessInformation processInformation) {
        return getPluginsAsync(processInformation).getUninterruptedly();
    }

    @Override
    public Task<ProcessInformation> startProcessAsync(String groupName) {
        return startProcessAsync(groupName, null);
    }

    @Override
    public Task<ProcessInformation> startProcessAsync(String groupName, String template) {
        return startProcessAsync(groupName, template, new JsonConfiguration());
    }

    @Override
    public Task<ProcessInformation> startProcessAsync(String groupName, String template, JsonConfiguration configurable) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutStartProcess(groupName, template, configurable), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", ProcessInformation.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<ProcessInformation> stopProcessAsync(String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutStopProcess(name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", ProcessInformation.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<ProcessInformation> stopProcessAsync(UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutStopProcess(uniqueID), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", ProcessInformation.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<ProcessInformation> getProcessAsync(String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetProcess(name), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", ProcessInformation.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<ProcessInformation> getProcessAsync(UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetProcess(uniqueID), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", ProcessInformation.TYPE));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetProcesses(), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", new TypeToken<List<ProcessInformation>>() {}));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetProcesses(group), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().get("result", new TypeToken<List<ProcessInformation>>() {}));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Void> executeProcessCommandAsync(String name, String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutExecuteProcessCommand(name, commandLine));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public Task<Integer> getGlobalOnlineCountAsync(Collection<String> ignoredProxies) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacketQuery(new ExternalAPIPacketOutGetOnlineCount(ignoredProxies), new Consumer<Packet>() {
                    @Override
                    public void accept(Packet packet) {
                        task.complete(packet.content().getInteger("result"));
                    }
                });
            }
        });
        return task;
    }

    @Override
    public Task<Void> updateAsync(ProcessInformation processInformation) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                sendPacket(new ExternalAPIPacketOutUpdateProcessInformation(processInformation));
                task.complete(null);
            }
        });
        return task;
    }

    @Override
    public ProcessInformation startProcess(String groupName) {
        return startProcessAsync(groupName).getUninterruptedly();
    }

    @Override
    public ProcessInformation startProcess(String groupName, String template) {
        return startProcessAsync(groupName, template).getUninterruptedly();
    }

    @Override
    public ProcessInformation startProcess(String groupName, String template, JsonConfiguration configurable) {
        return startProcessAsync(groupName, template, configurable).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(String name) {
        return stopProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(UUID uniqueID) {
        return stopProcessAsync(uniqueID).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(String name) {
        return getProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(UUID uniqueID) {
        return getProcessAsync(uniqueID).getUninterruptedly();
    }

    @Override
    public List<ProcessInformation> getAllProcesses() {
        return getAllProcessesAsync().getUninterruptedly();
    }

    @Override
    public List<ProcessInformation> getProcesses(String group) {
        return getProcessesAsync(group).getUninterruptedly();
    }

    @Override
    public void executeProcessCommand(String name, String commandLine) {
        executeProcessCommandAsync(name, commandLine).awaitUninterruptedly();
    }

    @Override
    public int getGlobalOnlineCount(Collection<String> ignoredProxies) {
        return getGlobalOnlineCountAsync(ignoredProxies).getUninterruptedly();
    }

    @Override
    public void update(ProcessInformation processInformation) {
        updateAsync(processInformation).getUninterruptedly();
    }

    @Override
    public Task<ProcessInformation> getThisProcessInformationAsync() {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                task.complete(getThisProcessInformation());
            }
        });
        return task;
    }

    /* ============== */

    protected abstract PacketHandler packetHandler();

    private void sendPacket(Packet packet) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
            @Override
            public void accept(PacketSender packetSender) {
                packetSender.sendPacket(packet);
            }
        });
    }

    private void sendPacketQuery(Packet packet, Consumer<Packet> result) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
            @Override
            public void accept(PacketSender packetSender) {
                packetHandler().getQueryHandler().sendQueryAsync(packetSender, packet).getTask().onFailure(new Consumer<TaskCompletionException>() {
                    @Override
                    public void accept(TaskCompletionException e) {
                        result.accept(null);
                    }
                }).onComplete(result);
            }
        });
    }
}
