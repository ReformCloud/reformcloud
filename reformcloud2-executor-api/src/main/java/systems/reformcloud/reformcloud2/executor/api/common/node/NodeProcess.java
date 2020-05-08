package systems.reformcloud.reformcloud2.executor.api.common.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.UUID;

public class NodeProcess implements SerializableObject {

    private String group;

    private String name;

    private UUID uniqueID;

    public NodeProcess(String group, String name, UUID uniqueID) {
        this.group = group;
        this.name = name;
        this.uniqueID = uniqueID;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.group);
        buffer.writeString(this.name);
        buffer.writeUniqueId(this.uniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.group = buffer.readString();
        this.name = buffer.readString();
        this.uniqueID = buffer.readUniqueId();
    }
}
