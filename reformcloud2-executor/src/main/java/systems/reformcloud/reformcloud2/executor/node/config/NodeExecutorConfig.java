package systems.reformcloud.reformcloud2.executor.node.config;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.groups.setup.GroupSetupHelper;
import systems.reformcloud.reformcloud2.executor.api.common.groups.setup.GroupSetupVersion;
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

import static systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams.newCollection;

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
            "reformcloud/global/plugins",
            "reformcloud/files/.connection"
    );

    private final Setup setup = new DefaultSetup();
    private final List<MainGroup> mainGroups = new ArrayList<>();
    private final List<ProcessGroup> processGroups = new ArrayList<>();
    private final AtomicBoolean firstStartup = new AtomicBoolean(false);
    private final Registry localMainGroupsRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/main"));
    private final Registry localSubGroupsRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/sub"));
    private NodeInformation self;
    private NodeConfig nodeConfig;
    private String connectionKey;
    private String currentNodeConnectionKey;
    private IngameMessages ingameMessages;

    public void init() {
        createDirectories();
        if (!Files.exists(NodeConfig.PATH)) {
            firstStartup.set(true);
            setup.addQuestion(new DefaultSetupQuestion(
                    "Please enter the start host or domain name of the node",
                    "Please enter your real address or domain name",
                    e -> CommonHelper.getIpAddress(e.trim()) != null,
                    e -> {
                        String ip = CommonHelper.getIpAddress(e.trim());

                        new JsonConfiguration().add("config", new NodeConfig(
                                CommonHelper.calculateMaxMemory(),
                                ip,
                                Collections.singletonList(Collections.singletonMap(ip, 1809)),
                                Collections.singletonList(Collections.singletonMap(ip, 2008)),
                                Collections.emptyList()
                        )).write(NodeConfig.PATH);
                    }
            )).addQuestion(new DefaultSetupQuestion(
                    "Please copy the connection key for other nodes into the console (if there is any other node), generate one using \"gen\" or type \"null\"",
                    "",
                    s -> true,
                    s -> {
                        if (s.equalsIgnoreCase("null")) {
                            return;
                        }

                        if (s.equalsIgnoreCase("gen")) {
                            s = StringUtil.generateString(50);
                        }

                        new JsonConfiguration().add("key", s).write("reformcloud/files/.connection/connection.json");
                    }
            )).startSetup(NodeExecutor.getInstance().getLoggerBase());

            System.out.println("Please choose a default installation type:");
            GroupSetupHelper.printAvailable();

            String result = NodeExecutor.getInstance().getLoggerBase().readLineNoPrompt();
            while (!result.trim().isEmpty()) {
                GroupSetupVersion version = GroupSetupHelper.findByName(result);
                if (version == null) {
                    System.out.println("This setup type is not supported");
                    result = NodeExecutor.getInstance().getLoggerBase().readLineNoPrompt();
                    continue;
                }

                version.install(this::handleProcessGroupCreate, this::handleMainGroupCreate);
                System.out.println("Finished installation of " + version.getName() + "!");
                break;
            }

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
        processGroups.clear();
        mainGroups.clear();

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

    public NodeConfig reload() {
        this.nodeConfig = JsonConfiguration.read(NodeConfig.PATH).get("config", NodeConfig.TYPE);
        this.ingameMessages = JsonConfiguration.read("reformcloud/configs/messages.json").get("messages", IngameMessages.TYPE);
        this.self = this.nodeConfig.prepare();
        this.connectionKey = JsonConfiguration.read("reformcloud/files/.connection/connection.json").getOrDefault("key", (String) null);
        this.loadGroups();

        return this.nodeConfig;
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
        return new ArrayList<>(mainGroups);
    }

    public IngameMessages getIngameMessages() {
        return ingameMessages;
    }

    public List<ProcessGroup> getProcessGroups() {
        return new ArrayList<>(processGroups);
    }
}
