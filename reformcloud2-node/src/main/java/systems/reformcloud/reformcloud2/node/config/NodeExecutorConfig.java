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
package systems.reformcloud.reformcloud2.node.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.groups.setup.GroupSetupHelper;
import systems.reformcloud.reformcloud2.executor.api.common.groups.setup.GroupSetupVersion;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.Setup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetupQuestion;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.registry.Registry;
import systems.reformcloud.reformcloud2.executor.api.common.registry.basic.RegistryBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.node.NodeExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams.newCollection;

public final class NodeExecutorConfig {

    private static final Collection<Path> PATHS = newCollection(
            s -> Paths.get(s),
            "reformcloud/groups/main",
            "reformcloud/groups/sub",
            "reformcloud/configs",
            "reformcloud/applications",
            "reformcloud/temp",
            "reformcloud/static",
            "reformcloud/templates",
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
    private IngameMessages ingameMessages;

    public void init() {
        this.createDirectories();
        if (!Files.exists(NodeConfig.PATH)) {
            this.firstStartup.set(true);

            AtomicReference<String> nodeName = new AtomicReference<>();
            AtomicReference<String> networkAddress = new AtomicReference<>();
            AtomicInteger networkPort = new AtomicInteger();
            AtomicInteger httpPort = new AtomicInteger();
            AtomicBoolean runClusterSetup = new AtomicBoolean(false);
            List<NodeConfig.NetworkAddress> clusterNodes = new ArrayList<>();

            this.setup.addQuestion(new DefaultSetupQuestion(
                    LanguageManager.get("node-setup-question-node-name"),
                    "",
                    s -> true,
                    e -> {
                        String name = e;
                        if (e.trim().equals("null")) {
                            name = "Node-" + UUID.randomUUID().toString().split("-")[0];
                        }

                        nodeName.set(name);
                    }
            )).addQuestion(new DefaultSetupQuestion(
                    LanguageManager.get("node-setup-question-node-address"),
                    LanguageManager.get("node-setup-question-node-address-wrong"),
                    e -> CommonHelper.getIpAddress(e.trim()) != null,
                    e -> networkAddress.set(CommonHelper.getIpAddress(e.trim()))
            )).addQuestion(new DefaultSetupQuestion(
                    LanguageManager.get("node-setup-question-node-network-port"),
                    LanguageManager.get("node-setup-question-integer", 0, 65535),
                    e -> {
                        Integer integer = CommonHelper.fromString(e);
                        return integer != null && integer > 0 && integer < 65535;
                    },
                    e -> {
                        Integer integer = CommonHelper.fromString(e);
                        if (integer == null) {
                            throw new RuntimeException(e);
                        }

                        networkPort.set(integer);
                    }
            )).addQuestion(new DefaultSetupQuestion(
                    LanguageManager.get("node-setup-question-node-web-port"),
                    LanguageManager.get("node-setup-question-integer", 0, 65535),
                    e -> {
                        Integer integer = CommonHelper.fromString(e);
                        return integer != null && integer > 0 && integer < 65535;
                    },
                    e -> {
                        Integer integer = CommonHelper.fromString(e);
                        if (integer == null) {
                            throw new RuntimeException(e);
                        }

                        httpPort.set(integer);
                    }
            )).addQuestion(new DefaultSetupQuestion(
                    LanguageManager.get("node-setup-question-connection-key"),
                    "",
                    s -> true,
                    s -> {
                        if (s.equalsIgnoreCase("gen")) {
                            s = StringUtil.generateString(1);
                        }

                        new JsonConfiguration().add("key", s).write("reformcloud/files/.connection/connection.json");
                    }
            )).addQuestion(new DefaultSetupQuestion(
                    LanguageManager.get("node-setup-in-cluster"),
                    LanguageManager.get("node-setup-question-boolean"),
                    s -> CommonHelper.booleanFromString(s) != null,
                    s -> {
                        if (s.equalsIgnoreCase("true")) {
                            runClusterSetup.set(true);
                        }
                    }
            )).startSetup(NodeExecutor.getInstance().getLoggerBase());

            if (runClusterSetup.get()) {
                clusterNodes.addAll(this.runClusterSetup());
            }

            new JsonConfiguration().add("config", new NodeConfig(
                    nodeName.get(),
                    UUID.randomUUID(),
                    CommonHelper.calculateMaxMemory(),
                    networkAddress.get(),
                    new ArrayList<>(Collections.singletonList(new NodeConfig.NetworkAddress(networkAddress.get(), networkPort.get()))),
                    new ArrayList<>(Collections.singletonList(new NodeConfig.NetworkAddress(networkAddress.get(), httpPort.get()))),
                    clusterNodes
            )).write(NodeConfig.PATH);

            System.out.println(LanguageManager.get("general-setup-choose-default-installation"));
            GroupSetupHelper.printAvailable();

            String result = NodeExecutor.getInstance().getLoggerBase().readLineNoPrompt();
            while (!result.trim().isEmpty()) {
                GroupSetupVersion version = GroupSetupHelper.findByName(result);
                if (version == null) {
                    System.out.println(LanguageManager.get("general-setup-choose-default-installation-wrong"));
                    result = NodeExecutor.getInstance().getLoggerBase().readLineNoPrompt();
                    continue;
                }

                version.install(this::handleProcessGroupCreate, this::handleMainGroupCreate);
                System.out.println(LanguageManager.get("general-setup-default-installation-done", version.getName()));
                break;
            }

            new JsonConfiguration().add("messages", new IngameMessages()).write(Paths.get("reformcloud/configs/messages.json"));
        }

        this.nodeConfig = JsonConfiguration.read(NodeConfig.PATH).get("config", NodeConfig.TYPE);
        this.ingameMessages = JsonConfiguration.read("reformcloud/configs/messages.json").get("messages", IngameMessages.TYPE);
        this.self = this.nodeConfig.prepare();
        this.connectionKey = JsonConfiguration.read("reformcloud/files/.connection/connection.json").getOrDefault("key", (String) null);
        this.loadGroups();
    }

    private void loadGroups() {
        this.processGroups.clear();
        this.mainGroups.clear();

        this.processGroups.addAll(this.localSubGroupsRegistry.readKeys(e -> e.get("key", ProcessGroup.TYPE)));
        this.mainGroups.addAll(this.localMainGroupsRegistry.readKeys(e -> e.get("key", MainGroup.TYPE)));
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

        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(
                e -> ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(e.getProcessDetail().getProcessUniqueID())
        );
    }

    public void handleMainGroupDelete(MainGroup mainGroup) {
        this.localMainGroupsRegistry.deleteKey(mainGroup.getName());
    }

    public NodeConfig reload() {
        this.nodeConfig = JsonConfiguration.read(NodeConfig.PATH).get("config", NodeConfig.TYPE);
        this.ingameMessages = JsonConfiguration.read("reformcloud/configs/messages.json").get("messages", IngameMessages.TYPE);
        this.self.setMaxMemory(this.nodeConfig.getMaxMemory());
        this.connectionKey = JsonConfiguration.read("reformcloud/files/.connection/connection.json").getOrDefault("key", (String) null);
        this.loadGroups();

        return this.nodeConfig;
    }

    @NotNull
    private Collection<NodeConfig.NetworkAddress> runClusterSetup() {
        this.setup.clear();

        AtomicInteger nodeCount = new AtomicInteger(1);
        this.setup.addQuestion(new DefaultSetupQuestion(
                LanguageManager.get("node-cluster-setup-node-count"),
                LanguageManager.get("node-setup-question-integer", 0, 100),
                s -> {
                    Integer integer = CommonHelper.fromString(s);
                    return integer != null && integer > 0 && integer < 100;
                },
                s -> {
                    Integer integer = CommonHelper.fromString(s);
                    if (integer == null) {
                        throw new IllegalStateException();
                    }

                    nodeCount.set(integer);
                }
        )).startSetup(NodeExecutor.getInstance().getLoggerBase());

        AtomicReference<String> nodeHost = new AtomicReference<>();
        Collection<NodeConfig.NetworkAddress> out = new ArrayList<>();

        this.setup.clear();
        this.setup.addQuestion(new DefaultSetupQuestion(
                LanguageManager.get("node-cluster-setup-new-node-host"),
                LanguageManager.get("node-setup-question-node-address-wrong"),
                s -> {
                    String ipAddress = CommonHelper.getIpAddress(s.trim());
                    return ipAddress != null && out.stream().noneMatch(e -> e.getHost().equals(ipAddress));
                },
                s -> nodeHost.set(CommonHelper.getIpAddress(s.trim()))
        )).addQuestion(new DefaultSetupQuestion(
                LanguageManager.get("node-cluster-setup-new-node-port"),
                LanguageManager.get("node-setup-question-integer", 0, 65535),
                s -> CommonHelper.fromString(s) != null,
                s -> {
                    Integer integer = CommonHelper.fromString(s);
                    if (integer == null) {
                        throw new IllegalStateException();
                    }

                    out.add(new NodeConfig.NetworkAddress(nodeHost.get(), integer));
                }
        ));

        for (int i = 1; i <= nodeCount.get(); i++) {
            System.out.println(LanguageManager.get("node-cluster-setup-new-node", i));
            this.setup.startSetup(NodeExecutor.getInstance().getLoggerBase());
        }

        return out;
    }

    private void createDirectories() {
        PATHS.forEach(SystemHelper::createDirectory);
    }

    public NodeInformation getSelf() {
        return this.self;
    }

    public NodeConfig getNodeConfig() {
        return this.nodeConfig;
    }

    public String getConnectionKey() {
        return this.connectionKey;
    }

    public boolean isFirstStartup() {
        return this.firstStartup.get();
    }

    public List<MainGroup> getMainGroups() {
        return new ArrayList<>(this.mainGroups);
    }

    public IngameMessages getIngameMessages() {
        return this.ingameMessages;
    }

    public List<ProcessGroup> getProcessGroups() {
        return new ArrayList<>(this.processGroups);
    }
}
