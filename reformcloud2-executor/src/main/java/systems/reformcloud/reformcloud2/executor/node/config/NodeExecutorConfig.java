package systems.reformcloud.reformcloud2.executor.node.config;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
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
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.registry.Registry;
import systems.reformcloud.reformcloud2.executor.api.common.registry.basic.RegistryBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links.newCollection;

public class NodeExecutorConfig {

    private static final Collection<Path> PATHS = newCollection(
            s -> Paths.get(s),
            "reformcloud/groups/main",
            "reformcloud/groups/sub",
            "reformcloud/configs",
            "reformcloud/applications",
            "reformcloud/temp",
            "reformcloud/static",
            "reformcloud/templates",
            "reformcloud/global",
            "reformcloud/files/.connection"
    );

    private final Setup setup = new DefaultSetup();

    private NodeInformation self;

    private NodeConfig nodeConfig;

    private String connectionKey;

    private String currentNodeConnectionKey;

    private final List<MainGroup> mainGroups = new ArrayList<>();

    private final List<ProcessGroup> processGroups = new ArrayList<>();

    private final AtomicBoolean firstStartup = new AtomicBoolean(false);

    private final Registry localMainGroupsRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/main"));

    private final Registry localSubGroupsRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/sub"));

    private IngameMessages ingameMessages;

    public void init() {
        createDirectories();
        if (!Files.exists(NodeConfig.PATH)) {
            firstStartup.set(true);
            setup.addQuestion(new DefaultSetupQuestion(
                    "Please enter the start host of the node",
                    "Please enter your real address",
                    e -> e.trim().split("\\.").length == 4,
                    e -> new JsonConfiguration().add("config", new NodeConfig(
                            CommonHelper.calculateMaxMemory(),
                            e.trim(),
                            Collections.singletonList(Collections.singletonMap(e.trim(), 1809)),
                            Collections.singletonList(Collections.singletonMap(e.trim(), 2008)),
                            Collections.emptyList()
                    )).write(NodeConfig.PATH)
            )).addQuestion(new DefaultSetupQuestion(
                    "Please copy the connection key for other nodes into the console (if there is any other node) or type \"null\"",
                    "",
                    s -> true,
                    s -> {
                        if (s.equalsIgnoreCase("null")) {
                            return;
                        }

                        new JsonConfiguration().add("key", s).write("reformcloud/files/.connection/connection.json");
                    }
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

                        this.localMainGroupsRegistry.createKey(mainProxy.getName(), mainProxy);
                        this.localMainGroupsRegistry.createKey(mainLobby.getName(), mainLobby);

                        this.localSubGroupsRegistry.createKey(proxy.getName(), proxy);
                        this.localSubGroupsRegistry.createKey(lobby.getName(), lobby);
                    }
            )).startSetup(NodeExecutor.getInstance().getLoggerBase());

            new JsonConfiguration().add("messages", new IngameMessages()).write(Paths.get("reformcloud/configs/messages.json"));
        }

        this.nodeConfig = JsonConfiguration.read(NodeConfig.PATH).get("config", NodeConfig.TYPE);
        this.ingameMessages = JsonConfiguration.read("reformcloud/configs/messages.json").get("messages", IngameMessages.TYPE);
        this.self = this.nodeConfig.prepare();
        this.connectionKey = JsonConfiguration.read("reformcloud/files/.connection/connection.json").getOrDefault("key", (String) null);
        this.loadGroups();

        this.currentNodeConnectionKey = StringUtil.generateString(64);
    }

    private void loadGroups() {
        processGroups.addAll(this.localSubGroupsRegistry.readKeys(e -> e.get("key", ProcessGroup.TYPE)));
        mainGroups.addAll(this.localMainGroupsRegistry.readKeys(e -> e.get("key", MainGroup.TYPE)));
    }

    public void handleProcessGroupCreate(ProcessGroup processGroup) {
        this.localSubGroupsRegistry.createKey(processGroup.getName(), processGroup);
    }

    public void handleMainGroupCreate(MainGroup mainGroup) {
        this.localMainGroupsRegistry.createKey(mainGroup.getName(), mainGroup);
    }

    public void handleProcessGroupUpdate(ProcessGroup processGroup) {
        this.localSubGroupsRegistry.updateKey(processGroup.getName(), processGroup);
    }

    public void handleMainGroupUpdate(MainGroup mainGroup) {
        this.localMainGroupsRegistry.updateKey(mainGroup.getName(), mainGroup);
    }

    public void handleProcessGroupDelete(ProcessGroup processGroup) {
        this.localSubGroupsRegistry.deleteKey(processGroup.getName());
    }

    public void handleMainGroupDelete(MainGroup mainGroup) {
        this.localMainGroupsRegistry.deleteKey(mainGroup.getName());
    }

    private void createDirectories() {
        PATHS.forEach(SystemHelper::createDirectory);
    }

    public NodeInformation getSelf() {
        return self;
    }

    public NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public String getConnectionKey() {
        return connectionKey;
    }

    public String getCurrentNodeConnectionKey() {
        return currentNodeConnectionKey;
    }

    public boolean isFirstStartup() {
        return firstStartup.get();
    }

    public List<MainGroup> getMainGroups() {
        return mainGroups;
    }

    public IngameMessages getIngameMessages() {
        return ingameMessages;
    }

    public List<ProcessGroup> getProcessGroups() {
        return processGroups;
    }
}
