package systems.reformcloud.reformcloud2.executor.node.process.log;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen.NodePacketOutScreenEnabled;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen.NodePacketOutScreenLineAdded;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NodeProcessScreen {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public NodeProcessScreen(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    private final UUID uniqueID;

    private final Deque<String> queue = new ConcurrentLinkedDeque<>();

    private final Collection<String> enabledFrom = new ArrayList<>();

    public void toggleFor(String name) {
        if (enabledFrom.contains(name)) {
            enabledFrom.remove(name);
            return;
        }

        enabledFrom.add(name);
        if (name.equals(NodeExecutor.getInstance().getNodeConfig().getName())) {
            ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uniqueID);
            if (processInformation != null) {
                queue.forEach(line -> System.out.println(LanguageManager.get("screen-line-added", processInformation.getName(), line)));
            }
        } else {
            DefaultChannelManager.INSTANCE.get(name).ifPresent(e -> e.sendPacket(new NodePacketOutScreenEnabled(
                    uniqueID, queue
            )));
        }
    }

    void addScreenLine(String line) {
        if (line.equals(LINE_SEPARATOR)) {
            return;
        }

        while (queue.size() >= 128) {
            queue.remove();
        }

        queue.offerLast(line);
        enabledFrom.stream()
                .filter(e -> DefaultChannelManager.INSTANCE.get(e).isPresent())
                .map(e -> DefaultChannelManager.INSTANCE.get(e).orNothing())
                .filter(Objects::nonNull)
                .forEach(e -> e.sendPacket(new NodePacketOutScreenLineAdded(uniqueID, line)));

        ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uniqueID);
        if (processInformation != null && enabledFrom.contains(NodeExecutor.getInstance().getNodeConfig().getName())) {
                System.out.println(LanguageManager.get("screen-line-added", processInformation.getName(), line));
        }
    }

    public UUID getUniqueID() {
        return uniqueID;
    }
}
