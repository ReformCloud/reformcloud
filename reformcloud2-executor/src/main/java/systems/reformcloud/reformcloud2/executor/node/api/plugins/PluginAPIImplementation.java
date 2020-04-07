package systems.reformcloud.reformcloud2.executor.node.api.plugins;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.api.NodePluginAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PluginAPIImplementation implements PluginSyncAPI, PluginAsyncAPI {

    public PluginAPIImplementation(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

    private final NodeNetworkManager nodeNetworkManager;

    @NotNull
    @Override
    public Task<Void> installPluginAsync(@NotNull String process, @NotNull InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(installPluginAsync(information, plugin).getUninterruptedly());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> installPluginAsync(@NotNull ProcessInformation process, @NotNull InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(process.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(process.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.INSTALL, process.getProcessDetail().getName(), new DefaultInstallablePlugin(
                        plugin.getDownloadURL(),
                        plugin.getName(),
                        plugin.version(),
                        plugin.author(),
                        plugin.main()
                ))));
            } else {
                DefaultChannelManager.INSTANCE.get(process.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.INSTALL, process.getProcessDetail().getName(), new DefaultInstallablePlugin(
                        plugin.getDownloadURL(),
                        plugin.getName(),
                        plugin.version(),
                        plugin.author(),
                        plugin.main()
                ))));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> unloadPluginAsync(@NotNull String process, @NotNull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(unloadPluginAsync(information, plugin).getUninterruptedly());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> unloadPluginAsync(@NotNull ProcessInformation process, @NotNull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(process.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(process.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.UNINSTALL,
                        process.getProcessDetail().getName(),
                        new DefaultPlugin(
                                plugin.version(),
                                plugin.author(),
                                plugin.main(),
                                plugin.depends(),
                                plugin.softpends(),
                                plugin.enabled(),
                                plugin.getName()
                        ))));
            } else {
                DefaultChannelManager.INSTANCE.get(process.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.UNINSTALL,
                        process.getProcessDetail().getName(),
                        new DefaultPlugin(
                                plugin.version(),
                                plugin.author(),
                                plugin.main(),
                                plugin.depends(),
                                plugin.softpends(),
                                plugin.enabled(),
                                plugin.getName()
                        ))));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@NotNull String process, @NotNull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(getInstalledPluginAsync(information, name).getUninterruptedly());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@NotNull ProcessInformation process, @NotNull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.filterToReference(process.getPlugins(), e -> e.getName().equals(name)).orNothing()));
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull String process, @NotNull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(getPluginsAsync(information, author).getUninterruptedly());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull ProcessInformation process, @NotNull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.allOf(process.getPlugins(), e -> e.author() != null && e.author().equals(author))));
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull String process) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(getPluginsAsync(information).getUninterruptedly());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull ProcessInformation processInformation) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Collections.unmodifiableList(processInformation.getPlugins())));
        return task;
    }

    @Override
    public void installPlugin(@NotNull String process, @NotNull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void installPlugin(@NotNull ProcessInformation process, @NotNull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@NotNull String process, @NotNull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@NotNull ProcessInformation process, @NotNull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@NotNull String process, @NotNull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@NotNull ProcessInformation process, @NotNull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @NotNull
    @Override
    public Collection<DefaultPlugin> getPlugins(@NotNull String process, @NotNull String author) {
        Collection<DefaultPlugin> result = getPluginsAsync(process, author).getUninterruptedly(TimeUnit.SECONDS, 5);
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Collection<DefaultPlugin> getPlugins(@NotNull ProcessInformation process, @NotNull String author) {
        Collection<DefaultPlugin> result = getPluginsAsync(process, author).getUninterruptedly(TimeUnit.SECONDS, 5);
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Collection<DefaultPlugin> getPlugins(@NotNull String process) {
        Collection<DefaultPlugin> result = getPluginsAsync(process).getUninterruptedly(TimeUnit.SECONDS, 5);
        return result == null ? new ArrayList<>() : result;
    }

    @NotNull
    @Override
    public Collection<DefaultPlugin> getPlugins(@NotNull ProcessInformation processInformation) {
        Collection<DefaultPlugin> result = getPluginsAsync(processInformation).getUninterruptedly(TimeUnit.SECONDS, 5);
        return result == null ? new ArrayList<>() : result;
    }
}
