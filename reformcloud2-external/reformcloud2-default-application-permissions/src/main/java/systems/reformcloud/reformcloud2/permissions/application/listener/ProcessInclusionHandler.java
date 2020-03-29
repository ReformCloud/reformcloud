package systems.reformcloud.reformcloud2.permissions.application.listener;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.common.process.event.ProcessInformationConfigureEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPrepareEvent;
import systems.reformcloud.reformcloud2.permissions.application.ReformCloudApplication;

public final class ProcessInclusionHandler {

    @Listener
    public void handle(final ProcessInformationConfigureEvent event) {
        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)) {
            this.includeSelfFile(event.getInformation());
        }
    }

    @Listener
    public void handle(final RunningProcessPrepareEvent event) {
        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            this.includeSelfFile(event.getRunningProcess().getProcessInformation());
        }
    }

    private void includeSelfFile(@NotNull ProcessInformation processInformation) {
        processInformation.getPreInclusions().add(new ProcessInclusion(
                "https://dl.reformcloud.systems/addonsv2/reformcloud2-default-application-permissions-"
                        + ReformCloudApplication.getInstance().getApplication().applicationConfig().version() + ".jar",
                "plugins/permissions-" + ReformCloudApplication.getInstance().getApplication().applicationConfig().version() + ".jar"
        ));
    }
}
