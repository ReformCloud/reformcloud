package systems.reformcloud.reformcloud2.executor.client.process.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.SharedRunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.env.EnvironmentBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.screen.ProcessScreen;

import java.util.ArrayList;
import java.util.Collection;

public final class DefaultRunningProcess extends SharedRunningProcess {

    public DefaultRunningProcess(@NotNull ProcessInformation processInformation) {
        super(processInformation);
    }

    @NotNull
    @Override
    public Collection<String> getCachedLogLines() {
        ProcessScreen screen = ClientExecutor.getInstance().getScreenManager().getPerProcessScreenLines().get(
                this.getProcessInformation().getProcessDetail().getProcessUniqueID()
        );
        if (screen != null) {
            return screen.getQueue();
        }

        return new ArrayList<>();
    }

    @Override
    public void chooseStartupEnvironmentAndPrepare() {
        EnvironmentBuilder.constructEnvFor(this);
    }

    @NotNull
    @Override
    public Duo<String, Integer> getAvailableConnectionHost() {
        return new Duo<>(
                ClientExecutor.getInstance().getClientExecutorConfig().getClientConnectionConfig().getHost(),
                ClientExecutor.getInstance().getClientExecutorConfig().getClientConnectionConfig().getPort()
        );
    }

    @NotNull
    @Override
    public String getConnectionKey() {
        return ClientExecutor.getInstance().getClientExecutorConfig().getConnectionKey();
    }
}
