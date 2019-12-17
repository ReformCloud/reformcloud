package systems.reformcloud.reforncloud2.notifications.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reforncloud2.notifications.velocity.listener.ProcessListener;

@Plugin(
    id = "reformcloud_2_notifications", name = "ReformCloud2Notifications",
    version = "2.0",
    description =
        "Publishes notifications to all players with a permission on server start/connect/stop",
    url = "https://reformcloud.systems", authors = {"derklaro"},
    dependencies = { @Dependency(id = "reformcloud_2_api_executor") })
public class VelocityPlugin {

  @Inject
  public VelocityPlugin(ProxyServer server) {
    this.listener = new ProcessListener(server);
    ExecutorAPI.getInstance().getEventManager().registerListener(this.listener);

    proxyServer = server;
  }

  private final ProcessListener listener;

  public static ProxyServer proxyServer;

  @Subscribe
  public void handle(final ProxyShutdownEvent event) {
    ExecutorAPI.getInstance().getEventManager().unregisterListener(
        this.listener);
  }
}
