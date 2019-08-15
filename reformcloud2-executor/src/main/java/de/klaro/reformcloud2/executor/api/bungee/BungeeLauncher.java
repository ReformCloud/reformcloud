package de.klaro.reformcloud2.executor.api.bungee;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeLauncher extends Plugin {

    @Override
    public void onLoad() {
        DependencyLoader.doLoad();
    }
}
