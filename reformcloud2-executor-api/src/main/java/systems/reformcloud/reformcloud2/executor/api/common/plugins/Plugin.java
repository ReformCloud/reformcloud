package systems.reformcloud.reformcloud2.executor.api.common.plugins;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;

/**
 * This class represents any loaded plugin on a process instance
 *
 * @see ProcessInformation#getPlugins()
 */
public abstract class Plugin implements Nameable {

    public static final TypeToken<DefaultPlugin> TYPE = new TypeToken<DefaultPlugin>() {
    };

    /**
     * @return The version of the plugin
     */
    @NotNull
    public abstract String version();

    /**
     * @return The author of the plugin
     */
    @Nullable
    public abstract String author();

    /**
     * @return The main class of the plugin
     */
    @NotNull
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
