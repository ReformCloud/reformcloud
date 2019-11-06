package systems.reformcloud.reformcloud2.executor.api.common.plugins;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;

import javax.annotation.Nonnull;

public abstract class InstallablePlugin extends Plugin {

    public static final TypeToken<DefaultInstallablePlugin> INSTALLABLE_TYPE = new TypeToken<DefaultInstallablePlugin>() {};

    /**
     * @return The download url of the plugin
     */
    @Nonnull
    public abstract String getDownloadURL();
}
