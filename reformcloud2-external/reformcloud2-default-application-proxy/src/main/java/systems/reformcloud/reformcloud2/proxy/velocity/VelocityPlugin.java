package systems.reformcloud.reformcloud2.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.proxy.network.PacketInConfigUpdate;
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
        PluginConfigHandler.request(() -> {
            VelocityListener listener = new VelocityListener();

            server.getEventManager().register(this, listener);
            ExecutorAPI.getInstance().getEventManager().registerListener(listener);

            ExecutorAPI.getInstance().getPacketHandler().registerHandler(new PacketInConfigUpdate());
        });
    }

    public static ProxyServer proxyServer;
}
