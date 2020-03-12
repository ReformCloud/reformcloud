package systems.reformcloud.reformcloud2.executor.client.process.basic;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.SharedRunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.env.EnvironmentBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;

import javax.annotation.Nonnull;

public final class DefaultRunningProcess extends SharedRunningProcess {

    public DefaultRunningProcess(@Nonnull ProcessInformation processInformation) {
        super(processInformation);
    }

    @Override
    public void chooseStartupEnvironmentAndPrepare() {
        EnvironmentBuilder.constructEnvFor(this);
    }

    @Nonnull
    @Override
    public Duo<String, Integer> getAvailableConnectionHost() {
        return new Duo<>(
                ClientExecutor.getInstance().getClientExecutorConfig().getClientConnectionConfig().getHost(),
                ClientExecutor.getInstance().getClientExecutorConfig().getClientConnectionConfig().getPort()
        );
    }

    @Nonnull
    @Override
    public String getConnectionKey() {
        return ClientExecutor.getInstance().getClientExecutorConfig().getConnectionKey();
    }
}
