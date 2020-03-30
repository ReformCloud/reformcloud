package systems.reformcloud.reformcloud2.executor.node.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPreparedEvent;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutProcessPrepared;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

public class RunningProcessPreparedListener {

    @Listener
    public void handle(final RunningProcessPreparedEvent event) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new NodePacketOutProcessPrepared(
                event.getRunningProcess().getProcessInformation().getProcessDetail().getName(),
                event.getRunningProcess().getProcessInformation().getProcessDetail().getProcessUniqueID(),
                event.getRunningProcess().getProcessInformation().getProcessDetail().getTemplate().getName()
        ));
        LocalProcessManager.registerLocalProcess(event.getRunningProcess());
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().registerLocalProcess(event.getRunningProcess());
    }
}
