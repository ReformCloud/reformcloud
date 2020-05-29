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
package systems.reformcloud.reformcloud2.node.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.node.NodeProcess;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.config.NodeConfig;

import java.util.Collection;

public final class CommandCluster extends GlobalCommand {

    public CommandCluster() {
        super("cluster", "reformcloud.command.cluster", "Manages the node cluster", "clu");
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessages((
                "cluster list                      | Lists all connected nodes and all other nodes from the config\n" +
                        "cluster me                        | Shows information about the current node\n" +
                        "cluster head                      | Shows information about the head node\n" +
                        "cluster info <name>               | Shows information about the specified node\n" +
                        "cluster create <ip/domain> <port> | Creates a new node in the config for the specified host and port\n" +
                        "cluster delete <ip/domain>        | Deletes the specified node from the config"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        if (strings.length == 0) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
            this.listConnectedAndListenersToSender(commandSource);
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("me")) {
            this.showInformationAboutToSender(commandSource, NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode());
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("head")) {
            NodeInformation head = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getHeadNode();
            if (head == null) {
                head = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
            }

            this.showInformationAboutToSender(commandSource, head);
            return true;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("info")) {
            NodeInformation info = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getNode(strings[1]);
            if (info == null && strings[1].equals(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getName())) {
                info = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
            }

            if (info == null) {
                commandSource.sendMessage(LanguageManager.get("command-cluster-node-not-connected", strings[1]));
                return true;
            }

            this.showInformationAboutToSender(commandSource, info);
            return true;
        }

        if (strings.length == 3 && strings[0].equalsIgnoreCase("create")) {
            String ip = CommonHelper.getIpAddress(strings[1]);
            if (ip == null) {
                commandSource.sendMessage(LanguageManager.get("controller-setup-question-controller-address-wrong"));
                return true;
            }

            if (this.existsNode(ip)) {
                commandSource.sendMessage(LanguageManager.get("command-cluster-node-already-exists", ip));
                return true;
            }

            Integer port = CommonHelper.fromString(strings[2]);
            if (port == null || port < 0) {
                commandSource.sendMessage(LanguageManager.get("command-integer-failed", 0, strings[2]));
                return true;
            }

            NodeExecutor.getInstance().getNodeConfig().getClusterNodes().add(new NodeConfig.NetworkAddress(ip, port));
            NodeExecutor.getInstance().getNodeConfig().save();
            commandSource.sendMessage(LanguageManager.get("command-cluster-created-node", ip, strings[2]));
            return true;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("delete")) {
            String ip = CommonHelper.getIpAddress(strings[1]);
            if (ip == null) {
                commandSource.sendMessage(LanguageManager.get("controller-setup-question-controller-address-wrong"));
                return true;
            }

            NodeConfig.NetworkAddress address = Streams.filter(
                    NodeExecutor.getInstance().getNodeConfig().getClusterNodes(),
                    e -> e.getHost().equals(ip.trim())
            );
            if (address == null) {
                commandSource.sendMessage(LanguageManager.get("command-cluster-node-not-exists", ip));
                return true;
            }

            NodeExecutor.getInstance().getNodeConfig().getClusterNodes().remove(address);
            NodeExecutor.getInstance().getNodeConfig().save();
            commandSource.sendMessage(LanguageManager.get("command-cluster-node-deleted", ip));
            return true;
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private boolean existsNode(@NotNull String host) {
        return NodeExecutor
                .getInstance()
                .getNodeConfig()
                .getClusterNodes()
                .stream()
                .anyMatch(e -> e.getHost().equals(host.trim()));
    }

    private void showInformationAboutToSender(@NotNull CommandSource source, @NotNull NodeInformation information) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" > Name            - ").append(information.getName()).append("\n");
        stringBuilder.append(" > UniqueID        - ").append(information.getNodeUniqueID()).append("\n");
        stringBuilder.append(" > Memory          - ").append(information.getUsedMemory()).append("MB/").append(information.getMaxMemory()).append("MB\n");
        stringBuilder.append(" > OS              - ").append(information.getProcessRuntimeInformation().getOsVersion()).append("\n");
        stringBuilder.append(" > OS-Arch         - ").append(information.getProcessRuntimeInformation().getSystemArchitecture()).append("\n");
        stringBuilder.append(" > Java            - ").append(information.getProcessRuntimeInformation().getJavaVersion()).append("\n");
        stringBuilder.append(" > Cores           - ").append(information.getProcessRuntimeInformation().getProcessorCount()).append("\n");
        stringBuilder.append(" > Threads         - ").append(information.getProcessRuntimeInformation().getThreadInfos().size()).append("\n");
        stringBuilder.append(" > Heap Memory     - ").append(information.getProcessRuntimeInformation().getMemoryUsageInternal()).append("MB").append("\n");
        stringBuilder.append(" > Non-Heap Memory - ").append(information.getProcessRuntimeInformation().getNonHeapMemoryUsage()).append("MB").append("\n");
        stringBuilder.append(" > CPU             - ").append(CommonHelper.DECIMAL_FORMAT.format(information.getProcessRuntimeInformation().getCpuUsageSystem())).append("%").append("\n");
        stringBuilder.append(" > Load average    - ").append(CommonHelper.DECIMAL_FORMAT.format(information.getProcessRuntimeInformation().getLoadAverageSystem())).append("MB").append("\n");
        stringBuilder.append(" > Start time      - ").append(CommonHelper.DATE_FORMAT.format(information.getStartupTime())).append("\n");
        stringBuilder.append(" > Last Update     - ").append(CommonHelper.DATE_FORMAT.format(information.getLastUpdate())).append("\n");
        stringBuilder.append(" ").append("\n");
        stringBuilder.append(" > Started processes (").append(information.getStartedProcesses().size()).append(")").append("\n");
        for (NodeProcess startedProcess : information.getStartedProcesses()) {
            stringBuilder.append("\n");
            stringBuilder.append("  > Name           - ").append(startedProcess.getName()).append("\n");
            stringBuilder.append("  > UniqueID       - ").append(startedProcess.getUniqueID()).append("\n");
            stringBuilder.append("  > Group          - ").append(startedProcess.getGroup()).append("\n");
            stringBuilder.append(" ");
        }

        source.sendMessages(stringBuilder.toString().split("\n"));
    }

    private void listConnectedAndListenersToSender(CommandSource source) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Known nodes (").append(NodeExecutor.getInstance().getNodeConfig().getClusterNodes().size()).append(")").append("\n");
        for (NodeConfig.NetworkAddress clusterNode : NodeExecutor.getInstance().getNodeConfig().getClusterNodes()) {
            stringBuilder.append(" > ").append(clusterNode.getHost()).append(":").append(clusterNode.getPort()).append("\n");
        }

        stringBuilder.append("\n");

        Collection<NodeInformation> connectedNodes = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getConnectedNodes();

        stringBuilder.append("Connected nodes (").append(connectedNodes.size()).append(")").append("\n");
        for (NodeInformation connectedNode : connectedNodes) {
            stringBuilder.append(" > ").append(connectedNode.getName()).append("/").append(connectedNode.getNodeUniqueID()).append("\n");
        }

        source.sendMessages(stringBuilder.toString().split("\n"));
    }
}
