package systems.reformcloud.reformcloud2.signs.util.sign;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.UUID;

public class CloudSign implements SerializableObject {

    public CloudSign() {
    }

    public CloudSign(String group, CloudLocation location) {
        this.group = group;
        this.location = location;
        this.uniqueID = UUID.randomUUID();
        this.currentTarget = null;
    }

    private String group;

    private CloudLocation location;

    private UUID uniqueID;

    private ProcessInformation currentTarget;

    public String getGroup() {
        return group;
    }

    public CloudLocation getLocation() {
        return location;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public ProcessInformation getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(ProcessInformation currentTarget) {
        this.currentTarget = currentTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloudSign)) return false;
        CloudSign sign = (CloudSign) o;
        return sign.getUniqueID().equals(this.getUniqueID());
    }

    @Override
    public int hashCode() {
        return this.getUniqueID().hashCode();
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.group);
        buffer.writeObject(this.location);
        buffer.writeUniqueId(this.uniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.group = buffer.readString();
        this.location = buffer.readObject(CloudLocation.class);
        this.uniqueID = buffer.readUniqueId();
    }
}
