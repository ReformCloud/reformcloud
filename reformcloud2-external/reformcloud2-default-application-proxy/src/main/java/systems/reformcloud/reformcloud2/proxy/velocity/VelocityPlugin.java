package systems.reformcloud.reformcloud2.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import systems.reformcloud.reformcloud2.proxy.plugin.PluginConfigHandler;
import systems.reformcloud.reformcloud2.proxy.velocity.listener.VelocityListener;

@Plugin(
        id = "reformcloud_2_proxy",
        name = "ReformCloud2Proxy",
        version = "2.0",
        description = "The proxy plugin",
        url = "https://reformcloud.systems",
        authors = {"derklaro"},
        dependencies = {@Dependency(id = "reformcloud_2_api_executor")}
)
public class VelocityPlugin {

    @Inject
    public VelocityPlugin(ProxyServer server) {
        proxyServer = server;
        PluginConfigHandler.request(() -> server.getEventManager().register(this, new VelocityListener()));
    }

    public static ProxyServer proxyServer;
}
