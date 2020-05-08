package systems.reformcloud.reformcloud2.executor.api.common.client.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.UUID;

public final class DefaultClientRuntimeInformation implements ClientRuntimeInformation, SerializableObject {

    public DefaultClientRuntimeInformation() {
    }

    public DefaultClientRuntimeInformation(String startHost, int maxMemory, int maxProcesses, String name, UUID uniqueId) {
        this.startHost = startHost;
        this.maxMemory = maxMemory;
        this.maxProcesses = maxProcesses;
        this.name = name;
        this.uniqueID = uniqueId;
    }

    private String startHost;

    private int maxMemory;

    private int maxProcesses;

    private String name;

    private UUID uniqueID;

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

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.startHost);
        buffer.writeInt(this.maxMemory);
        buffer.writeInt(this.maxProcesses);
        buffer.writeString(this.name);
        buffer.writeUniqueId(this.uniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.startHost = buffer.readString();
        this.maxMemory = buffer.readInt();
        this.maxProcesses = buffer.readInt();
        this.name = buffer.readString();
        this.uniqueID = buffer.readUniqueId();
    }
}
