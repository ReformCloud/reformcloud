package systems.reformcloud.reformcloud2.executor.controller.api.process;

import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.controller.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.api.ControllerExecuteCommand;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

    @Nonnull
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName) {
        return startProcessAsync(groupName).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName, String template) {
        return startProcessAsync(groupName, template).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName, String template, @Nonnull JsonConfiguration configurable) {
        return startProcessAsync(groupName, template, configurable).getUninterruptedly();
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
        return getAllProcessesAsync().getUninterruptedly();
    }

    @Nonnull
    @Override
    public List<ProcessInformation> getProcesses(@Nonnull String group) {
        return getProcessesAsync(group).getUninterruptedly();
    }

    @Override
    public void executeProcessCommand(@Nonnull String name, @Nonnull String commandLine) {
        executeProcessCommandAsync(name, commandLine).awaitUninterruptedly();
    }

    @Override
    public int getGlobalOnlineCount(@Nonnull Collection<String> ignoredProxies) {
        return getGlobalOnlineCountAsync(ignoredProxies).getUninterruptedly();
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
