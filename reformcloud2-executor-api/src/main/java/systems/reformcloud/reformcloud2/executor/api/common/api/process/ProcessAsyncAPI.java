package systems.reformcloud.reformcloud2.executor.api.common.api.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfigurationBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

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
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName) {
        return this.startProcessAsync(groupName, null);
    }

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template  The template which should be used
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template) {
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
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template, @NotNull JsonConfiguration configurable) {
        return this.startProcessAsync(groupName, template, configurable, UUID.randomUUID());
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template,
                                                       @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template,
                                                       @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                       @Nullable String displayName) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template,
                                                       @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template,
                                                       @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template,
                                                       @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port, @Nullable Integer id) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, null);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template,
                                                       @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, maxPlayers, new ArrayList<>());
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @param inclusions   The inclusions which should get loaded before the start of the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template,
                                                       @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers,
                                                       @NotNull Collection<ProcessInclusion> inclusions) {
        return this.startProcessAsync(groupName, template, configurable, uniqueID, displayName,
                maxMemory, port, id, maxPlayers, inclusions, ProcessState.READY);
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @param inclusions   The inclusions which should get loaded before the start of the process
     * @param initialState The state which the process should set after the connect to the network
     * @return The created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> startProcessAsync(@NotNull String groupName, @Nullable String template,
                                                       @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                       @Nullable String displayName, @Nullable Integer maxMemory,
                                                       @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers,
                                                       @NotNull Collection<ProcessInclusion> inclusions, @NotNull ProcessState initialState) {
        ProcessConfigurationBuilder builder = ProcessConfigurationBuilder
                .newBuilder(groupName)
                .extra(configurable)
                .uniqueId(uniqueID)
                .initialState(initialState)
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

    /**
     * Starts a process based on the given configuration
     *
     * @param configuration The configuration which is the base for the new process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     * @see ProcessConfigurationBuilder#newBuilder(String)
     */
    @NotNull
    Task<ProcessInformation> startProcessAsync(@NotNull ProcessConfiguration configuration);

    /**
     * Starts a prepared process
     *
     * @param processInformation The process information of the prepared process
     * @return A task which which will be completed with the {@link ProcessInformation}
     */
    @NotNull
    Task<ProcessInformation> startProcessAsync(@NotNull ProcessInformation processInformation);

    /**
     * Prepares a process
     *
     * @param groupName The name of the group which should be started from
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName) {
        return this.prepareProcessAsync(groupName, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName The name of the group which should be started from
     * @param template  The template which should be used
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template) {
        return this.prepareProcessAsync(groupName, template, new JsonConfiguration());
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template, @NotNull JsonConfiguration configurable) {
        return this.prepareProcessAsync(groupName, template, configurable, UUID.randomUUID());
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template,
                                                         @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template,
                                                         @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                         @Nullable String displayName) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template,
                                                         @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template,
                                                         @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template,
                                                         @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port, @Nullable Integer id) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, null);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template,
                                                         @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, maxPlayers, new ArrayList<>());
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @param inclusions   The inclusions which should get loaded before the start of the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template,
                                                         @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers,
                                                         @NotNull Collection<ProcessInclusion> inclusions) {
        return this.prepareProcessAsync(groupName, template, configurable, uniqueID, displayName,
                maxMemory, port, id, maxPlayers, inclusions, ProcessState.READY);
    }

    /**
     * Prepares a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @param uniqueID     The unique id which should get used for the process
     * @param displayName  The display name of the new process
     * @param maxMemory    The maximum amount of memory which the new process is allowed to use
     * @param port         The port of the new process (If it's already in use a random port will be used)
     * @param id           The id of the process (for example {@code 1}). The name of the process will also use
     *                     the given id (for {@code 1} it might be {@code Lobby-1}). If the id is already taken
     *                     a random id will get used
     * @param maxPlayers   The maximum amount of player which are allowed to join the process
     * @param inclusions   The inclusions which should get loaded before the start of the process
     * @param initialState The state which the process should set after the connect to the network
     * @return The created {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> prepareProcessAsync(@NotNull String groupName, @Nullable String template,
                                                         @NotNull JsonConfiguration configurable, @NotNull UUID uniqueID,
                                                         @Nullable String displayName, @Nullable Integer maxMemory,
                                                         @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers,
                                                         @NotNull Collection<ProcessInclusion> inclusions, @NotNull ProcessState initialState) {
        ProcessConfigurationBuilder builder = ProcessConfigurationBuilder
                .newBuilder(groupName)
                .extra(configurable)
                .uniqueId(uniqueID)
                .initialState(initialState)
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

    /**
     * Prepares a process based on the given configuration
     *
     * @param configuration The configuration which is the base for the new process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     * @see ProcessConfigurationBuilder#newBuilder(String)
     */
    @NotNull
    Task<ProcessInformation> prepareProcessAsync(@NotNull ProcessConfiguration configuration);

    /**
     * Stops a process
     *
     * @param processInformation The process information of the process which should get stopped
     * @return A task which will be completed with the last {@link ProcessInformation}
     */
    @NotNull
    default Task<ProcessInformation> stopProcessAsync(@NotNull ProcessInformation processInformation) {
        return this.stopProcessAsync(processInformation.getProcessDetail().getProcessUniqueID());
    }

    /**
     * Stops a process
     *
     * @param name The name of the process
     * @return A task which will be completed with the last {@link ProcessInformation}
     */
    @NotNull
    Task<ProcessInformation> stopProcessAsync(@NotNull String name);

    /**
     * Stops a process
     *
     * @param uniqueID The uniqueID of the process
     * @return A task which will be completed with the last {@link ProcessInformation}
     */
    @NotNull
    Task<ProcessInformation> stopProcessAsync(@NotNull UUID uniqueID);

    /**
     * Gets a process
     *
     * @param name The name of the process
     * @return A task which will be completed with the {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    @NotNull
    Task<ProcessInformation> getProcessAsync(@NotNull String name);

    /**
     * Gets a process
     *
     * @param uniqueID The uniqueID of the process
     * @return A task which will be completed with the {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    @NotNull
    Task<ProcessInformation> getProcessAsync(@NotNull UUID uniqueID);

    /**
     * Get all processes
     *
     * @return A task with all started processes
     */
    @NotNull
    Task<List<ProcessInformation>> getAllProcessesAsync();

    /**
     * Get all processes of a specific group
     *
     * @param group The group which should be searched for
     * @return A task with all started processes of the specified groups
     */
    @NotNull
    Task<List<ProcessInformation>> getProcessesAsync(@NotNull String group);

    /**
     * Executes a command on a process
     *
     * @param name        The name of the process
     * @param commandLine The command line with should be executed
     * @return A task which will be completed after the packet sent
     */
    @NotNull
    Task<Void> executeProcessCommandAsync(@NotNull String name, @NotNull String commandLine);

    /**
     * Gets the global online count
     *
     * @param ignoredProxies The ignored proxies
     * @return A task which will be completed with the global online count
     */
    @NotNull
    Task<Integer> getGlobalOnlineCountAsync(@NotNull Collection<String> ignoredProxies);

    /**
     * Updates a specific {@link ProcessInformation}
     *
     * @param processInformation The process information which should be updated
     * @return A task which will be completed after the update of the {@link ProcessInformation}
     */
    default Task<Void> updateAsync(@NotNull ProcessInformation processInformation) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);
            task.complete(null);
        });
        return task;
    }
}
