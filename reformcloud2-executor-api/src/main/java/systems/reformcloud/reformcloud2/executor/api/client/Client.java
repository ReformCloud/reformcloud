package systems.reformcloud.reformcloud2.executor.api.client;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

public abstract class Client extends ExternalAPIImplementation implements ReloadableRuntime {

    public static Client getInstance() {
        return (Client) ExecutorAPI.getInstance();
    }

    protected abstract void bootstrap();

    public abstract void shutdown();

    public abstract CommandManager getCommandManager();

    public abstract NetworkClient getNetworkClient();
}
