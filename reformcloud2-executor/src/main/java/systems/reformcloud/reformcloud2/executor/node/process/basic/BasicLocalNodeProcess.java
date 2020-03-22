package systems.reformcloud.reformcloud2.executor.node.process.basic;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.SharedRunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.env.EnvironmentBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import javax.annotation.Nonnull;

public class BasicLocalNodeProcess extends SharedRunningProcess {

    public BasicLocalNodeProcess(@Nonnull ProcessInformation processInformation) {
        super(processInformation);
    }

    @Override
    public void chooseStartupEnvironmentAndPrepare() {
        EnvironmentBuilder.constructEnvFor(this);
    }

    @Nonnull
    @Override
    public Duo<String, Integer> getAvailableConnectionHost() {
        return NodeExecutor.getInstance().getConnectHost();
    }

    @Nonnull
    @Override
    public String getConnectionKey() {
        return NodeExecutor.getInstance().getNodeExecutorConfig().getConnectionKey();
    }
}
