package systems.reformcloud.reformcloud2.executor.controller.api.plugins;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
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
import systems.reformcloud.reformcloud2.executor.controller.packet.out.api.ControllerPluginAction;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PluginAPIImplementation implements PluginSyncAPI, PluginAsyncAPI {

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DefaultChannelManager.INSTANCE.get(process).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPluginAction(
                    ControllerPluginAction.Action.INSTALL,
                    new DefaultInstallablePlugin(
                            plugin.getDownloadURL(),
                            plugin.getName(),
                            plugin.version(),
                            plugin.author(),
                            plugin.main()
                    )
            )));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        return installPluginAsync(process.getName(), plugin);
    }

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull String process, @Nonnull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DefaultChannelManager.INSTANCE.get(process).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPluginAction(
                    ControllerPluginAction.Action.UNINSTALL,
                    new DefaultPlugin(
                            plugin.version(),
                            plugin.author(),
                            plugin.main(),
                            plugin.depends(),
                            plugin.softpends(),
                            plugin.enabled(),
                            plugin.getName()
                    )
            )));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        return unloadPluginAsync(process.getName(), plugin);
    }

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull String process, @Nonnull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(process);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            task.complete(Links.filter(processInformation.getPlugins(), defaultPlugin -> defaultPlugin.getName().equals(name)));
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull ProcessInformation process, @Nonnull String name) {
        return getInstalledPluginAsync(process.getName(), name);
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process, @Nonnull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(process);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            task.complete(Links.allOf(processInformation.getPlugins(), defaultPlugin -> defaultPlugin.author().equals(author)));
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation process, @Nonnull String author) {
        return getPluginsAsync(process.getName(), author);
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(process);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            task.complete(processInformation.getPlugins());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation.getName());
    }

    @Override
    public void installPlugin(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void installPlugin(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void unloadPlugin(@Nonnull String process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void unloadPlugin(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull String process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull ProcessInformation process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process) {
        return getPluginsAsync(process).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation).getUninterruptedly(TimeUnit.SECONDS, 5);
    }
}
