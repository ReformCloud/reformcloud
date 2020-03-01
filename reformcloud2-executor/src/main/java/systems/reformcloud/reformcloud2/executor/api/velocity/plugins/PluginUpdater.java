package systems.reformcloud.reformcloud2.executor.api.velocity.plugins;

import com.velocitypowered.api.plugin.PluginContainer;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class PluginUpdater extends AbsoluteThread {

    public PluginUpdater() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Collection<PluginContainer> plugins = VelocityExecutor.getInstance().getProxyServer().getPluginManager().getPlugins();
            if (plugins.size() != API.getInstance().getCurrentProcessInformation().getPlugins().size()) {
                API.getInstance().getCurrentProcessInformation().updateRuntimeInformation();
                API.getInstance().getCurrentProcessInformation().getPlugins().clear();
                plugins.forEach(plugin -> {
                    List<String> depends = new ArrayList<>();
                    List<String> softDepends = new ArrayList<>();
                    plugin.getDescription().getDependencies().forEach(pluginDependency -> {
                        if (pluginDependency.isOptional()) {
                            softDepends.add(pluginDependency.getId());
                        } else {
                            depends.add(pluginDependency.getId());
                        }
                    });

                    API.getInstance().getCurrentProcessInformation().getPlugins().add(new DefaultPlugin(
                            plugin.getDescription().getVersion().isPresent() ? plugin.getDescription().getVersion().get() : "unknown",
                            plugin.getDescription().getAuthors().get(0),
                            null,
                            depends,
                            softDepends,
                            true,
                            plugin.getDescription().getId()
                    ));
                });
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(API.getInstance().getCurrentProcessInformation());
            }

            AbsoluteThread.sleep(TimeUnit.SECONDS, 5);
        }
    }
}
