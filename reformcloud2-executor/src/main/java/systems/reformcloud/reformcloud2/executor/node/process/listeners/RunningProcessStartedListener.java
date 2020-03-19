package systems.reformcloud.reformcloud2.executor.node.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreen;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreenHandler;

public class RunningProcessStartedListener {

    @Listener
    public void handle(final RunningProcessStartedEvent event) {
        NodeExecutor.getInstance().getClusterSyncManager().syncProcessStartup(event.getRunningProcess().getProcessInformation());
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleLocalProcessStart(event.getRunningProcess().getProcessInformation());
        NodeProcessScreenHandler.registerScreen(new NodeProcessScreen(event.getRunningProcess().getProcessInformation().getProcessUniqueID()));
        System.out.println(LanguageManager.get("client-process-start-done", event.getRunningProcess().getProcessInformation().getName()));
    }
}
