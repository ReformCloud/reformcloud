package systems.reformcloud.reformcloud2.executor.api.common.api.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    Task<ProcessInformation> startProcessAsync(@Nonnull String groupName);

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template The template which should be used
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template);

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template The template which should be used
     * @param configurable The data for the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, @Nullable String template, @Nonnull JsonConfiguration configurable);

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
     * @param name The name of the process
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
     */
    @Nullable
    @CheckReturnValue
    Task<ProcessInformation> getThisProcessInformationAsync();
}
