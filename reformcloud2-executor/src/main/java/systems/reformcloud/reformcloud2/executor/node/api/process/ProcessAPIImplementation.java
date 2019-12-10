package systems.reformcloud.reformcloud2.executor.node.api.process;

import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutExecuteCommand;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ProcessAPIImplementation implements ProcessSyncAPI, ProcessAsyncAPI {

    public ProcessAPIImplementation(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

    private final NodeNetworkManager nodeNetworkManager;

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
        Task.EXECUTOR.execute(() -> {
            ProcessGroup group = Links.filterToReference(NodeExecutor.getInstance().getClusterSyncManager().getProcessGroups(), e -> e.getName().equals(groupName)).orNothing();
            if (group == null) {
                task.complete(null);
                return;
            }

            task.complete(nodeNetworkManager.startProcess(
                    group,
                    Links.filterToReference(group.getTemplates(), e -> e.getName().equals(template)).orNothing(),
                    configurable
            ));
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation old = nodeNetworkManager.getNodeProcessHelper().getClusterProcess(name);
            if (old == null) {
                task.complete(null);
                return;
            }

            nodeNetworkManager.stopProcess(old.getName());
            task.complete(old);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation old = nodeNetworkManager.getNodeProcessHelper().getClusterProcess(uniqueID);
            if (old == null) {
                task.complete(null);
                return;
            }

            nodeNetworkManager.stopProcess(old.getProcessUniqueID());
            task.complete(old);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.getNodeProcessHelper().getClusterProcess(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.getNodeProcessHelper().getClusterProcess(uniqueID)));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.newList(nodeNetworkManager.getNodeProcessHelper().getClusterProcesses())));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(@Nonnull String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.newList(nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(group))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> executeProcessCommandAsync(@Nonnull String name, @Nonnull String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getProcess(name);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (NodeExecutor.getInstance().getNodeConfig().getUniqueID().equals(processInformation.getNodeUniqueID())) {
                Links.filterToReference(LocalProcessManager.getNodeProcesses(),
                        e -> e.getProcessInformation().getProcessUniqueID().equals(processInformation.getProcessUniqueID())
                ).ifPresent(e -> e.sendCommand(commandLine));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent())
                        .ifPresent(e -> e.sendPacket(new NodePacketOutExecuteCommand(processInformation.getName(), commandLine)));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getGlobalOnlineCountAsync(@Nonnull Collection<String> ignoredProxies) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            int online = Links.allOf(nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(),
                    e -> !e.getTemplate().isServer() && !ignoredProxies.contains(e.getName())
            ).stream().mapToInt(ProcessInformation::getOnlineCount).sum();
            task.complete(online);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getThisProcessInformationAsync() {
        Task<ProcessInformation> task = new DefaultTask<>();
        task.complete(null);
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
        this.nodeNetworkManager.getNodeProcessHelper().update(processInformation);
    }
}
