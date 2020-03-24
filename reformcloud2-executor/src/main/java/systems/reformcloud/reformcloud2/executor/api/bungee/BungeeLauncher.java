package systems.reformcloud.reformcloud2.executor.api.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;

public final class BungeeLauncher extends Plugin {

    @Override
    public void onLoad() {
        LanguageWorker.doLoad();
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

        ProxyServer.getInstance().getPlayers().forEach(e -> e.disconnect(TextComponent.fromLegacyText(
                BungeeExecutor.getInstance().getMessages().getCurrentProcessClosed()
        )));
    }
}
