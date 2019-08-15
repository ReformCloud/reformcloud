package de.klaro.reformcloud2.executor.api.nukkit;

import cn.nukkit.plugin.PluginBase;
import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;

public final class NukkitLauncher extends PluginBase {

    @Override
    public void onLoad() {
        DependencyLoader.doLoad();
    }
}
