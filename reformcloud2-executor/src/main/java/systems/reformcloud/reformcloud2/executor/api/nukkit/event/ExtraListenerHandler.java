package systems.reformcloud.reformcloud2.executor.api.nukkit.event;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.plugin.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.nukkit.NukkitExecutor;

public final class ExtraListenerHandler implements Listener {

    @EventHandler
    public void handle(final PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        DefaultPlugin defaultPlugin = new DefaultPlugin(
                plugin.getDescription().getVersion(),
                plugin.getDescription().getAuthors().get(0),
                plugin.getDescription().getMain(),
                plugin.getDescription().getDepend(),
                plugin.getDescription().getSoftDepend(),
                plugin.isEnabled(),
                plugin.getName()
        );
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation().getPlugins().add(defaultPlugin);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation().updateRuntimeInformation();
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation());
    }

    @EventHandler
    public void handle(final PluginDisableEvent event) {
        final ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        Plugin plugin = event.getPlugin();
        DefaultPlugin defaultPlugin = Streams.filter(processInformation.getPlugins(), in -> in.getName().equals(plugin.getName()));
        if (defaultPlugin != null) {
            processInformation.getPlugins().remove(defaultPlugin);
            processInformation.updateRuntimeInformation();
            NukkitExecutor.getInstance().setThisProcessInformation(processInformation);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);
        }
    }
}
