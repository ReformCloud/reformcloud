package systems.reformcloud.reformcloud2.signs.util.sign.config;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.Collection;

public class SignConfig implements Serializable, Cloneable {

    public static final TypeToken<SignConfig> TYPE = new TypeToken<SignConfig>() {
    };

    public SignConfig(long updateIntervalInSeconds, Collection<SignLayout> layouts, boolean knockBackEnabled, String knockBackBypassPermission, double knockBackDistance, double knockBackStrength) {
        this.updateIntervalInSeconds = updateIntervalInSeconds;
        this.layouts = layouts;
        this.knockBackEnabled = knockBackEnabled;
        this.knockBackBypassPermission = knockBackBypassPermission;
        this.knockBackDistance = knockBackDistance;
        this.knockBackStrength = knockBackStrength;
    }

    private final long updateIntervalInSeconds;

    private final Collection<SignLayout> layouts;

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
}
