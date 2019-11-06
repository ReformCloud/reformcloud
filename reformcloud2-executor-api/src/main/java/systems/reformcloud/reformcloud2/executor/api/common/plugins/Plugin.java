package systems.reformcloud.reformcloud2.executor.api.common.plugins;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class Plugin implements Nameable {

    public static final TypeToken<DefaultPlugin> TYPE = new TypeToken<DefaultPlugin>() {};

    /**
     * @return The version of the plugin
     */
    @Nonnull
    public abstract String version();

    /**
     * @return The author of the plugin
     */
    @Nonnull
    public abstract String author();

    /**
     * @return The main class of the plugin
     */
    @Nonnull
    public abstract String main();

    /**
     * @return The depends of the plugin
     */
    @Nullable
    public abstract List<String> depends();

    /**
     * @return The softpends of the plugin
     */
    @Nullable
    public abstract List<String> softpends();

    /**
     * @return If the plugin is enabled
     */
    public abstract boolean enabled();
}
