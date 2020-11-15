/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.groups.setup.GroupSetupHelper;
import systems.reformcloud.reformcloud2.executor.api.groups.setup.GroupSetupVersion;
import systems.reformcloud.reformcloud2.executor.api.language.TranslationHolder;
import systems.reformcloud.reformcloud2.shared.network.SimpleNetworkAddress;
import systems.reformcloud.reformcloud2.shared.StringUtil;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.setup.DefaultSetup;
import systems.reformcloud.reformcloud2.node.setup.DefaultSetupQuestion;
import systems.reformcloud.reformcloud2.node.setup.Setup;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;
import systems.reformcloud.reformcloud2.shared.network.NetworkUtils;
import systems.reformcloud.reformcloud2.shared.platform.Platform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static systems.reformcloud.reformcloud2.executor.api.utility.list.Streams.newCollection;

public final class NodeExecutorConfig {

    private static final Collection<Path> PATHS = newCollection(
        Paths::get,
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

    private NodeConfig nodeConfig;
    private String connectionKey;
    private IngameMessages ingameMessages;

    public void init() {
        this.createDirectories();
        if (Files.notExists(NodeConfig.PATH)) {
            AtomicReference<String> nodeName = new AtomicReference<>();
            AtomicReference<String> networkAddress = new AtomicReference<>();
            AtomicInteger networkPort = new AtomicInteger();
            AtomicInteger httpPort = new AtomicInteger();
            AtomicBoolean runClusterSetup = new AtomicBoolean(false);
            List<SimpleNetworkAddress> clusterNodes = new ArrayList<>();

            String ips = String.join(", ", NetworkUtils.getAllAvailableIpAddresses());

            this.setup.addQuestion(new DefaultSetupQuestion(
                setupAnswer -> {
                    if (setupAnswer.getOriginalAnswer().equalsIgnoreCase("null")) {
                        nodeName.set("Node-" + UUID.randomUUID().toString().split("-")[0]);
                    } else {
                        nodeName.set(setupAnswer.getOriginalAnswer());
                    }

                    return true;
                },
                "",
                TranslationHolder.translate("node-setup-question-node-name")
            )).addQuestion(new DefaultSetupQuestion(
                setupAnswer -> {
                    String address = NetworkUtils.validateAndGetIpAddress(setupAnswer.getOriginalAnswer());
                    if (address != null) {
                        networkAddress.set(address);
                    }

                    return address != null;
                },
                TranslationHolder.translate("node-setup-question-node-address-wrong"),
                TranslationHolder.translate("node-setup-question-node-address", ips)
            )).addQuestion(new DefaultSetupQuestion(
                setupAnswer -> {
                    Integer port = setupAnswer.getAsInt();
                    if (port != null && port > 0 && port < 65535) {
                        networkPort.set(port);
                        return true;
                    }

                    return false;
                },
                TranslationHolder.translate("node-setup-question-integer", 0, 65535),
                TranslationHolder.translate("node-setup-question-node-network-port")
            )).addQuestion(new DefaultSetupQuestion(
                setupAnswer -> {
                    Integer webPort = setupAnswer.getAsInt();
                    if (webPort != null && webPort > 0 && webPort < 65535) {
                        httpPort.set(webPort);
                        return true;
                    }

                    return false;
                },
                TranslationHolder.translate("node-setup-question-integer", 0, 65535),
                TranslationHolder.translate("node-setup-question-node-web-port")
            )).addQuestion(new DefaultSetupQuestion(
                setupAnswer -> {
                    String key = setupAnswer.getOriginalAnswer();
                    if (setupAnswer.getOriginalAnswer().equalsIgnoreCase("gen")) {
                        key = StringUtil.generateRandomString(512);
                    }

                    new JsonConfiguration().add("key", key).write("reformcloud/files/.connection/connection.json");
                    return true;
                },
                "",
                TranslationHolder.translate("node-setup-question-connection-key")
            )).addQuestion(new DefaultSetupQuestion(
                setupAnswer -> {
                    Boolean clusterSetup = setupAnswer.getAsBoolean();
                    if (clusterSetup != null) {
                        runClusterSetup.set(clusterSetup);
                    }

                    return clusterSetup != null;
                },
                TranslationHolder.translate("node-setup-question-boolean"),
                TranslationHolder.translate("node-setup-in-cluster")
            )).runSetup();

            if (runClusterSetup.get()) {
                clusterNodes.addAll(this.runClusterSetup());
            }

            int maxMemory = Platform.getTotalSystemMemory() - 1024;
            if (maxMemory < 512) {
                System.err.println(TranslationHolder.translate("start-config-low-memory"));
                maxMemory = 512;
            }

            new JsonConfiguration().add("config", new NodeConfig(
                nodeName.get(),
                UUID.randomUUID(),
                maxMemory,
                networkAddress.get(),
                new ArrayList<>(Collections.singletonList(new SimpleNetworkAddress(networkAddress.get(), networkPort.get()))),
                new ArrayList<>(Collections.singletonList(new SimpleNetworkAddress(networkAddress.get(), httpPort.get()))),
                clusterNodes
            )).write(NodeConfig.PATH);

            System.out.println(TranslationHolder.translate("general-setup-choose-default-installation"));
            GroupSetupHelper.printAvailable();

            String result = NodeExecutor.getInstance().getConsole().readString().getUninterruptedly();
            while (result != null && !result.trim().isEmpty()) {
                GroupSetupVersion version = GroupSetupHelper.findByName(result);
                if (version == null) {
                    System.out.println(TranslationHolder.translate("general-setup-choose-default-installation-wrong"));
                    result = NodeExecutor.getInstance().getConsole().readString().getUninterruptedly();
                    continue;
                }

                version.install(
                    NodeExecutor.getInstance().getDefaultProcessGroupProvider()::addProcessGroup,
                    NodeExecutor.getInstance().getDefaultMainGroupProvider()::addGroup
                );
                System.out.println(TranslationHolder.translate("general-setup-default-installation-done", version.getName()));
                break;
            }

            new JsonConfiguration().add("messages", new IngameMessages()).write(Path.of("reformcloud/configs/messages.json"));
        }

        this.nodeConfig = JsonConfiguration.read(NodeConfig.PATH).get("config", NodeConfig.class);
        this.ingameMessages = JsonConfiguration.read("reformcloud/configs/messages.json").get("messages", IngameMessages.TYPE);
        this.connectionKey = JsonConfiguration.read("reformcloud/files/.connection/connection.json").getOrDefault("key", (String) null);
    }

    public NodeConfig reload() {
        this.nodeConfig = JsonConfiguration.read(NodeConfig.PATH).get("config", NodeConfig.class);
        this.ingameMessages = JsonConfiguration.read("reformcloud/configs/messages.json").get("messages", IngameMessages.TYPE);
        this.connectionKey = JsonConfiguration.read("reformcloud/files/.connection/connection.json").getOrDefault("key", (String) null);

        return this.nodeConfig;
    }

    @NotNull
    private Collection<SimpleNetworkAddress> runClusterSetup() {
        this.setup.clear();

        AtomicInteger nodeCount = new AtomicInteger(1);
        this.setup.addQuestion(new DefaultSetupQuestion(
            setupAnswer -> {
                Integer count = setupAnswer.getAsInt();
                if (count != null && count > 0 && count < 100) {
                    nodeCount.set(count);
                    return true;
                }

                return false;
            },
            TranslationHolder.translate("node-setup-question-integer", 0, 100),
            TranslationHolder.translate("node-cluster-setup-node-count")
        )).runSetup();

        AtomicReference<String> nodeHost = new AtomicReference<>();
        Collection<SimpleNetworkAddress> out = new ArrayList<>();

        this.setup.clear();
        this.setup.addQuestion(new DefaultSetupQuestion(
            setupAnswer -> {
                String address = NetworkUtils.validateAndGetIpAddress(setupAnswer.getOriginalAnswer());
                if (address != null) {
                    nodeHost.set(address);
                }

                return address != null;
            },
            TranslationHolder.translate("node-setup-question-node-address-wrong"),
            TranslationHolder.translate("node-cluster-setup-new-node-host")
        )).addQuestion(new DefaultSetupQuestion(
            setupAnswer -> {
                Integer nodePort = setupAnswer.getAsInt();
                if (nodePort != null && nodePort > 0 && nodePort < 65535) {
                    out.add(new SimpleNetworkAddress(nodeHost.get(), nodePort));
                    return true;
                }

                return false;
            },
            TranslationHolder.translate("node-setup-question-integer", 0, 65535),
            TranslationHolder.translate("node-cluster-setup-new-node-port")
        ));

        for (int i = 1; i <= nodeCount.get(); i++) {
            System.out.println(TranslationHolder.translate("node-cluster-setup-new-node", i));
            this.setup.runSetup();
        }

        return out;
    }

    private void createDirectories() {
        PATHS.forEach(IOUtils::createDirectory);
    }

    public NodeConfig getNodeConfig() {
        return this.nodeConfig;
    }

    public String getConnectionKey() {
        return this.connectionKey;
    }

    public IngameMessages getIngameMessages() {
        return this.ingameMessages;
    }
}
