/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.node.api.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.manager.SharedRunningProcessManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutCopyProcess;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutExecuteCommand;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProcessAPIImplementation implements ProcessSyncAPI, ProcessAsyncAPI {

    private final NodeNetworkManager nodeNetworkManager;

    public ProcessAPIImplementation(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> startProcessAsync(@NotNull ProcessConfiguration configuration) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.prepareProcess(configuration, true)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> startProcessAsync(@NotNull ProcessInformation processInformation) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.startProcess(processInformation)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> prepareProcessAsync(@NotNull ProcessConfiguration configuration) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.prepareProcess(configuration, false)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@NotNull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation old = nodeNetworkManager.getNodeProcessHelper().getClusterProcess(name);
            if (old == null) {
                task.complete(null);
                return;
            }

            nodeNetworkManager.stopProcess(old.getProcessDetail().getName());
            task.complete(old);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@NotNull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation old = nodeNetworkManager.getNodeProcessHelper().getClusterProcess(uniqueID);
            if (old == null) {
                task.complete(null);
                return;
            }

            nodeNetworkManager.stopProcess(old.getProcessDetail().getProcessUniqueID());
            task.complete(old);
        });
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
        if (NodeExecutor.getInstance().getNodeConfig().getUniqueID().equals(target.getProcessDetail().getParentUniqueID())) {
            Streams.filterToReference(
                    SharedRunningProcessManager.getAllProcesses(),
                    e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(target.getProcessDetail().getProcessUniqueID())
            ).ifPresent(e -> e.copy(targetTemplate, targetTemplateStorage, targetTemplateGroup));
        } else {
            DefaultChannelManager.INSTANCE.get(target.getProcessDetail().getParentName()).ifPresent(packetSender -> packetSender.sendPacket(
                    new NodePacketOutCopyProcess(
                            target.getProcessDetail().getProcessUniqueID(),
                            targetTemplate,
                            targetTemplateStorage,
                            targetTemplateGroup
                    )
            ));
        }
    }

    @NotNull
    @Override
    public Task<ProcessInformation> getProcessAsync(@NotNull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.getNodeProcessHelper().getClusterProcess(name)));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessInformation> getProcessAsync(@NotNull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.getNodeProcessHelper().getClusterProcess(uniqueID)));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.newList(nodeNetworkManager.getNodeProcessHelper().getClusterProcesses())));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(@NotNull String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.newList(nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(group))));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> executeProcessCommandAsync(@NotNull String name, @NotNull String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getProcess(name);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (NodeExecutor.getInstance().getNodeConfig().getUniqueID().equals(processInformation.getProcessDetail().getParentUniqueID())) {
                Streams.filterToReference(
                        SharedRunningProcessManager.getAllProcesses(),
                        e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())
                ).ifPresent(e -> e.sendCommand(commandLine));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName())
                        .ifPresent(e -> e.sendPacket(new NodePacketOutExecuteCommand(processInformation.getProcessDetail().getName(), commandLine)));
            }

            task.complete(null);
        });
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
        ProcessInformation result = this.startProcessAsync(processInformation).getUninterruptedly(TimeUnit.SECONDS, 5);
        return result == null ? processInformation : result;
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
    public void update(@NotNull ProcessInformation processInformation) {
        this.nodeNetworkManager.getNodeProcessHelper().update(processInformation);
    }
}
