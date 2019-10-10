package systems.reformcloud.reformcloud2.executor.node.config;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultMainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.Version;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.Setup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetupQuestion;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.registry.Registry;
import systems.reformcloud.reformcloud2.executor.api.common.registry.basic.RegistryBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import static systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links.newCollection;

public class NodeExecutorConfig {

    private static final Collection<Path> PATHS = newCollection(
            s -> Paths.get(s),
            "reformcloud/groups/main",
            "reformcloud/groups/sub",
            "reformcloud/configs",
            "reformcloud/applications"
    );

    private final Setup setup = new DefaultSetup();

    private NodeInformation self;

    private NodeConfig nodeConfig;

    private String connectionKey;

    private final AtomicBoolean firstStartup = new AtomicBoolean(false);

    private final Registry localMainGroupsRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/main"));

    private final Registry localSubGroupsRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/sub"));

    public void init() {
        createDirectories();
        if (!Files.exists(NodeConfig.PATH)) {
            firstStartup.set(true);
            setup.addQuestion(new DefaultSetupQuestion(
                    "Please enter the start host of the client",
                    "Please enter your real address",
                    e -> e.split("\\.").length == 4,
                    e -> new JsonConfiguration().add("config", new NodeConfig(
                            CommonHelper.calculateMaxMemory(),
                            Collections.emptyList(),
                            Collections.singletonList(Collections.singletonMap(e, 2008)),
                            Collections.singletonList(Collections.singletonMap(e, 1809))
                    )).write(NodeConfig.PATH)
            )).addQuestion(new DefaultSetupQuestion(
                    "Please copy the connection key for other nodes into the console (if there is any other node)",
                    "",
                    s -> true,
                    s -> {
                        if (s.trim().isEmpty()) {
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
        }

        this.nodeConfig = JsonConfiguration.read(NodeConfig.PATH).get("config", NodeConfig.TYPE);
        this.self = this.nodeConfig.prepare();
        this.connectionKey = JsonConfiguration.read("reformcloud/files/.connection/connection.json").getString("key");
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

    public boolean isFirstStartup() {
        return firstStartup.get();
    }
}
