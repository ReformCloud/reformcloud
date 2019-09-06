package de.klaro.reformcloud2.executor.api.nukkit;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import de.klaro.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;

public final class NukkitLauncher extends PluginBase {

    @Override
    public void onLoad() {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();
        StringUtil.sendHeader();
    }

    @Override
    public void onEnable() {
        new NukkitExecutor(this);
    }

    @Override
    public void onDisable() {
        Server.getInstance().getScheduler().cancelTask(this);
        NukkitExecutor.getInstance().getNetworkClient().disconnect();
    }
}
