package de.klaro.reformcloud2.executor.api.nukkit.event;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.plugin.Plugin;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.nukkit.NukkitExecutor;

import java.util.function.Predicate;

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
        ExecutorAPI.getInstance().getThisProcessInformation().getPlugins().add(defaultPlugin);
        ExecutorAPI.getInstance().getThisProcessInformation().updateRuntimeInformation();
        ExecutorAPI.getInstance().update(ExecutorAPI.getInstance().getThisProcessInformation());
    }

    @EventHandler
    public void handle(final PluginDisableEvent event) {
        final ProcessInformation processInformation = ExecutorAPI.getInstance().getThisProcessInformation();
        Plugin plugin = event.getPlugin();
        DefaultPlugin defaultPlugin = Links.filter(processInformation.getPlugins(), new Predicate<DefaultPlugin>() {
            @Override
            public boolean test(DefaultPlugin in) {
                return in.getName().equals(plugin.getName());
            }
        });
        if (defaultPlugin != null) {
            processInformation.getPlugins().remove(defaultPlugin);
            processInformation.updateRuntimeInformation();
            NukkitExecutor.getInstance().setThisProcessInformation(processInformation);
            ExecutorAPI.getInstance().update(processInformation);
        }
    }
}
