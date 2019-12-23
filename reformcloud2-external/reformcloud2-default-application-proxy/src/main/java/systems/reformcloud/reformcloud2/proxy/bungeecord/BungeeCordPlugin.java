package systems.reformcloud.reformcloud2.proxy.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.proxy.bungeecord.listener.BungeeCordListener;
import systems.reformcloud.reformcloud2.proxy.plugin.PluginConfigHandler;

public class BungeeCordPlugin extends Plugin {

    @Override
    public void onEnable() {
        PluginConfigHandler.request(() -> ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeCordListener()));
    }
}
