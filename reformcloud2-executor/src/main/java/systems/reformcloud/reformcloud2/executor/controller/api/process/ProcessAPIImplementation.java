package systems.reformcloud.reformcloud2.executor.controller.api.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.controller.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.ControllerPacketOutCopyProcess;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.api.ControllerExecuteCommand;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProcessAPIImplementation implements ProcessSyncAPI, ProcessAsyncAPI {

    public ProcessAPIImplementation(ProcessManager processManager) {
        this.processManager = processManager;
    }

    private final ProcessManager processManager;

    @NotNull
    @Override
    public Task<ProcessInformation> startProcessAsync(@NotNull ProcessConfiguration configuration) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.startProcess(configuration)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> startProcessAsync(@NotNull ProcessInformation processInformation) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.startProcess(processInformation)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> prepareProcessAsync(@NotNull ProcessConfiguration configuration) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.prepareProcess(configuration)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@NotNull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.stopProcess(name)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@NotNull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.stopProcess(uniqueID)));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name) {
        return Task.supply(() -> {
            ProcessInformation information = this.getProcess(name);
            if (information == null) {
                return null;
            }

            return this.copyProcessAsync(
                    name,
                    information.getProcessDetail().getTemplate().getName(),
                    information.getProcessDetail().getTemplate().getBackend(),
                    information.getProcessGroup().getName()
            ).getUninterruptedly();
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId) {
        return Task.supply(() -> {
            ProcessInformation information = this.getProcess(processUniqueId);
            if (information == null) {
                return null;
            }

            return this.copyProcessAsync(
                    processUniqueId,
                    information.getProcessDetail().getTemplate().getName(),
                    information.getProcessDetail().getTemplate().getBackend(),
                    information.getProcessGroup().getName()
            ).getUninterruptedly();
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate) {
        return Task.supply(() -> {
            ProcessInformation information = this.getProcess(name);
            if (information == null) {
                return null;
            }

            return this.copyProcessAsync(
                    name,
                    targetTemplate,
                    information.getProcessDetail().getTemplate().getBackend(),
                    information.getProcessGroup().getName()
            ).getUninterruptedly();
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate) {
        return Task.supply(() -> {
            ProcessInformation information = this.getProcess(processUniqueId);
            if (information == null) {
                return null;
            }

            return this.copyProcessAsync(
                    processUniqueId,
                    targetTemplate,
                    information.getProcessDetail().getTemplate().getBackend(),
                    information.getProcessGroup().getName()
            ).getUninterruptedly();
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        return Task.supply(() -> {
            ProcessInformation information = this.getProcess(name);
            if (information == null) {
                return null;
            }

            return this.copyProcessAsync(
                    name,
                    targetTemplate,
                    targetTemplateStorage,
                    information.getProcessGroup().getName()
            ).getUninterruptedly();
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        return Task.supply(() -> {
            ProcessInformation information = this.getProcess(processUniqueId);
            if (information == null) {
                return null;
            }

            return this.copyProcessAsync(
                    processUniqueId,
                    targetTemplate,
                    targetTemplateStorage,
                    information.getProcessGroup().getName()
            ).getUninterruptedly();
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        return Task.supply(() -> {
            ProcessInformation information = this.getProcess(name);
            if (information == null) {
                return null;
            }

            this.copyProcess0(information, targetTemplate, targetTemplateStorage, targetTemplateGroup);
            return null;
        });
    }

    @NotNull
    @Override
    public Task<Void> copyProcessAsync(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        return Task.supply(() -> {
            ProcessInformation information = this.getProcess(processUniqueId);
            if (information == null) {
                return null;
            }

            this.copyProcess0(information, targetTemplate, targetTemplateStorage, targetTemplateGroup);
            return null;
        });
    }

    private void copyProcess0(@NotNull ProcessInformation target, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        DefaultChannelManager.INSTANCE.get(target.getProcessDetail().getParentName()).ifPresent(
                packetSender -> packetSender.sendPacket(new ControllerPacketOutCopyProcess(
                                target.getProcessDetail().getProcessUniqueID(),
                                targetTemplate,
                                targetTemplateStorage,
                                targetTemplateGroup
                        )
                ));
    }

    @NotNull
    @Override
    public Task<ProcessInformation> getProcessAsync(@NotNull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcess(name)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> getProcessAsync(@NotNull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcess(uniqueID)));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getAllProcesses()));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(@NotNull String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcesses(group)));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> executeProcessCommandAsync(@NotNull String name, @NotNull String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = getProcess(name);
            if (information == null) {
                task.complete(null);
                return;
            }

            DefaultChannelManager.INSTANCE.get(information.getProcessDetail().getParentName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerExecuteCommand(name, commandLine)));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Integer> getGlobalOnlineCountAsync(@NotNull Collection<String> ignoredProxies) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager
                .getAllProcesses()
                .stream()
                .filter(processInformation -> !processInformation.getProcessDetail().getTemplate().isServer()
                        && !ignoredProxies.contains(processInformation.getProcessDetail().getName()))
                .mapToInt(e -> e.getProcessPlayerManager().getOnlineCount())
                .sum()
        ));
        return task;
    }

    @Nullable
    @Override
    public ProcessInformation startProcess(@NotNull ProcessConfiguration configuration) {
        return this.startProcessAsync(configuration).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @NotNull
    @Override
    public ProcessInformation startProcess(@NotNull ProcessInformation processInformation) {
        ProcessInformation information = this.startProcessAsync(processInformation).getUninterruptedly(TimeUnit.SECONDS, 5);
        return information == null ? processInformation : information;
    }

    @Nullable
    @Override
    public ProcessInformation prepareProcess(@NotNull ProcessConfiguration configuration) {
        return this.prepareProcessAsync(configuration).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public ProcessInformation stopProcess(@NotNull String name) {
        return stopProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(@NotNull UUID uniqueID) {
        return stopProcessAsync(uniqueID).getUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull String name) {
        this.copyProcessAsync(name).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull UUID processUniqueId) {
        this.copyProcessAsync(processUniqueId).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull String name, @NotNull String targetTemplate) {
        this.copyProcessAsync(name, targetTemplate).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull UUID processUniqueId, @NotNull String targetTemplate) {
        this.copyProcessAsync(processUniqueId, targetTemplate).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        this.copyProcessAsync(name, targetTemplate, targetTemplateStorage).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage) {
        this.copyProcessAsync(processUniqueId, targetTemplate, targetTemplateStorage).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull String name, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        this.copyProcessAsync(name, targetTemplate, targetTemplateStorage, targetTemplateGroup).awaitUninterruptedly();
    }

    @Override
    public void copyProcess(@NotNull UUID processUniqueId, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        this.copyProcessAsync(processUniqueId, targetTemplate, targetTemplateStorage, targetTemplateGroup).awaitUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@NotNull String name) {
        return getProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@NotNull UUID uniqueID) {
        return getProcessAsync(uniqueID).getUninterruptedly();
    }

    @NotNull
    @Override
    public List<ProcessInformation> getAllProcesses() {
        List<ProcessInformation> list = getAllProcessesAsync().getUninterruptedly();
        Conditions.nonNull(list);
        return list;
    }

    @NotNull
    @Override
    public List<ProcessInformation> getProcesses(@NotNull String group) {
        List<ProcessInformation> information = getProcessesAsync(group).getUninterruptedly();
        Conditions.nonNull(information);
        return information;
    }

    @Override
    public void executeProcessCommand(@NotNull String name, @NotNull String commandLine) {
        executeProcessCommandAsync(name, commandLine).awaitUninterruptedly();
    }

    @Override
    public int getGlobalOnlineCount(@NotNull Collection<String> ignoredProxies) {
        Integer integer = getGlobalOnlineCountAsync(ignoredProxies).getUninterruptedly();
        return integer == null ? 0 : integer;
    }

    @Override
    public void update(@NotNull ProcessInformation processInformation) {
        this.processManager.update(processInformation);
    }
}
