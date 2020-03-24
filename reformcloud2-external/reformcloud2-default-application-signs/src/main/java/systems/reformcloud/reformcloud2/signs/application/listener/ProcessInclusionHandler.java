package systems.reformcloud.reformcloud2.signs.application.listener;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.common.process.event.ProcessInformationConfigureEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPrepareEvent;
import systems.reformcloud.reformcloud2.signs.application.ReformCloudApplication;

import javax.annotation.Nonnull;

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

    private void includeSelfFile(@Nonnull ProcessInformation processInformation) {
        if (!processInformation.getTemplate().getVersion().isServer()
                || !processInformation.getProcessGroup().isCanBeUsedAsLobby()) {
            return;
        }

        processInformation.getPreInclusions().add(new ProcessInclusion(
                "https://dl.reformcloud.systems/addonsv2/reformcloud2-default-application-signs-"
                        + ReformCloudApplication.getInstance().getApplication().applicationConfig().version() + ".jar",
                "plugins/signs-" + ReformCloudApplication.getInstance().getApplication().applicationConfig().version() + ".jar"
        ));
    }
}
