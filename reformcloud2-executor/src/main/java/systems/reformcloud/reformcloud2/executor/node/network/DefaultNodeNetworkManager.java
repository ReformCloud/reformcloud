package systems.reformcloud.reformcloud2.executor.node.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
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
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.BiMap;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static systems.reformcloud.reformcloud2.executor.api.common.process.running.matcher.PreparedProcessFilter.findMayMatchingProcess;

public final class DefaultNodeNetworkManager implements NodeNetworkManager {

    private static final Queue<Duo<ProcessConfiguration, Boolean>> LATER = new ConcurrentLinkedQueue<>();

    private static final BiMap<String, UUID> PER_GROUP_WAITING = new BiMap<>();

    public DefaultNodeNetworkManager(@NotNull NodeProcessManager processManager, @NotNull InternalNetworkCluster cluster) {
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

    @NotNull
    @Override
    public NodeProcessManager getNodeProcessHelper() {
        return this.localNodeProcessManager;
    }

    @NotNull
    @Override
    public InternalNetworkCluster getCluster() {
        return this.cluster;
    }

    @Override
    public ProcessInformation prepareProcess(@NotNull ProcessConfiguration configuration, boolean start) {
        synchronized (this) {
            if (start) {
                ProcessInformation maybe = findMayMatchingProcess(configuration, this.getPreparedProcesses(configuration.getBase().getName()));
                if (maybe != null) {
                    System.out.println(LanguageManager.get(
                            "process-start-already-prepared-process",
                            configuration.getBase().getName(),
                            maybe.getProcessDetail().getName()
                    ));

                    this.startProcess(maybe);
                    return maybe;
                }
            }

            return this.startProcessInternal(configuration, true, start);
        }
    }

    @NotNull
    @Override
    public synchronized ProcessInformation startProcess(@NotNull ProcessInformation processInformation) {
        synchronized (this) {
            if (getCluster().isSelfNodeHead()) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(
                        e -> e.sendPacket(new NodePacketOutStartPreparedProcess(processInformation))
                ).ifEmpty(e -> {
                    if (processInformation.getProcessDetail().getParentUniqueID().equals(cluster.getSelfNode().getNodeUniqueID())
                            && processInformation.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)) {
                        LocalProcessManager.getNodeProcesses()
                                .stream()
                                .filter(p -> p.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()))
                                .findFirst()
                                .ifPresent(LocalProcessQueue::queue);
                    }
                });

                processInformation.getProcessDetail().setProcessState(ProcessState.READY_TO_START);
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);
            } else {
                DefaultChannelManager.INSTANCE.get(this.cluster.getHeadNode().getName()).ifPresent(
                        e -> e.sendPacket(new NodePacketOutToHeadStartPreparedProcess(processInformation))
                );
            }

            return processInformation;
        }
    }

    @Nullable
    private ProcessInformation startProcessInternal(@NotNull ProcessConfiguration configuration, boolean informUser, boolean start) {
        Template template = configuration.getTemplate();
        if (template == null) {
            if (configuration.getBase().getTemplates().isEmpty()) {
                template = new Template(
                        0,
                        "default",
                        false,
                        FileBackend.NAME,
                        "#",
                        new RuntimeConfiguration(
                                512, new ArrayList<>(), new HashMap<>()
                        ), Version.PAPER_1_8_8);
                configuration.getBase().getTemplates().add(template);
                ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateProcessGroup(configuration.getBase());

                System.err.println("Starting up process " + configuration.getBase().getName()
                        + " with default template because no template is set up");
            } else {
                for (Template groupTemplate : configuration.getBase().getTemplates()) {
                    if (template == null) {
                        template = groupTemplate;
                    } else if (template.getPriority() < groupTemplate.getPriority()) {
                        template = groupTemplate;
                    }
                }
            }
        }

        Conditions.nonNull(template, "Unable to find any template to start the process with");

        if (getCluster().isSelfNodeHead()) {
            PER_GROUP_WAITING.add(configuration.getBase().getName(), configuration.getUniqueId());

            if (getCluster().noOtherNodes()) {
                if (configuration.getBase().getStartupConfiguration().isSearchBestClientAlone()
                        || configuration.getBase().getStartupConfiguration().getUseOnlyTheseClients()
                        .contains(NodeExecutor.getInstance().getNodeConfig().getName())) {
                    return this.localNodeProcessManager.prepareLocalProcess(configuration, template, start);
                }

                LATER.add(new Duo<>(configuration, start));
                return null;
            }

            int maxMemory = configuration.getMaxMemory() == null
                    ? template.getRuntimeConfiguration().getMaxMemory()
                    : configuration.getMaxMemory();

            NodeInformation best = this.getCluster().findBestNodeForStartup(configuration.getBase(), maxMemory);
            if (best != null && best.canEqual(this.getCluster().getSelfNode())) {
                return this.localNodeProcessManager.prepareLocalProcess(configuration, template, start);
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
            return this.localNodeProcessManager.queueProcess(configuration, template, best, start);
        }

        return this.getCluster().sendQueryToHead(
                new NodePacketOutQueryStartProcess(configuration, start),
                packet -> packet.content().get("result", ProcessInformation.TYPE)
        );
    }

    @Override
    public void stopProcess(@NotNull String name) {
        ProcessInformation information = this.localNodeProcessManager.getClusterProcess(name);
        if (information == null) {
            return;
        }

        this.stopProcess(information.getProcessDetail().getProcessUniqueID());
    }

    @Override
    public void stopProcess(@NotNull UUID uuid) {
        if (localNodeProcessManager.isLocal(uuid)) {
            this.localNodeProcessManager.stopLocalProcess(uuid);
            return;
        }

        ProcessInformation information = this.localNodeProcessManager.getClusterProcess(uuid);
        if (information == null) {
            return;
        }

        DefaultChannelManager.INSTANCE.get(information.getProcessDetail().getParentName()).ifPresent(
                e -> e.sendPacket(new NodePacketOutStopProcess(uuid))
        );
    }

    @NotNull
    @Override
    public Collection<Duo<ProcessConfiguration, Boolean>> getWaitingProcesses() {
        return Collections.unmodifiableCollection(LATER);
    }

    @NotNull
    @Override
    public BiMap<String, UUID> getRegisteredProcesses() {
        return PER_GROUP_WAITING;
    }

    @Override
    public void close() {
        LATER.clear();
    }

    @NotNull
    private List<ProcessInformation> getPreparedProcesses(@NotNull String group) {
        return Streams.list(
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(group),
                e -> e.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)
        );
    }
}
