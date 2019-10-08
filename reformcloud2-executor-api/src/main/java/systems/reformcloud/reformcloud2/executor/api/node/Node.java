package systems.reformcloud.reformcloud2.executor.api.node;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;
import systems.reformcloud.reformcloud2.executor.api.controller.Controller;

public abstract class Node extends ExecutorAPI implements ReloadableRuntime {

    public static void setInstance(Node instance) {
        Conditions.isTrue(Node.instance == null);
        Node.instance = instance;
    }

    private static Node instance;

    protected abstract void bootstrap();

    public abstract void shutdown() throws Exception;

    public static Node getInstance() {
        return instance == null ? (Node) ExecutorAPI.getInstance() : instance;
    }

    public abstract NetworkServer getNetworkServer();

    public abstract CommandManager getCommandManager();
}
