package systems.reformcloud.reformcloud2.executor.api.bungee.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

public final class PluginUpdater extends AbsoluteThread {

  public PluginUpdater() {
    enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      Collection<Plugin> plugins =
          ProxyServer.getInstance().getPluginManager().getPlugins();
      if (plugins.size() != ExecutorAPI.getInstance()
                                .getSyncAPI()
                                .getProcessSyncAPI()
                                .getThisProcessInformation()
                                .getPlugins()
                                .size()) {
        ExecutorAPI.getInstance()
            .getSyncAPI()
            .getProcessSyncAPI()
            .getThisProcessInformation()
            .updateRuntimeInformation();
        ExecutorAPI.getInstance()
            .getSyncAPI()
            .getProcessSyncAPI()
            .getThisProcessInformation()
            .getPlugins()
            .clear();
        plugins.forEach(
            plugin
            -> ExecutorAPI.getInstance()
                   .getSyncAPI()
                   .getProcessSyncAPI()
                   .getThisProcessInformation()
                   .getPlugins()
                   .add(new DefaultPlugin(
                       plugin.getDescription().getVersion(),
                       plugin.getDescription().getAuthor(),
                       plugin.getDescription().getMain(),
                       new ArrayList<>(plugin.getDescription().getDepends()),
                       new ArrayList<>(
                           plugin.getDescription().getSoftDepends()),
                       true, plugin.getDescription().getName())));
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(
            ExecutorAPI.getInstance()
                .getSyncAPI()
                .getProcessSyncAPI()
                .getThisProcessInformation());
      }

      AbsoluteThread.sleep(TimeUnit.SECONDS, 5);
    }
  }
}
