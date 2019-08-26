package de.klaro.reformcloud2.executor.api.client;

import de.klaro.reformcloud2.executor.api.ExecutorType;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.network.client.NetworkClient;

public abstract class Client extends ExecutorAPI {

    public Client() {
        ExecutorAPI.setInstance(this);
        super.type = ExecutorType.CLIENT;
        bootstrap();
    }

    abstract void bootstrap();

    public static Client getInstance() {
        return (Client) ExecutorAPI.getInstance();
    }

    public abstract NetworkClient getNetworkClient();
}
