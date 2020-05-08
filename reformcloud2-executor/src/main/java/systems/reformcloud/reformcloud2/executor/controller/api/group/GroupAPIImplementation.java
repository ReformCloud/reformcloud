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
package systems.reformcloud.reformcloud2.executor.controller.api.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupEnvironment;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.controller.config.ControllerExecutorConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupAPIImplementation implements GroupAsyncAPI, GroupSyncAPI {

    private final ControllerExecutorConfig controllerExecutorConfig = ControllerExecutor.getInstance().getControllerExecutorConfig();

    @NotNull
    @Override
    public Task<MainGroup> createMainGroupAsync(@NotNull String name) {
        return createMainGroupAsync(name, new ArrayList<>());
    }

    @NotNull
    @Override
    public Task<MainGroup> createMainGroupAsync(@NotNull String name, @NotNull List<String> subgroups) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            MainGroup mainGroup = new MainGroup(name, subgroups);
            task.complete(controllerExecutorConfig.createMainGroup(mainGroup));
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name) {
        return createProcessGroupAsync(name, new ArrayList<>());
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates) {
        return createProcessGroupAsync(name, templates, new StartupConfiguration(
                -1, 1, 1, templates.isEmpty() ? 41000 : templates.get(0).getVersion().getDefaultPort(),
                StartupEnvironment.JAVA_RUNTIME, true, new ArrayList<>()
        ));
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, new PlayerAccessConfiguration(
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
        return createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, false);
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
            task.complete(createProcessGroupAsync(processGroup).getUninterruptedly());
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(controllerExecutorConfig.createProcessGroup(processGroup)));
        return task;
    }

    @NotNull
    @Override
    public Task<MainGroup> updateMainGroupAsync(@NotNull MainGroup mainGroup) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            controllerExecutorConfig.updateMainGroup(mainGroup);
            task.complete(mainGroup);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            controllerExecutorConfig.updateProcessGroup(processGroup);
            task.complete(processGroup);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<MainGroup> getMainGroupAsync(@NotNull String name) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.filter(controllerExecutorConfig.getMainGroups(), mainGroup -> mainGroup.getName().equals(name))));
        return task;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> getProcessGroupAsync(@NotNull String name) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Streams.filter(controllerExecutorConfig.getProcessGroups(), processGroup -> processGroup.getName().equals(name))));
        return task;
    }

    @NotNull
    @Override
    public Task<Void> deleteMainGroupAsync(@NotNull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Streams.filterToReference(controllerExecutorConfig.getMainGroups(), mainGroup -> mainGroup.getName().equals(name)).ifPresent(controllerExecutorConfig::deleteMainGroup);
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> deleteProcessGroupAsync(@NotNull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Streams.filterToReference(controllerExecutorConfig.getProcessGroups(), processGroup -> processGroup.getName().equals(name)).ifPresent(controllerExecutorConfig::deleteProcessGroup);
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        Task<List<MainGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Collections.unmodifiableList(controllerExecutorConfig.getMainGroups())));
        return task;
    }

    @NotNull
    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        Task<List<ProcessGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Collections.unmodifiableList(controllerExecutorConfig.getProcessGroups())));
        return task;
    }

    @NotNull
    @Override
    public MainGroup createMainGroup(@NotNull String name) {
        MainGroup mainGroup = createMainGroupAsync(name).getUninterruptedly();
        Conditions.nonNull(mainGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return mainGroup;
    }

    @NotNull
    @Override
    public MainGroup createMainGroup(@NotNull String name, @NotNull List<String> subgroups) {
        MainGroup mainGroup = createMainGroupAsync(name, subgroups).getUninterruptedly();
        Conditions.nonNull(mainGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return mainGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name) {
        ProcessGroup processGroup = createProcessGroupAsync(name).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates) {
        ProcessGroup processGroup = createProcessGroupAsync(name, templates).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration) {
        ProcessGroup processGroup = createProcessGroupAsync(name, templates, startupConfiguration).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration) {
        ProcessGroup processGroup = createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates, @NotNull StartupConfiguration startupConfiguration, @NotNull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        ProcessGroup processGroup = createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, staticGroup).getUninterruptedly();
        Conditions.nonNull(processGroup, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup;
    }

    @NotNull
    @Override
    public ProcessGroup createProcessGroup(@NotNull ProcessGroup processGroup) {
        ProcessGroup processGroup1 = createProcessGroupAsync(processGroup).getUninterruptedly();
        Conditions.nonNull(processGroup1, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return processGroup1;
    }

    @NotNull
    @Override
    public MainGroup updateMainGroup(@NotNull MainGroup mainGroup) {
        MainGroup group = updateMainGroupAsync(mainGroup).getUninterruptedly();
        Conditions.nonNull(group, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return group;
    }

    @NotNull
    @Override
    public ProcessGroup updateProcessGroup(@NotNull ProcessGroup processGroup) {
        ProcessGroup group = updateProcessGroupAsync(processGroup).getUninterruptedly();
        Conditions.nonNull(group, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return group;
    }

    @Nullable
    @Override
    public MainGroup getMainGroup(@NotNull String name) {
        return getMainGroupAsync(name).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessGroup getProcessGroup(@NotNull String name) {
        return getProcessGroupAsync(name).getUninterruptedly();
    }

    @Override
    public void deleteMainGroup(@NotNull String name) {
        deleteMainGroupAsync(name).awaitUninterruptedly();
    }

    @Override
    public void deleteProcessGroup(@NotNull String name) {
        deleteProcessGroupAsync(name).awaitUninterruptedly();
    }

    @NotNull
    @Override
    public List<MainGroup> getMainGroups() {
        List<MainGroup> mainGroups = getMainGroupsAsync().getUninterruptedly();
        Conditions.nonNull(mainGroups, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return mainGroups;
    }

    @NotNull
    @Override
    public List<ProcessGroup> getProcessGroups() {
        List<ProcessGroup> groups = getProcessGroupsAsync().getUninterruptedly();
        Conditions.nonNull(groups, "An internal error occurred while executing api method. !!! This is not a bug of reformcloud !!! Please do not report this as a bug"); // Should never happen
        return groups;
    }
}
