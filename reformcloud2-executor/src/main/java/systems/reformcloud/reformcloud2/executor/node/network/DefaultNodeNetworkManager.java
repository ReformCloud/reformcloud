package systems.reformcloud.reformcloud2.executor.node.network;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.matcher.PreparedProcessFilter;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutStartPreparedProcess;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutStopProcess;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutToHeadStartPreparedProcess;
import systems.reformcloud.reformcloud2.executor.node.network.packet.query.out.NodePacketOutQueryStartProcess;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.startup.LocalProcessQueue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultNodeNetworkManager implements NodeNetworkManager {

    private static final Map<UUID, String> QUEUED_PROCESSES = new ConcurrentHashMap<>();

    private static final Queue<Duo<ProcessConfiguration, Boolean>> LATER = new ConcurrentLinkedQueue<>();

    public DefaultNodeNetworkManager(NodeProcessManager processManager, InternalNetworkCluster cluster) {
        this.localNodeProcessManager = processManager;
        this.cluster = cluster;

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (!LATER.isEmpty()) {
                Duo<ProcessConfiguration, Boolean> duo = LATER.poll();
                this.startProcessInternal(duo.getFirst(), false, duo.getSecond());
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private final NodeProcessManager localNodeProcessManager;

    private final InternalNetworkCluster cluster;

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
    public synchronized ProcessInformation prepareProcess(ProcessConfiguration configuration, boolean start) {
        return this.startProcessInternal(configuration, true, start);
    }

    @Override
    public synchronized ProcessInformation startProcess(ProcessConfiguration configuration) {
        ProcessInformation matching = PreparedProcessFilter.findMayMatchingProcess(
                configuration, this.getPreparedProcesses(configuration.getBase().getName())
        );
        if (matching != null && matching.getProcessState().equals(ProcessState.PREPARED)) {
            System.out.println(LanguageManager.get(
                    "process-start-already-prepared-process",
                    configuration.getBase().getName(),
                    matching.getName()
            ));
            this.startProcess(matching);
            return matching;
        }

        return this.startProcessInternal(configuration, true, true);
    }

    @Override
    public synchronized ProcessInformation startProcess(ProcessInformation processInformation) {
        if (getCluster().isSelfNodeHead()) {
            DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(
                    e -> e.sendPacket(new NodePacketOutStartPreparedProcess(processInformation))
            ).ifEmpty(e -> {
                if (processInformation.getNodeUniqueID() != null
                        && processInformation.getNodeUniqueID().equals(cluster.getSelfNode().getNodeUniqueID())
                        && processInformation.getProcessState().equals(ProcessState.PREPARED)) {
                    LocalProcessManager.getNodeProcesses()
                            .stream()
                            .filter(p -> p.getProcessInformation().getProcessUniqueID().equals(processInformation.getProcessUniqueID()))
                            .findFirst()
                            .ifPresent(LocalProcessQueue::queue);
                }
            });

            processInformation.setProcessState(ProcessState.READY_TO_START);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);
        } else {
            DefaultChannelManager.INSTANCE.get(this.cluster.getHeadNode().getName()).ifPresent(
                    e -> e.sendPacket(new NodePacketOutToHeadStartPreparedProcess(processInformation))
            );
        }

        return processInformation;
    }

    private synchronized ProcessInformation startProcessInternal(ProcessConfiguration configuration, boolean informUser, boolean start) {
        Template template = configuration.getTemplate();
        if (template == null) {
            AtomicReference<Template> bestTemplate = new AtomicReference<>();
            configuration.getBase().getTemplates().forEach(template1 -> {
                if (bestTemplate.get() == null) {
                    bestTemplate.set(template1);
                } else {
                    if (bestTemplate.get().getPriority() < template1.getPriority()) {
                        bestTemplate.set(template1);
                    }
                }
            });

            if (bestTemplate.get() == null) {
                bestTemplate.set(new Template(0, "default", false, FileBackend.NAME, "#", new RuntimeConfiguration(
                        512, new ArrayList<>(), new HashMap<>()
                ), Version.PAPER_1_8_8));

                System.err.println("Starting up process " + configuration.getBase().getName() + " with default template because no template is set up");
                Thread.dumpStack();
            }

            template = bestTemplate.get();
        }

        if (getCluster().isSelfNodeHead()) {
            QUEUED_PROCESSES.put(configuration.getUniqueId(), configuration.getBase().getName());

            if (getCluster().noOtherNodes()) {
                if (configuration.getBase().getStartupConfiguration().isSearchBestClientAlone()) {
                    return localNodeProcessManager.prepareLocalProcess(configuration, template, start);
                }

                if (configuration.getBase().getStartupConfiguration().getUseOnlyTheseClients()
                        .contains(NodeExecutor.getInstance().getNodeConfig().getName())) {
                    return localNodeProcessManager.prepareLocalProcess(configuration, template, start);
                }

                LATER.add(new Duo<>(configuration, start));
                return null;
            }

            int maxMemory = configuration.getMaxMemory() == null ? template.getRuntimeConfiguration().getMaxMemory()
                    : configuration.getMaxMemory();

            NodeInformation best = getCluster().findBestNodeForStartup(
                    configuration.getBase(),
                    maxMemory
            );
            if (best != null && best.canEqual(getCluster().getHeadNode())) {
                return localNodeProcessManager.prepareLocalProcess(configuration, template, start);
            }

            if (best == null) {
                if (informUser) {
                    System.out.println(LanguageManager.get(
                            "node-process-no-node-queued",
                            configuration.getBase().getName(),
                            template.getName()
                    ));
                }

                LATER.add(new Duo<>(configuration, start));
                return null;
            }

            best.addUsedMemory(maxMemory);
            return localNodeProcessManager.queueProcess(configuration, template, best, start);
        }

        return getCluster().sendQueryToHead(new NodePacketOutQueryStartProcess(configuration, start),
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

    private List<ProcessInformation> getPreparedProcesses(String group) {
        return Streams.list(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(group),
                e -> e.getProcessState().equals(ProcessState.PREPARED)
        );
    }
}
