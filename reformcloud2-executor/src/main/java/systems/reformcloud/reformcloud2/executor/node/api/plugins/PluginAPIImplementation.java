package systems.reformcloud.reformcloud2.executor.node.api.plugins;

import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.plugins.PluginSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.api.NodePluginAction;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public class PluginAPIImplementation implements PluginSyncAPI, PluginAsyncAPI {

    public PluginAPIImplementation(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

    private final NodeNetworkManager nodeNetworkManager;

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
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

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(process.getParent())) {
                DefaultChannelManager.INSTANCE.get(process.getName()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.INSTALL, process.getName(), new DefaultInstallablePlugin(
                        plugin.getDownloadURL(),
                        plugin.getName(),
                        plugin.version(),
                        plugin.author(),
                        plugin.main()
                ))));
            } else {
                DefaultChannelManager.INSTANCE.get(process.getParent()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.INSTALL, process.getName(), new DefaultInstallablePlugin(
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

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull String process, @Nonnull Plugin plugin) {
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

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(process.getParent())) {
                DefaultChannelManager.INSTANCE.get(process.getName()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.UNINSTALL,
                        process.getName(),
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
                DefaultChannelManager.INSTANCE.get(process.getParent()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.UNINSTALL,
                        process.getName(),
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

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull String process, @Nonnull String name) {
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

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull ProcessInformation process, @Nonnull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.filterToReference(process.getPlugins(), e -> e.getName().equals(name)).orNothing()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process, @Nonnull String author) {
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

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation process, @Nonnull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.allOf(process.getPlugins(), e -> e.author() != null && e.author().equals(author))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process) {
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

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation processInformation) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Collections.unmodifiableList(processInformation.getPlugins())));
        return task;
    }

    @Override
    public void installPlugin(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void installPlugin(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@Nonnull String process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull String process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull ProcessInformation process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process) {
        return getPluginsAsync(process).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation).getUninterruptedly();
    }
}
