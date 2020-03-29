package systems.reformcloud.reformcloud2.executor.controller.api.plugins;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
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
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.api.ControllerPluginAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class PluginAPIImplementation implements PluginSyncAPI, PluginAsyncAPI {

    @NotNull
    @Override
    public Task<Void> installPluginAsync(@NotNull String process, @NotNull InstallablePlugin plugin) {
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

    @NotNull
    @Override
    public Task<Void> installPluginAsync(@NotNull ProcessInformation process, @NotNull InstallablePlugin plugin) {
        return installPluginAsync(process.getName(), plugin);
    }

    @NotNull
    @Override
    public Task<Void> unloadPluginAsync(@NotNull String process, @NotNull Plugin plugin) {
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

    @NotNull
    @Override
    public Task<Void> unloadPluginAsync(@NotNull ProcessInformation process, @NotNull Plugin plugin) {
        return unloadPluginAsync(process.getName(), plugin);
    }

    @NotNull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@NotNull String process, @NotNull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(process);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            task.complete(Streams.filter(processInformation.getPlugins(), defaultPlugin -> defaultPlugin.getName().equals(name)));
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@NotNull ProcessInformation process, @NotNull String name) {
        return getInstalledPluginAsync(process.getName(), name);
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull String process, @NotNull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(process);
            if (processInformation == null) {
                task.complete(new ArrayList<>());
                return;
            }

            task.complete(Streams.allOf(processInformation.getPlugins(), defaultPlugin -> defaultPlugin.author() != null && defaultPlugin.author().equals(author)));
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull ProcessInformation process, @NotNull String author) {
        return getPluginsAsync(process.getName(), author);
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull String process) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(process);
            if (processInformation == null) {
                task.complete(new ArrayList<>());
                return;
            }

            task.complete(processInformation.getPlugins());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@NotNull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation.getName());
    }

    @Override
    public void installPlugin(@NotNull String process, @NotNull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void installPlugin(@NotNull ProcessInformation process, @NotNull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void unloadPlugin(@NotNull String process, @NotNull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void unloadPlugin(@NotNull ProcessInformation process, @NotNull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public Plugin getInstalledPlugin(@NotNull String process, @NotNull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public Plugin getInstalledPlugin(@NotNull ProcessInformation process, @NotNull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly(TimeUnit.SECONDS, 5);
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
