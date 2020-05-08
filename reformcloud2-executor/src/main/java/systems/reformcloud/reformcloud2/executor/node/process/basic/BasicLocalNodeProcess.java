package systems.reformcloud.reformcloud2.executor.node.process.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.SharedRunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.env.EnvironmentBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreen;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreenHandler;

import java.util.ArrayList;
import java.util.Collection;

public class BasicLocalNodeProcess extends SharedRunningProcess {

    public BasicLocalNodeProcess(@NotNull ProcessInformation processInformation) {
        super(processInformation);
    }

    @NotNull
    @Override
    public Collection<String> getCachedLogLines() {
        NodeProcessScreen screen = NodeProcessScreenHandler.getScreen(
                this.getProcessInformation().getProcessDetail().getProcessUniqueID()
        ).orNothing();
        if (screen != null) {
            return screen.getCachedLines();
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
        return NodeExecutor.getInstance().getConnectHost();
    }

    @NotNull
    @Override
    public String getConnectionKey() {
        return NodeExecutor.getInstance().getNodeExecutorConfig().getConnectionKey();
    }
}
