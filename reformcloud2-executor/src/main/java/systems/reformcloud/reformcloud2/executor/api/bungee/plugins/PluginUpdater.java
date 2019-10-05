package systems.reformcloud.reformcloud2.executor.api.bungee.plugins;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public final class PluginUpdater extends AbsoluteThread {

    public PluginUpdater() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Collection<Plugin> plugins = ProxyServer.getInstance().getPluginManager().getPlugins();
            if (plugins.size() != ExecutorAPI.getInstance().getThisProcessInformation().getPlugins().size()) {
                ExecutorAPI.getInstance().getThisProcessInformation().updateRuntimeInformation();
                ExecutorAPI.getInstance().getThisProcessInformation().getPlugins().clear();
                plugins.forEach(plugin -> ExecutorAPI.getInstance().getThisProcessInformation().getPlugins().add(new DefaultPlugin(
                        plugin.getDescription().getVersion(),
                        plugin.getDescription().getAuthor(),
                        plugin.getDescription().getMain(),
                        new ArrayList<>(plugin.getDescription().getDepends()),
                        new ArrayList<>(plugin.getDescription().getSoftDepends()),
                        true,
                        plugin.getDescription().getName()
                )));
                ExecutorAPI.getInstance().update(ExecutorAPI.getInstance().getThisProcessInformation());
            }

            AbsoluteThread.sleep(TimeUnit.SECONDS, 5);
        }
    }
}
