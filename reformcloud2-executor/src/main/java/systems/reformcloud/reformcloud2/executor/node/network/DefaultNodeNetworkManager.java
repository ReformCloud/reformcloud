package systems.reformcloud.reformcloud2.executor.node.network;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutStopProcess;
import systems.reformcloud.reformcloud2.executor.node.network.packet.query.NodePacketOutQueryGetProcess;
import systems.reformcloud.reformcloud2.executor.node.network.packet.query.NodePacketOutQueryStartProcess;

import java.util.UUID;

public class DefaultNodeNetworkManager implements NodeNetworkManager {

    public DefaultNodeNetworkManager(NodeProcessManager processManager, InternalNetworkCluster cluster) {
        this.localNodeProcessManager = processManager;
        this.cluster = cluster;
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
        ProcessInformation information = localNodeProcessManager.getLocalCloudProcess(name);
        if (information != null) {
            return information;
        }

        return getCluster().sendQueryToHead(new NodePacketOutQueryGetProcess(name), packet -> packet.content().get("result", ProcessInformation.TYPE));
    }

    @Override
    public ProcessInformation getCloudProcess(UUID uuid) {
        ProcessInformation information = localNodeProcessManager.getLocalCloudProcess(uuid);
        if (information != null) {
            return information;
        }

        return getCluster().sendQueryToHead(new NodePacketOutQueryGetProcess(uuid), packet -> packet.content().get("result", ProcessInformation.TYPE));
    }

    @Override
    public ProcessInformation startProcess(ProcessGroup processGroup, Template template, JsonConfiguration data) {
        if (getCluster().isSelfNodeHead()) {
            if (getCluster().noOtherNodes()) {
                return localNodeProcessManager.startLocalProcess(processGroup, template, data);
            }

            NodeInformation best = getCluster().findBestNodeForStartup(template);
            if (best != null && best.equals(getCluster().getHeadNode())) {
                return localNodeProcessManager.startLocalProcess(processGroup, template, data);
            }

            return localNodeProcessManager.queueProcess(processGroup, template, data, best);
        }

        return getCluster().sendQueryToHead(new NodePacketOutQueryStartProcess(processGroup, template, data),
                packet -> packet.content().get("result", ProcessInformation.TYPE
        ));
    }

    @Override
    public void stopProcess(String name) {
        if (localNodeProcessManager.isLocal(name)) {
            localNodeProcessManager.stopLocalProcess(name);
            return;
        }

        getCluster().publishToHeadNode(new NodePacketOutStopProcess(name));
    }

    @Override
    public void stopProcess(UUID uuid) {
        if (localNodeProcessManager.isLocal(uuid)) {
            localNodeProcessManager.stopLocalProcess(uuid);
            return;
        }

        getCluster().publishToHeadNode(new NodePacketOutStopProcess(uuid));
    }
}
