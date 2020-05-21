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
package systems.reformcloud.reformcloud2.executor.node.api.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterSyncManager;

import java.util.ArrayList;
import java.util.List;

public class GroupAPIImplementation implements GroupAsyncAPI, GroupSyncAPI {

    private final ClusterSyncManager clusterSyncManager;

    public GroupAPIImplementation(ClusterSyncManager clusterSyncManager) {
        this.clusterSyncManager = clusterSyncManager;
    }

    @NotNull
    @Override
    public Task<MainGroup> createMainGroupAsync(@NotNull String name) {
        return this.createMainGroupAsync(name, new ArrayList<>());
    }

    @NotNull
    @Override
    public Task<MainGroup> createMainGroupAsync(@NotNull String name, @NotNull List<String> subgroups) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.clusterSyncManager.existsMainGroup(name)) {
                task.complete(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(name));
                return;
            }

            MainGroup mainGroup = new MainGroup(name, subgroups);
            this.clusterSyncManager.syncMainGroupCreate(mainGroup);
            task.complete(mainGroup);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name) {
        return this.createProcessGroupAsync(name, new ArrayList<>());
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates) {
        return this.createProcessGroupAsync(name, templates, new StartupConfiguration(
                -1, 1, 1, templates.isEmpty() ? 41000 : templates.get(0).getVersion().getDefaultPort(),
                "java", true, new ArrayList<>()
        ));
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration) {
        return this.createProcessGroupAsync(name, templates, startupConfiguration, new PlayerAccessConfiguration(
                "reformcloud.join.full",
                false,
                "reformcloud.join.maintenance",
                false,
                null,
                true,
                true,
                50
        ));
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration) {
        return this.createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, false);
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessGroup processGroup = new ProcessGroup(
                    name,
                    true,
                    startupConfiguration,
                    templates,
                    playerAccessConfiguration,
                    staticGroup
            );
            task.complete(this.createProcessGroupAsync(processGroup).getUninterruptedly());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.clusterSyncManager.existsProcessGroup(processGroup.getName())) {
                task.complete(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(processGroup.getName()));
                return;
            }

            this.clusterSyncManager.syncProcessGroupCreate(processGroup);
            task.complete(processGroup);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<MainGroup> updateMainGroupAsync(@NotNull MainGroup mainGroup) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.clusterSyncManager.syncMainGroupUpdate(mainGroup);
            task.complete(mainGroup);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.clusterSyncManager.syncProcessGroupUpdate(processGroup);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(e -> {
                e.setProcessGroup(processGroup);
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(e);
            });
            task.complete(processGroup);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<MainGroup> getMainGroupAsync(@NotNull String name) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.filterToReference(this.clusterSyncManager.getMainGroups(), e -> e.getName().equals(name)).orNothing()));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> getProcessGroupAsync(@NotNull String name) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.filterToReference(this.clusterSyncManager.getProcessGroups(), e -> e.getName().equals(name)).orNothing()));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> deleteMainGroupAsync(@NotNull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.clusterSyncManager.syncMainGroupDelete(name);
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> deleteProcessGroupAsync(@NotNull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.clusterSyncManager.syncProcessGroupDelete(name);
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        Task<List<MainGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.newList(this.clusterSyncManager.getMainGroups())));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        Task<List<ProcessGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.newList(this.clusterSyncManager.getProcessGroups())));
        return task;
    }

    @NotNull
    @Override
    public MainGroup createMainGroup(@NotNull String name) {
        MainGroup mainGroup = this.createMainGroupAsync(name).getUninterruptedly();
        Conditions.nonNull(mainGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return mainGroup;
    }

    @NotNull
    @Override
    public MainGroup createMainGroup(@NotNull String name, @NotNull List<String> subgroups) {
        MainGroup mainGroup = this.createMainGroupAsync(name, subgroups).getUninterruptedly();
        Conditions.nonNull(mainGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return mainGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name) {
        ProcessGroup processGroup = this.createProcessGroupAsync(name).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates) {
        ProcessGroup processGroup = this.createProcessGroupAsync(name, templates).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration) {
        ProcessGroup processGroup = this.createProcessGroupAsync(name, templates, startupConfiguration).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration) {
        ProcessGroup processGroup = this.createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        ProcessGroup processGroup = this.createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, staticGroup).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull ProcessGroup processGroup) {
        ProcessGroup processGroup1 = this.createProcessGroupAsync(processGroup).getUninterruptedly();
        Conditions.nonNull(processGroup1, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup1;
    }

    @NotNull
    @Override
    public MainGroup updateMainGroup(@NotNull MainGroup mainGroup) {
        MainGroup group = this.updateMainGroupAsync(mainGroup).getUninterruptedly();
        Conditions.nonNull(group, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return group;
    }

    @NotNull
    @Override
    public ProcessGroup updateProcessGroup(@NotNull ProcessGroup processGroup) {
        ProcessGroup group = this.updateProcessGroupAsync(processGroup).getUninterruptedly();
        Conditions.nonNull(group, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return group;
    }

    @Nullable
    @Override
    public MainGroup getMainGroup(@NotNull String name) {
        return this.getMainGroupAsync(name).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessGroup getProcessGroup(@NotNull String name) {
        return this.getProcessGroupAsync(name).getUninterruptedly();
    }

    @Override
    public void deleteMainGroup(@NotNull String name) {
        this.deleteMainGroupAsync(name).awaitUninterruptedly();
    }

    @Override
    public void deleteProcessGroup(@NotNull String name) {
        this.deleteProcessGroupAsync(name).awaitUninterruptedly();
    }

    @NotNull
    @Override
    public List<MainGroup> getMainGroups() {
        List<MainGroup> mainGroups = this.getMainGroupsAsync().getUninterruptedly();
        Conditions.nonNull(mainGroups, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return mainGroups;
    }

    @NotNull
    @Override
    public List<ProcessGroup> getProcessGroups() {
        List<ProcessGroup> groups = this.getProcessGroupsAsync().getUninterruptedly();
        Conditions.nonNull(groups, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return groups;
    }
}
