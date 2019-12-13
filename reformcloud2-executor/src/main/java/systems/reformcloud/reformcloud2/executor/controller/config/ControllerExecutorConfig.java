package systems.reformcloud.reformcloud2.executor.controller.config;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultMainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.Setup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetupQuestion;
import systems.reformcloud.reformcloud2.executor.api.common.registry.Registry;
import systems.reformcloud.reformcloud2.executor.api.common.registry.basic.RegistryBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links.newCollection;
import static systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper.createDirectory;

public final class ControllerExecutorConfig {

    private static final Collection<Path> PATHS = newCollection(
            s -> Paths.get(s),
            "reformcloud/groups/main",
            "reformcloud/groups/sub",
            "reformcloud/configs",
            "reformcloud/applications"
    );

    private final Setup setup = new DefaultSetup();

    private final ControllerConfig controllerConfig;

    private final List<MainGroup> mainGroups = new ArrayList<>();

    private final List<ProcessGroup> processGroups = new ArrayList<>();

    private final Registry subGroupRegistry;

    private final Registry mainGroupRegistry;

    private final String connectionKey;

    private final IngameMessages ingameMessages;

    private final AtomicBoolean firstStartup = new AtomicBoolean(false);

    public ControllerExecutorConfig() {
        createDirectories();
        this.subGroupRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/sub"));
        this.mainGroupRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/main"));

        if (!Files.exists(ControllerConfig.PATH)) {
            this.firstStartup.set(true);
            firstSetup();
        }

        loadGroups();
        this.controllerConfig = load();
        this.connectionKey = connectionKey();
        this.ingameMessages = JsonConfiguration.read("reformcloud/configs/messages.json").get("messages", IngameMessages.TYPE);
    }

    private ControllerConfig load() {
        return JsonConfiguration.read(ControllerConfig.PATH).get("config", ControllerConfig.TYPE);
    }

    private String connectionKey() {
        return JsonConfiguration.read("reformcloud/.bin/connection.json").getString("key");
    }

    private void loadGroups() {
        processGroups.addAll(this.subGroupRegistry.readKeys(e -> e.get("key", ProcessGroup.TYPE)));
        mainGroups.addAll(this.mainGroupRegistry.readKeys(e -> e.get("key", MainGroup.TYPE)));
    }

    private void createDirectories() {
        PATHS.forEach(path -> {
            if (!Files.exists(path)) {
                createDirectory(path);
            }
        });
    }

    private void firstSetup() {
        new JsonConfiguration().add("key", StringUtil.generateString(50)).write(Paths.get(
                "reformcloud/.bin/connection.json"
        ));
        new JsonConfiguration().add("messages", new IngameMessages()).write(Paths.get("reformcloud/configs/messages.json"));

        setup.addQuestion(new DefaultSetupQuestion("Please write the ip address of the controller",
                "Please write your real ip",
                s -> s.trim().split("\\.").length == 4,
                s -> new JsonConfiguration().add("config", new ControllerConfig(
                        -1,
                        Collections.singletonList(Collections.singletonMap(s.trim(), 2008)),
                        Collections.singletonList(Collections.singletonMap(s.trim(), 1809))
                )).write(ControllerConfig.PATH)
        )).addQuestion(new DefaultSetupQuestion(
                "Please choose an installation type [(Java Proxy and Java Lobby) \"1\", (Pocket Proxy and Pocket Lobby) \"2\", (Nothing) \"3\"]",
                "This installation type is not valid",
                s -> {
                    try {
                        int i = Integer.parseInt(s);
                        return i >= 1 && i <= 3;
                    } catch (final Throwable throwable) {
                        return false;
                    }
                },
                s -> {
                    MainGroup mainProxy = new DefaultMainGroup("Proxy", Collections.singletonList("Proxy"));
                    MainGroup mainLobby = new DefaultMainGroup("Lobby", Collections.singletonList("Lobby"));

                    ProcessGroup proxy = null;
                    ProcessGroup lobby = null;

                    switch (Integer.parseInt(s)) {
                        case 1: {
                             proxy = new DefaultProcessGroup(
                                    "Proxy", 25565, Version.BUNGEECORD,
                                    128, true, 512
                            );
                            lobby = new DefaultProcessGroup(
                                    "Lobby", 41000, Version.SPIGOT_1_15,
                                    512, false, 50
                            );
                            break;
                        }

                        case 2: {
                            proxy = new DefaultProcessGroup(
                                    "Proxy", 19132, Version.WATERDOG,
                                    128, true, 512
                            );
                            lobby = new DefaultProcessGroup(
                                    "Lobby", 41000, Version.NUKKIT_X,
                                    512, false, 50
                            );
                            break;
                        }

                        case 3: {
                            return;
                        }
                    }

                    if (proxy == null) {
                        throw new IllegalStateException("Lobby or Proxy group not initialized correctly");
                    }

                    this.mainGroupRegistry.createKey(mainProxy.getName(), mainProxy);
                    this.mainGroupRegistry.createKey(mainLobby.getName(), mainLobby);

                    this.subGroupRegistry.createKey(proxy.getName(), proxy);
                    this.subGroupRegistry.createKey(lobby.getName(), lobby);
                }
        )).startSetup(ControllerExecutor.getInstance().getLoggerBase());
    }

