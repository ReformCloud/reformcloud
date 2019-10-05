package systems.reformcloud.reformcloud2.executor.api.common.plugins;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;

public abstract class InstallablePlugin extends Plugin {

    public static final TypeToken<DefaultInstallablePlugin> INSTALLABLE_TYPE = new TypeToken<DefaultInstallablePlugin>() {};

    public abstract String getDownloadURL();
}
