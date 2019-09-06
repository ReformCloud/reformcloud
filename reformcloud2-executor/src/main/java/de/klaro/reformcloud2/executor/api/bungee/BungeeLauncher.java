package de.klaro.reformcloud2.executor.api.bungee;

import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import de.klaro.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeLauncher extends Plugin {

    @Override
    public void onLoad() {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();
        StringUtil.sendHeader();
    }

    @Override
    public void onEnable() {
        new BungeeExecutor(this);
        BungeeExecutor.clearHandlers();
    }

    @Override
    public void onDisable() {
        BungeeExecutor.getInstance().getNetworkClient().disconnect();
        ProxyServer.getInstance().getScheduler().cancel(this);
    }
}
