package systems.reformcloud.reformcloud2.signs.util.sign.config;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.Collection;

public class SignConfig implements Serializable, Cloneable {

    public static final TypeToken<SignConfig> TYPE = new TypeToken<SignConfig>() {};

    public SignConfig(Collection<SignLayout> layouts, long animationsPerSecond) {
        this.layouts = layouts;
        this.updateIntervalInSeconds = animationsPerSecond;
    }

    private final long updateIntervalInSeconds;

    private final Collection<SignLayout> layouts;

    public Collection<SignLayout> getLayouts() {
        return layouts;
    }

    public long getUpdateInterval() {
        return updateIntervalInSeconds > 0 ? updateIntervalInSeconds : 1;
    }
}
