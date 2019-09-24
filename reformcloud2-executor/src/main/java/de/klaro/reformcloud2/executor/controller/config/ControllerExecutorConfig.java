package de.klaro.reformcloud2.executor.controller.config;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.MainGroup;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.basic.DefaultMainGroup;
import de.klaro.reformcloud2.executor.api.common.groups.basic.DefaultProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.logger.setup.Setup;
import de.klaro.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetup;
import de.klaro.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetupQuestion;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.controller.ControllerExecutor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.klaro.reformcloud2.executor.api.common.utility.list.Links.newCollection;
import static de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper.createDirectory;

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

    private final String connectionKey;

    private final IngameMessages ingameMessages;

    private final AtomicBoolean firstStartup = new AtomicBoolean(false);

    public ControllerExecutorConfig() {
        createDirectories();
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
        for (File file : Objects.requireNonNull(new File("reformcloud/groups/main").listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".json")))) {
            this.mainGroups.add(JsonConfiguration.read(file).get("group", MainGroup.TYPE));
        }

        for (File file : Objects.requireNonNull(new File("reformcloud/groups/sub").listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".json")))) {
            this.processGroups.add(JsonConfiguration.read(file).get("group", ProcessGroup.TYPE));
        }
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
                s -> s.split("\\.").length == 4,
                s -> new JsonConfiguration().add("config", new ControllerConfig(
                        -1,
                        Collections.singletonList(Collections.singletonMap(s, 2008)),
                        Collections.singletonList(Collections.singletonMap(s, 1809))
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
                                    "Proxy", mainProxy.getName(), 25565, Version.BUNGEECORD,
                                    128, true, 512
                            );
                            lobby = new DefaultProcessGroup(
                                    "Lobby", mainLobby.getName(), 41000, Version.PAPER_1_8_8,
                                    512, false, 50
                            );
                            break;
                        }

                        case 2: {
                            proxy = new DefaultProcessGroup(
                                    "Proxy", mainProxy.getName(), 19132, Version.WATERDOG,
                                    128, true, 512
                            );
                            lobby = new DefaultProcessGroup(
                                    "Lobby", mainLobby.getName(), 41000, Version.NUKKIT_X,
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

                    new JsonConfiguration()
                            .add("group", mainProxy).write("reformcloud/groups/main/" + mainProxy.getName() + ".json");
                    new JsonConfiguration()
                            .add("group", mainLobby).write("reformcloud/groups/main/" + mainLobby.getName() + ".json");

                    new JsonConfiguration()
                            .add("group", proxy).write("reformcloud/groups/sub/" + proxy.getName() + ".json");
                    new JsonConfiguration()
                            .add("group", lobby).write("reformcloud/groups/sub/" + lobby.getName() + ".json");
                }
        )).startSetup(ControllerExecutor.getInstance().getLoggerBase());
    }

    public MainGroup createMainGroup(MainGroup mainGroup) {
        MainGroup mainGroup1 = mainGroups.stream().filter(group -> mainGroup.getName().equals(group.getName())).findFirst().orElse(null);
        if (mainGroup1 == null) {
            new JsonConfiguration()
                    .add("group", mainGroup).write("reformcloud/groups/main/" + mainGroup.getName() + ".json");
            mainGroups.add(mainGroup);
            return mainGroup;
        }

        return null;
    }

    public ProcessGroup createProcessGroup(ProcessGroup processGroup) {
        ProcessGroup processGroup1 = processGroups.stream().filter(group -> processGroup.getName().equals(group.getName())).findFirst().orElse(null);
        if (processGroup1 == null) {
            new JsonConfiguration()
                    .add("group", processGroup).write("reformcloud/groups/sub/" + processGroup.getName() + ".json");
            processGroups.add(processGroup);
            ControllerExecutor.getInstance().getAutoStartupHandler().update();
            return processGroup;
        }

        return null;
    }

    public void deleteMainGroup(MainGroup mainGroup) {
        mainGroups.remove(mainGroup);
        SystemHelper.deleteFile(new File("reformcloud/groups/main/" + mainGroup.getName() + ".json"));
    }

    public void deleteProcessGroup(ProcessGroup processGroup) {
        processGroups.remove(processGroup);
        SystemHelper.deleteFile(new File("reformcloud/groups/sub/" + processGroup.getName() + ".json"));
        ControllerExecutor.getInstance().getAutoStartupHandler().update();

        ExecutorAPI.getInstance().getProcesses(processGroup.getName()).forEach(processInformation -> {
            ExecutorAPI.getInstance().stopProcess(processInformation.getProcessUniqueID());
            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 10);
        });
    }

    public void updateProcessGroup(ProcessGroup processGroup) {
        Links.filterToReference(processGroups, group -> processGroup.getName().equals(group.getName())).ifPresent(group -> {
            processGroups.remove(group);
            processGroups.add(processGroup);
            new JsonConfiguration()
                    .add("group", processGroup).write("reformcloud/groups/sub/" + processGroup.getName() + ".json");
            ControllerExecutor.getInstance().getAutoStartupHandler().update();
        });

        Links.allOf(ExecutorAPI.getInstance().getAllProcesses(), processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName())).forEach(processInformation -> {
            processInformation.setProcessGroup(processGroup);
            ExecutorAPI.getInstance().update(processInformation);
        });
    }

    public void updateMainGroup(MainGroup mainGroup) {
        Links.filterToReference(mainGroups, group -> group.getName().equals(mainGroup.getName())).ifPresent(group -> {
            mainGroups.remove(group);
            mainGroups.add(mainGroup);
            new JsonConfiguration()
                    .add("group", mainGroup).write("reformcloud/groups/main/" + mainGroup.getName() + ".json");
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
