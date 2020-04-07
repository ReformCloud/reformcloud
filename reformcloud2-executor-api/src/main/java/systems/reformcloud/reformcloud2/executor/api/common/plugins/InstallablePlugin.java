package systems.reformcloud.reformcloud2.executor.api.common.plugins;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;

/**
 * This class represents any plugin which is installable on a process instance
 *
 * @see systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginSyncAPI#installPlugin(String, InstallablePlugin)
 * @see systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI#installPluginAsync(String, InstallablePlugin)
 */
public abstract class InstallablePlugin extends Plugin {

    public static final TypeToken<DefaultInstallablePlugin> INSTALLABLE_TYPE = new TypeToken<DefaultInstallablePlugin>() {
    };

    /**
     * @return The download url of the plugin
     */
    @NotNull
    public abstract String getDownloadURL();
}
