package systems.reformcloud.reformcloud2.executor.node.network;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Trio;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutStopProcess;
import systems.reformcloud.reformcloud2.executor.node.network.packet.query.out.NodePacketOutQueryStartProcess;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DefaultNodeNetworkManager implements NodeNetworkManager {

    private static final Map<UUID, String> QUEUED_PROCESSES = new ConcurrentHashMap<>();

    private static final Queue<Trio<ProcessGroup, Template, JsonConfiguration>> LATER = new ConcurrentLinkedQueue<>();
    private final NodeProcessManager localNodeProcessManager;
    private final InternalNetworkCluster cluster;

    public DefaultNodeNetworkManager(NodeProcessManager processManager, InternalNetworkCluster cluster) {
        this.localNodeProcessManager = processManager;
        this.cluster = cluster;

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (!LATER.isEmpty()) {
                Trio<ProcessGroup, Template, JsonConfiguration> next = LATER.poll();
                this.startProcessInternal(next.getFirst(), next.getSecond(), next.getThird(), false);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public NodeProcessManager getNodeProcessHelper() {
        return localNodeProcessManager;
    }

    @Override
    public InternalNetworkCluster getCluster() {
        return cluster;
    }

    @Override
    public ProcessInformation getCloudProcess(String name) {
        return localNodeProcessManager.getClusterProcess(name);
    }

    @Override
    public ProcessInformation getCloudProcess(UUID uuid) {
        return localNodeProcessManager.getClusterProcess(uuid);
    }

    @Override
    public ProcessInformation startProcess(ProcessGroup processGroup, Template template, JsonConfiguration data) {
        return startProcessInternal(processGroup, template, data, true);
    }

    private ProcessInformation startProcessInternal(ProcessGroup processGroup, Template template, JsonConfiguration data, boolean informUser) {
        if (processGroup == null) {
            return null;
        }

        if (template == null) {
            template = randomTemplate(processGroup);
        }

        if (data == null) {
            data = new JsonConfiguration();
        }

        if (getCluster().isSelfNodeHead()) {
            final UUID processUniqueID = UUID.randomUUID();
            QUEUED_PROCESSES.put(processUniqueID, processGroup.getName());

            if (getCluster().noOtherNodes()) {
                if (processGroup.getStartupConfiguration().isSearchBestClientAlone()) {
                    return localNodeProcessManager.startLocalProcess(processGroup, template, data, processUniqueID);
                }

                if (processGroup.getStartupConfiguration().getUseOnlyTheseClients().contains(NodeExecutor.getInstance().getNodeConfig().getName())) {
                    return localNodeProcessManager.startLocalProcess(processGroup, template, data, processUniqueID);
                }

                return null;
            }

            NodeInformation best = getCluster().findBestNodeForStartup(processGroup, template);
            if (best != null && best.canEqual(getCluster().getHeadNode())) {
                return localNodeProcessManager.startLocalProcess(processGroup, template, data, processUniqueID);
            }

            if (best == null) {
                if (informUser) {
                    System.out.println(LanguageManager.get("node-process-no-node-queued", processGroup.getName(), template.getName()));
                }

                LATER.add(new Trio<>(processGroup, template, data));
                return null;
            }

            best.addUsedMemory(template.getRuntimeConfiguration().getMaxMemory());
            return localNodeProcessManager.queueProcess(processGroup, template, data, best, processUniqueID);
        }

        return getCluster().sendQueryToHead(new NodePacketOutQueryStartProcess(processGroup, template, data),
                packet -> packet.content().get("result", ProcessInformation.TYPE
                ));
    }

    @Override
    public void stopProcess(String name) {
        ProcessInformation information = localNodeProcessManager.getClusterProcess(name);
        if (information == null) {
            return;
        }

        stopProcess(information.getProcessUniqueID());
    }

    @Override
    public void stopProcess(UUID uuid) {
        if (localNodeProcessManager.isLocal(uuid)) {
            localNodeProcessManager.stopLocalProcess(uuid);
            return;
        }

        ProcessInformation information = localNodeProcessManager.getClusterProcess(uuid);
        if (information == null) {
            return;
        }

        DefaultChannelManager.INSTANCE.get(information.getParent()).ifPresent(e -> e.sendPacket(new NodePacketOutStopProcess(uuid)));
    }

    @Override
    public Map<UUID, String> getQueuedProcesses() {
        return QUEUED_PROCESSES;
    }

    private Template randomTemplate(ProcessGroup processGroup) {
        if (processGroup.getTemplates().isEmpty()) {
            return new Template(0, "default", false, FileBackend.NAME, "#", new RuntimeConfiguration(
                    512, new ArrayList<>(), new HashMap<>()
            ), Version.PAPER_1_8_8);
        }

        if (processGroup.getTemplates().size() == 1) {
            return processGroup.getTemplates().get(0);
        }

        return processGroup.getTemplates().get(new Random().nextInt(processGroup.getTemplates().size()));
    }
}
