package systems.reformcloud.reformcloud2.executor.api.common.client.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;

import java.util.UUID;

public final class DefaultClientRuntimeInformation implements ClientRuntimeInformation {

    public DefaultClientRuntimeInformation(String startHost, int maxMemory, int maxProcesses, String name, UUID uniqueId) {
        this.startHost = startHost;
        this.maxMemory = maxMemory;
        this.maxProcesses = maxProcesses;
        this.name = name;
        this.uniqueID = uniqueId;
    }

    private final String startHost;

    private final int maxMemory;

    private final int maxProcesses;

    private final String name;

    private final UUID uniqueID;

    @NotNull
    @Override
    public String startHost() {
        return startHost;
    }

    @NotNull
    @Override
    public UUID uniqueID() {
        return uniqueID;
    }

    @Override
    public int maxMemory() {
        return maxMemory;
    }

    @Override
    public int maxProcessCount() {
        return maxProcesses;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }
}
