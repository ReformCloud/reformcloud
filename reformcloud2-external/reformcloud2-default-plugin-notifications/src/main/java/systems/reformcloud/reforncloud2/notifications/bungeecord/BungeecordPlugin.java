package systems.reformcloud.reforncloud2.notifications.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reforncloud2.notifications.bungeecord.listener.ProcessListener;

public final class BungeecordPlugin extends Plugin {

    private static final ProcessListener LISTENER = new ProcessListener();

    @Override
    public void onEnable() {
        ExecutorAPI.getInstance().getEventManager().registerListener(LISTENER);
    }

    @Override
    public void onDisable() {
        ExecutorAPI.getInstance().getEventManager().unregisterListener(LISTENER);
    }
}
