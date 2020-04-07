package systems.reformcloud.reformcloud2.executor.api.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

public abstract class Node extends ExecutorAPI implements ReloadableRuntime {

    @NotNull
    public static Node getInstance() {
        return (Node) ExecutorAPI.getInstance();
    }

    protected abstract void bootstrap();

    public abstract void shutdown() throws Exception;

    @NotNull
    public abstract NetworkServer getNetworkServer();

    @NotNull
    public abstract CommandManager getCommandManager();
}
