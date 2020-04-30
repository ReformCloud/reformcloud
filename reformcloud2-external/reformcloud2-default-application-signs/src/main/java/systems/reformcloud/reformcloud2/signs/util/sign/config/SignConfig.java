package systems.reformcloud.reformcloud2.signs.util.sign.config;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.Collection;

public class SignConfig implements SerializableObject, Cloneable {

    public static final TypeToken<SignConfig> TYPE = new TypeToken<SignConfig>() {
    };

    public SignConfig() {
    }

    public SignConfig(long updateIntervalInSeconds, Collection<SignLayout> layouts, boolean knockBackEnabled, String knockBackBypassPermission, double knockBackDistance, double knockBackStrength) {
        this.updateIntervalInSeconds = updateIntervalInSeconds;
        this.layouts = layouts;
        this.knockBackEnabled = knockBackEnabled;
        this.knockBackBypassPermission = knockBackBypassPermission;
        this.knockBackDistance = knockBackDistance;
        this.knockBackStrength = knockBackStrength;
    }

    private long updateIntervalInSeconds;

    private Collection<SignLayout> layouts;

    private boolean knockBackEnabled;

    private String knockBackBypassPermission;

    private double knockBackDistance;

    private double knockBackStrength;

    public Collection<SignLayout> getLayouts() {
        return layouts;
    }

    public long getUpdateInterval() {
        return updateIntervalInSeconds > 0 ? updateIntervalInSeconds : 1;
    }

    public boolean isKnockBackEnabled() {
        return knockBackEnabled;
    }

    public String getKnockBackBypassPermission() {
        return knockBackBypassPermission == null ? "reformcloud.knockback.bypass" : knockBackBypassPermission;
    }

    public double getKnockBackDistance() {
        return knockBackDistance;
    }

    public double getKnockBackStrength() {
        return knockBackStrength;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeLong(this.updateIntervalInSeconds);
        buffer.writeObjects(this.layouts);
        buffer.writeBoolean(this.knockBackEnabled);
        buffer.writeString(this.knockBackBypassPermission);
        buffer.writeDouble(this.knockBackDistance);
        buffer.writeDouble(this.knockBackStrength);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.updateIntervalInSeconds = buffer.readLong();
        this.layouts = buffer.readObjects(SignLayout.class);
        this.knockBackEnabled = buffer.readBoolean();
        this.knockBackBypassPermission = buffer.readString();
        this.knockBackDistance = buffer.readDouble();
        this.knockBackStrength = buffer.readDouble();
    }
}
