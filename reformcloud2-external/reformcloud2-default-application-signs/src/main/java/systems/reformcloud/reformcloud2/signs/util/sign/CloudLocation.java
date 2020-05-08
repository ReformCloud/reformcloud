package systems.reformcloud.reformcloud2.signs.util.sign;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.Objects;

public class CloudLocation implements SerializableObject {

    public CloudLocation() {
    }

    public CloudLocation(String world, String group, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.group = group;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    private String world;

    private String group;

    private double x;

    private double y;

    private double z;

    private float yaw;

    private float pitch;

    public String getWorld() {
        return world;
    }

    public String getGroup() {
        return group;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloudLocation)) return false;
        CloudLocation that = (CloudLocation) o;
        return Double.compare(that.getX(), getX()) == 0 &&
                Double.compare(that.getY(), getY()) == 0 &&
                Double.compare(that.getZ(), getZ()) == 0 &&
                Float.compare(that.getYaw(), getYaw()) == 0 &&
                Float.compare(that.getPitch(), getPitch()) == 0 &&
                Objects.equals(getWorld(), that.getWorld()) &&
                Objects.equals(getGroup(), that.getGroup());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.world);
        buffer.writeString(this.group);
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
        buffer.writeFloat(this.yaw);
        buffer.writeFloat(this.pitch);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.world = buffer.readString();
        this.group = buffer.readString();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.yaw = buffer.readFloat();
        this.pitch = buffer.readFloat();
    }
}
