package systems.reformcloud.reformcloud2.executor.node.commands;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeProcess;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class CommandCluster extends GlobalCommand {

    public CommandCluster() {
        super("cluster", "reformcloud.command.cluster", "Manages the node cluster", "clu");
    }

    @Override
    public void describeCommandToSender(@Nonnull CommandSource source) {
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
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
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
            this.showInformationAboutToSender(commandSource, NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getHeadNode());
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

            NodeExecutor.getInstance().getNodeConfig().getOtherNodes().add(Collections.singletonMap(ip, port));
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

            if (!this.existsNode(ip)) {
                commandSource.sendMessage(LanguageManager.get("command-cluster-node-not-exists", ip));
                return true;
            }

            Map<String, Integer> map = this.getByHost(ip);
            NodeExecutor.getInstance().getNodeConfig().getOtherNodes().remove(map);
            NodeExecutor.getInstance().getNodeConfig().save();
            commandSource.sendMessage(LanguageManager.get("command-cluster-node-deleted", ip));
            return true;
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private boolean existsNode(String host) {
        return getByHost(host) != null;
    }

    private Map<String, Integer> getByHost(String host) {
        for (Map<String, Integer> otherNode : NodeExecutor.getInstance().getNodeConfig().getOtherNodes()) {
            for (Map.Entry<String, Integer> stringIntegerEntry : otherNode.entrySet()) {
                if (stringIntegerEntry.getKey().equals(host)) {
                    return otherNode;
                }
            }
        }

        return null;
    }

    private void showInformationAboutToSender(CommandSource source, NodeInformation information) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" > Name       - ").append(information.getName()).append("\n");
        stringBuilder.append(" > UniqueID   - ").append(information.getNodeUniqueID()).append("\n");
        stringBuilder.append(" > Memory     - ").append(information.getUsedMemory()).append("MB/").append(information.getMaxMemory()).append("MB\n");
        stringBuilder.append(" > Start time - ").append(CommonHelper.DATE_FORMAT.format(information.getStartupTime())).append("\n");
        stringBuilder.append(" ").append("\n");
        stringBuilder.append(" > Started processes (").append(information.getStartedProcesses().size()).append(")").append("\n");
        for (NodeProcess startedProcess : information.getStartedProcesses()) {
            stringBuilder.append("\n");
            stringBuilder.append("  > Name      - ").append(startedProcess.getName()).append("\n");
            stringBuilder.append("  > UniqueID  - ").append(startedProcess.getUniqueID()).append("\n");
            stringBuilder.append("  > Group     - ").append(startedProcess.getGroup()).append("\n");
            stringBuilder.append(" ");
        }

        source.sendMessages(stringBuilder.toString().split("\n"));
    }

    private void listConnectedAndListenersToSender(CommandSource source) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Known nodes (").append(NodeExecutor.getInstance().getNodeConfig().getOtherNodes().size()).append(")").append("\n");
        for (Map<String, Integer> otherNode : NodeExecutor.getInstance().getNodeConfig().getOtherNodes()) {
            for (Map.Entry<String, Integer> stringIntegerEntry : otherNode.entrySet()) {
                stringBuilder.append(" > ").append(stringIntegerEntry.getKey()).append(":").append(stringIntegerEntry.getValue()).append("\n");
            }
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
