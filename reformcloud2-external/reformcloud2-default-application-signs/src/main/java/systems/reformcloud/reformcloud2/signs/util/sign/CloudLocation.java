package systems.reformcloud.reformcloud2.signs.util.sign;

import java.util.Objects;

public class CloudLocation {

    public CloudLocation(String world, String group, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.group = group;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    private final String world;

    private final String group;

    private final double x;

    private final double y;

    private final double z;

    private final float yaw;

    private final float pitch;

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
}
