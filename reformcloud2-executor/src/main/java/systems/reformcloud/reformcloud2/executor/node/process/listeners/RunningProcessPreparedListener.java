package systems.reformcloud.reformcloud2.executor.node.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPrepareEvent;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutProcessPrepared;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

public class RunningProcessPreparedListener {

    @Listener
    public void handle(final RunningProcessPrepareEvent event) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new NodePacketOutProcessPrepared(
                event.getRunningProcess().getProcessInformation().getName(),
                event.getRunningProcess().getProcessInformation().getProcessUniqueID(),
                event.getRunningProcess().getProcessInformation().getTemplate().getName()
        ));
        LocalProcessManager.registerLocalProcess(event.getRunningProcess());
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().registerLocalProcess(event.getRunningProcess());
    }
}
