package systems.reformcloud.reformcloud2.executor.controller.api.process;

import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.controller.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.api.ControllerExecuteCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProcessAPIImplementation implements ProcessSyncAPI, ProcessAsyncAPI {

    public ProcessAPIImplementation(ProcessManager processManager) {
        this.processManager = processManager;
    }

    private final ProcessManager processManager;

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName) {
        return startProcessAsync(groupName, null);
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, String template) {
        return startProcessAsync(groupName, template, new JsonConfiguration());
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, String template, @Nonnull JsonConfiguration configurable) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.startProcess(groupName, template, configurable)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull ProcessInformation processInformation) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.startProcess(processInformation)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName) {
        return this.startProcessAsync(groupName, null);
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template) {
        return this.prepareProcessAsync(groupName, template, new JsonConfiguration());
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template, @Nonnull JsonConfiguration configurable) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.prepareProcess(groupName, template, configurable)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.stopProcess(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.stopProcess(uniqueID)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcess(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcess(uniqueID)));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getAllProcesses()));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(@Nonnull String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcesses(group)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> executeProcessCommandAsync(@Nonnull String name, @Nonnull String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = getProcess(name);
            if (information == null) {
                task.complete(null);
                return;
            }

            DefaultChannelManager.INSTANCE.get(information.getParent()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerExecuteCommand(name, commandLine)));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getGlobalOnlineCountAsync(@Nonnull Collection<String> ignoredProxies) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getAllProcesses().stream().filter(processInformation -> !processInformation.getTemplate().isServer() && !ignoredProxies.contains(processInformation.getName())).mapToInt(ProcessInformation::getOnlineCount).sum()));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getThisProcessInformationAsync() {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getThisProcessInformation()));
        return task;
    }

    @Nullable
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName) {
        return startProcessAsync(groupName).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName, String template) {
        return startProcessAsync(groupName, template).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName, String template, @Nonnull JsonConfiguration configurable) {
        return startProcessAsync(groupName, template, configurable).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessInformation startProcess(@Nonnull ProcessInformation processInformation) {
        ProcessInformation information = this.startProcessAsync(processInformation).getUninterruptedly(TimeUnit.SECONDS, 5);
        return information == null ? processInformation : information;
    }

    @Nullable
    @Override
    public ProcessInformation prepareProcess(@Nonnull String groupName) {
        return this.prepareProcess(groupName, null);
    }

    @Nullable
    @Override
    public ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template) {
        return this.prepareProcess(groupName, template, new JsonConfiguration());
    }

    @Nullable
    @Override
    public ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template, @Nonnull JsonConfiguration configurable) {
        return this.prepareProcessAsync(groupName, template, configurable).getUninterruptedly(TimeUnit.SECONDS, 10);
    }

    @Override
    public ProcessInformation stopProcess(@Nonnull String name) {
        return stopProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(@Nonnull UUID uniqueID) {
        return stopProcessAsync(uniqueID).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@Nonnull String name) {
        return getProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@Nonnull UUID uniqueID) {
        return getProcessAsync(uniqueID).getUninterruptedly();
    }

    @Nonnull
    @Override
    public List<ProcessInformation> getAllProcesses() {
        List<ProcessInformation> list = getAllProcessesAsync().getUninterruptedly();
        Conditions.nonNull(list);
        return list;
    }

    @Nonnull
    @Override
    public List<ProcessInformation> getProcesses(@Nonnull String group) {
        List<ProcessInformation> information = getProcessesAsync(group).getUninterruptedly();
        Conditions.nonNull(information);
        return information;
    }

    @Override
    public void executeProcessCommand(@Nonnull String name, @Nonnull String commandLine) {
        executeProcessCommandAsync(name, commandLine).awaitUninterruptedly();
    }

    @Override
    public int getGlobalOnlineCount(@Nonnull Collection<String> ignoredProxies) {
        Integer integer = getGlobalOnlineCountAsync(ignoredProxies).getUninterruptedly();
        return integer == null ? 0 : integer;
    }

    @Override
    public ProcessInformation getThisProcessInformation() {
        return null;
    }

    @Override
    public void update(@Nonnull ProcessInformation processInformation) {
        this.processManager.update(processInformation);
    }
}
