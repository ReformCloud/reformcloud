package systems.reformcloud.reformcloud2.executor.api.common.api.process;

import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfigurationBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProcessAsyncAPI {

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName) {
        return this.startProcessAsync(groupName, null);
    }

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template  The template which should be used
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template) {
        return this.startProcessAsync(groupName, template, new JsonConfiguration());
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template, @Nonnull JsonConfiguration configurable) {
        return this.startProcessAsync(groupName, template, configurable, UUID.randomUUID());
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                       @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                       @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                       @Nullable String displayName) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                       @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                       @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                       @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port, @Nullable Integer id) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                       @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, maxPlayers, new ArrayList<>());
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                       @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers,
                                                       @Nonnull Collection<Duo<String, String>> inclusions) {
        ProcessConfigurationBuilder builder = ProcessConfigurationBuilder
                .newBuilder(groupName)
                .extra(configurable)
                .uniqueId(uniqueID)
                .inclusions(inclusions);

        if (template != null) {
            builder.template(template);
        }

        if (id != null && id > 0) {
            builder.id(id);
        }

        if (displayName != null) {
            builder.displayName(displayName);
        }

        if (maxMemory != null && maxMemory > 100) {
            builder.maxMemory(maxMemory);
        }

        if (port != null && port > 0) {
            builder.port(port);
        }

        if (maxPlayers != null && maxPlayers > 0) {
            builder.maxPlayers(maxPlayers);
        }

        return this.startProcessAsync(builder.build());
    }

    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> startProcessAsync(@Nonnull ProcessConfiguration configuration);

    /**
     * Starts a prepared process
     *
     * @param processInformation The process information of the prepared process
     * @return The process information after the start call
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> startProcessAsync(@Nonnull ProcessInformation processInformation);

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName) {
        return this.prepareProcessAsync(groupName, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template) {
        return this.prepareProcessAsync(groupName, template, new JsonConfiguration());
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template, @Nonnull JsonConfiguration configurable) {
        return this.prepareProcessAsync(groupName, template, configurable, UUID.randomUUID());
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                         @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                         @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                         @Nullable String displayName) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                         @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                         @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                         @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port, @Nullable Integer id) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, null);
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                         @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, maxPlayers, new ArrayList<>());
    }

    @Nonnull
    @CheckReturnValue
    default Task<ProcessInformation> prepareProcessAsync(@Nonnull String groupName, @Nullable String template,
                                                         @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers,
                                                         @Nonnull Collection<Duo<String, String>> inclusions) {
        ProcessConfigurationBuilder builder = ProcessConfigurationBuilder
                .newBuilder(groupName)
                .extra(configurable)
                .uniqueId(uniqueID)
                .inclusions(inclusions);

        if (template != null) {
            builder.template(template);
        }

        if (id != null && id > 0) {
            builder.id(id);
        }

        if (displayName != null) {
            builder.displayName(displayName);
        }

        if (maxMemory != null && maxMemory > 100) {
            builder.maxMemory(maxMemory);
        }

        if (port != null && port > 0) {
            builder.port(port);
        }

        if (maxPlayers != null && maxPlayers > 0) {
            builder.maxPlayers(maxPlayers);
        }

        return this.prepareProcessAsync(builder.build());
    }

    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> prepareProcessAsync(@Nonnull ProcessConfiguration configuration);

    /**
     * Stops a process
     *
     * @param name The name of the process
     * @return A task which will be completed with the last {@link ProcessInformation}
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> stopProcessAsync(@Nonnull String name);

    /**
     * Stops a process
     *
     * @param uniqueID The uniqueID of the process
     * @return A task which will be completed with the last {@link ProcessInformation}
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> stopProcessAsync(@Nonnull UUID uniqueID);

    /**
     * Gets a process
     *
     * @param name The name of the process
     * @return A task which will be completed with the {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> getProcessAsync(@Nonnull String name);

    /**
     * Gets a process
     *
     * @param uniqueID The uniqueID of the process
     * @return A task which will be completed with the {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> getProcessAsync(@Nonnull UUID uniqueID);

    /**
     * Get all processes
     *
     * @return A task with all started processes
     */
    @Nonnull
    @CheckReturnValue
    Task<List<ProcessInformation>> getAllProcessesAsync();

    /**
     * Get all processes of a specific group
     *
     * @param group The group which should be searched for
     * @return A task with all started processes of the specified groups
     */
    @Nonnull
    @CheckReturnValue
    Task<List<ProcessInformation>> getProcessesAsync(@Nonnull String group);

    /**
     * Executes a command on a process
     *
     * @param name        The name of the process
     * @param commandLine The command line with should be executed
     * @return A task which will be completed after the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> executeProcessCommandAsync(@Nonnull String name, @Nonnull String commandLine);

    /**
     * Gets the global online count
     *
     * @param ignoredProxies The ignored proxies
     * @return A task which will be completed with the global online count
     */
    @Nonnull
    @CheckReturnValue
    Task<Integer> getGlobalOnlineCountAsync(@Nonnull Collection<String> ignoredProxies);

    /**
     * Get the current process information
     *
     * @return A task with will be completed with the current {@link ProcessInformation} or {@code null} if the runtime is not a process
     * @deprecated Has been moved to {@link API#getCurrentProcessInformation()}. Will be removed in a further release
     */
    @Nullable
    @CheckReturnValue
    @Deprecated
    Task<ProcessInformation> getThisProcessInformationAsync();

    /**
     * Updates a specific {@link ProcessInformation}
     *
     * @param processInformation The process information which should be updated
     * @return A task which will be completed after the update of the {@link ProcessInformation}
     */
    default Task<Void> updateAsync(@Nonnull ProcessInformation processInformation) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);
            task.complete(null);
        });
        return task;
    }
}
