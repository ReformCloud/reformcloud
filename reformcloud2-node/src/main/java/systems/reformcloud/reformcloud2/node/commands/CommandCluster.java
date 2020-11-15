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
package systems.reformcloud.reformcloud2.node.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.language.TranslationHolder;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.shared.network.SimpleNetworkAddress;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.wrappers.NodeProcessWrapper;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.shared.Constants;
import systems.reformcloud.reformcloud2.shared.parser.Parsers;
import systems.reformcloud.reformcloud2.shared.network.NetworkUtils;
import systems.reformcloud.reformcloud2.shared.node.DefaultNodeInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class CommandCluster implements Command {

    private void describeCommandToSender(@NotNull CommandSender source) {
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
    public void process(@NotNull CommandSender sender, String[] strings, @NotNull String commandLine) {
        if (strings.length == 0) {
            this.describeCommandToSender(sender);
            return;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
            this.listConnectedAndListenersToSender(sender);
            return;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("me")) {
            this.showInformationAboutToSender(sender, NodeExecutor.getInstance().getCurrentNodeInformation());
            return;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("head")) {
            DefaultNodeInformation head = NodeExecutor.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).getHeadNode();
            this.showInformationAboutToSender(sender, head);
            return;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("info")) {
            Optional<NodeProcessWrapper> information = NodeExecutor.getInstance().getNodeInformationProvider().getNodeInformation(strings[1]);
            if (information.isEmpty()) {
                sender.sendMessage(TranslationHolder.translate("command-cluster-node-not-connected", strings[1]));
                return;
            }

            this.showInformationAboutToSender(sender, information.get().getNodeInformation());
            return;
        }

        if (strings.length == 3 && strings[0].equalsIgnoreCase("create")) {
            String ip = NetworkUtils.validateAndGetIpAddress(strings[1]);
            if (ip == null) {
                sender.sendMessage(TranslationHolder.translate("node-setup-question-address-wrong"));
                return;
            }

            if (this.existsNode(ip)) {
                sender.sendMessage(TranslationHolder.translate("command-cluster-node-already-exists", ip));
                return;
            }

            Integer port = Parsers.INT.parse(strings[2]);
            if (port == null || port < 0) {
                sender.sendMessage(TranslationHolder.translate("command-integer-failed", 0, strings[2]));
                return;
            }

            NodeExecutor.getInstance().getNodeConfig().getClusterNodes().add(new SimpleNetworkAddress(ip, port));
            NodeExecutor.getInstance().getNodeConfig().save();
            sender.sendMessage(TranslationHolder.translate("command-cluster-created-node", ip, strings[2]));
            return;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("delete")) {
            String ip = NetworkUtils.validateAndGetIpAddress(strings[1]);
            if (ip == null) {
                sender.sendMessage(TranslationHolder.translate("node-setup-question-address-wrong"));
                return;
            }

            SimpleNetworkAddress address = Streams.filter(
                NodeExecutor.getInstance().getNodeConfig().getClusterNodes(),
                e -> e.getHost().equals(ip.trim())
            );
            if (address == null) {
                sender.sendMessage(TranslationHolder.translate("command-cluster-node-not-exists", ip));
                return;
            }

            NodeExecutor.getInstance().getNodeConfig().getClusterNodes().remove(address);
            NodeExecutor.getInstance().getNodeConfig().save();
            sender.sendMessage(TranslationHolder.translate("command-cluster-node-deleted", ip));
            return;
        }

        this.describeCommandToSender(sender);
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender commandSender, String[] strings, int bufferIndex, @NotNull String commandLine) {
        List<String> result = new ArrayList<>();
        switch (bufferIndex) {
            case 0:
                result.addAll(Arrays.asList("list", "me", "head", "info", "create", "delete"));
                break;
            case 1:
                if (strings[0].equalsIgnoreCase("info")) {
                    result.addAll(ExecutorAPI.getInstance().getNodeInformationProvider().getNodeNames());
                } else if (strings[0].equalsIgnoreCase("create")) {
                    result.add("127.0.0.1");
                } else if (strings[0].equalsIgnoreCase("delete")) {
                    result.addAll(Streams.map(NodeExecutor.getInstance().getNodeConfig().getClusterNodes(), SimpleNetworkAddress::getHost));
                }

                break;
            case 2:
                if (strings[0].equalsIgnoreCase("create")) {
                    result.add("1809");
                }

                break;
            default:
                break;
        }

        return result;
    }

    private boolean existsNode(@NotNull String host) {
        return NodeExecutor
            .getInstance()
            .getNodeConfig()
            .getClusterNodes()
            .stream()
            .anyMatch(e -> e.getHost().equals(host.trim()));
    }

    private void showInformationAboutToSender(@NotNull CommandSender source, @NotNull NodeInformation information) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" > Name            - ").append(information.getName());
        if (NodeExecutor.getInstance().isOwnIdentity(information.getName())) {
            stringBuilder.append(" (me)");
        }

        stringBuilder.append("\n");
        stringBuilder.append(" > UniqueID        - ").append(information.getUniqueId()).append("\n");
        stringBuilder.append(" > Memory          - ").append(information.getUsedMemory()).append("MB/").append(information.getMaxMemory()).append("MB\n");
        stringBuilder.append(" > OS              - ").append(information.getProcessRuntimeInformation().getOsVersion()).append("\n");
        stringBuilder.append(" > OS-Arch         - ").append(information.getProcessRuntimeInformation().getSystemArchitecture()).append("\n");
        stringBuilder.append(" > Java            - ").append(information.getProcessRuntimeInformation().getJavaVersion()).append("\n");
        stringBuilder.append(" > Cores           - ").append(information.getProcessRuntimeInformation().getProcessorCount()).append("\n");
        stringBuilder.append(" > Heap Memory     - ").append(information.getProcessRuntimeInformation().getMemoryUsageInternal()).append("MB").append("\n");
        stringBuilder.append(" > Non-Heap Memory - ").append(information.getProcessRuntimeInformation().getNonHeapMemoryUsage()).append("MB").append("\n");
        stringBuilder.append(" > CPU             - ").append(Constants.TWO_POINT_THREE_DECIMAL_FORMAT.format(information.getProcessRuntimeInformation().getCpuUsageSystem())).append("%").append("\n");
        stringBuilder.append(" > Load average    - ").append(Constants.TWO_POINT_THREE_DECIMAL_FORMAT.format(information.getProcessRuntimeInformation().getLoadAverageSystem())).append("MB").append("\n");
        stringBuilder.append(" > Start time      - ").append(Constants.FULL_DATE_FORMAT.format(information.getStartupMillis())).append("\n");
        stringBuilder.append(" > Last Update     - ").append(Constants.FULL_DATE_FORMAT.format(information.getLastUpdateTimestamp())).append("\n");

        source.sendMessages(stringBuilder.toString().split("\n"));
    }

    private void listConnectedAndListenersToSender(CommandSender source) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Known nodes (").append(NodeExecutor.getInstance().getNodeConfig().getClusterNodes().size()).append(")").append("\n");
        for (SimpleNetworkAddress clusterNode : NodeExecutor.getInstance().getNodeConfig().getClusterNodes()) {
            stringBuilder.append(" > ").append(clusterNode.getHost()).append(":").append(clusterNode.getPort()).append("\n");
        }

        stringBuilder.append("\n");

        Collection<NodeInformation> connectedNodes = NodeExecutor.getInstance().getNodeInformationProvider().getNodes();

        stringBuilder.append("Connected nodes (").append(connectedNodes.size()).append(")").append("\n");
        for (NodeInformation connectedNode : connectedNodes) {
            stringBuilder.append(" > ").append(connectedNode.getName()).append("/").append(connectedNode.getUniqueId());
            if (NodeExecutor.getInstance().isOwnIdentity(connectedNode.getName())) {
                stringBuilder.append(" (me)");
            }

            stringBuilder.append("\n");
        }

        source.sendMessages(stringBuilder.toString().split("\n"));
    }
}
