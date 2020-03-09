package systems.reformcloud.reformcloud2.executor.api.node;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

import javax.annotation.Nonnull;

public abstract class Node extends ExecutorAPI implements ReloadableRuntime {

    @Nonnull
    public static Node getInstance() {
        return (Node) ExecutorAPI.getInstance();
    }

    protected abstract void bootstrap();

    public abstract void shutdown() throws Exception;

    @Nonnull
    public abstract NetworkServer getNetworkServer();

    @Nonnull
    public abstract CommandManager getCommandManager();
}
