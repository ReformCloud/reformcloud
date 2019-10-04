package de.klaro.reformcloud2.executor.api.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import de.klaro.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import net.kyori.text.TextComponent;

@Plugin(
        id = "reformcloud_2_api_executor",
        name = "ReformCloud2VelocityExecutor",
        version = "2",
        description = "The reformcloud executor api",
        authors = {
                "derklaro",
                "ReformCloud-Team"
        },
        url = "https://reformcloud.systems"
)
public final class VelocityLauncher {

    @Inject
    public VelocityLauncher(ProxyServer proxyServer) {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();
        StringUtil.sendHeader();

        this.proxyServer = proxyServer;
    }

    private ProxyServer proxyServer;

    @Subscribe
    public void handleInit(ProxyInitializeEvent event) {
        new VelocityExecutor(this, proxyServer);
    }

    @Subscribe
    public void handleStop(ProxyShutdownEvent event) {
        VelocityExecutor.getInstance().getNetworkClient().disconnect();
        proxyServer.getAllPlayers().forEach(e -> e.disconnect(TextComponent.of(
                VelocityExecutor.getInstance().getMessages().getCurrentProcessClosed()
        )));
    }
}
