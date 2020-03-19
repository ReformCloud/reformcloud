package systems.reformcloud.reformcloud2.executor.node.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreenHandler;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

public class RunningProcessStoppedListener {

    @Listener
    public void handle(final RunningProcessStoppedEvent event) {
        NodeProcessScreenHandler.unregisterScreen(event.getRunningProcess().getProcessInformation().getProcessUniqueID());
        NodeExecutor.getInstance().getClusterSyncManager().syncProcessStop(event.getRunningProcess().getProcessInformation());
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().unregisterLocalProcess(event.getRunningProcess().getProcessInformation().getProcessUniqueID());
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleLocalProcessStop(event.getRunningProcess().getProcessInformation());
        LocalProcessManager.unregisterProcesses(event.getRunningProcess().getProcessInformation().getProcessUniqueID());
    }
}