    @Nonnull
    public MainGroup createMainGroup(MainGroup mainGroup) {
        MainGroup mainGroup1 = mainGroups.stream().filter(group -> mainGroup.getName().equals(group.getName())).findFirst().orElse(null);
        if (mainGroup1 == null) {
            this.mainGroups.add(mainGroup);
            return this.mainGroupRegistry.createKey(mainGroup.getName(), mainGroup);
        }

        return mainGroup;
    }

    @Nonnull
    public ProcessGroup createProcessGroup(ProcessGroup processGroup) {
        ProcessGroup processGroup1 = processGroups.stream().filter(group -> processGroup.getName().equals(group.getName())).findFirst().orElse(null);
        if (processGroup1 == null) {
            this.processGroups.add(processGroup);
            ControllerExecutor.getInstance().getAutoStartupHandler().update();
            return this.subGroupRegistry.createKey(processGroup.getName(), processGroup);
        }

        return processGroup;
    }

    public void deleteMainGroup(MainGroup mainGroup) {
        mainGroups.remove(mainGroup);
        this.mainGroupRegistry.deleteKey(mainGroup.getName());
    }

    public void deleteProcessGroup(ProcessGroup processGroup) {
        this.subGroupRegistry.deleteKey(processGroup.getName());
        processGroups.remove(processGroup);
        ControllerExecutor.getInstance().getAutoStartupHandler().update();

        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(processInformation -> {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(processInformation.getProcessUniqueID());
            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 10);
        });
    }

    public void updateProcessGroup(ProcessGroup processGroup) {
        Links.filterToReference(processGroups, group -> processGroup.getName().equals(group.getName())).ifPresent(group -> {
            processGroups.remove(group);
            processGroups.add(processGroup);
            this.subGroupRegistry.updateKey(processGroup.getName(), processGroup);
            ControllerExecutor.getInstance().getAutoStartupHandler().update();
        });

        Links.allOf(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses(), processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName())).forEach(processInformation -> {
            processInformation.setProcessGroup(processGroup);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);
        });
    }

    public void updateMainGroup(MainGroup mainGroup) {
        Links.filterToReference(mainGroups, group -> group.getName().equals(mainGroup.getName())).ifPresent(group -> {
            mainGroups.remove(group);
            mainGroups.add(mainGroup);
            this.mainGroupRegistry.updateKey(mainGroup.getName(), mainGroup);
        });
    }

    public ControllerConfig getControllerConfig() {
        return controllerConfig;
    }

    public List<MainGroup> getMainGroups() {
        return mainGroups;
    }

    public List<ProcessGroup> getProcessGroups() {
        return processGroups;
    }

    public String getConnectionKey() {
        return connectionKey;
    }

    public IngameMessages getIngameMessages() {
        return ingameMessages;
    }

    public final boolean isFirstStartup() {
        return firstStartup.get();
    }
}
