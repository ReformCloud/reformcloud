package systems.reformcloud.reformcloud2.executor.api.common.api.process;

import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ProcessSyncAPI {

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    ProcessInformation startProcess(@Nonnull String groupName);

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template  The template which should be used
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template);

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    ProcessInformation startProcess(
            @Nonnull String groupName,
            @Nullable String template,
            @Nonnull JsonConfiguration configurable
    );

    /**
     * Stops a process
     *
     * @param name The name of the process
     * @return The last known {@link ProcessInformation}
     */
    @Nullable
    ProcessInformation stopProcess(@Nonnull String name);

    /**
     * Stops a process
     *
     * @param uniqueID The uniqueID of the process
     * @return The last {@link ProcessInformation}
     */
    @Nullable
    ProcessInformation stopProcess(@Nonnull UUID uniqueID);

    /**
     * Gets a process
     *
     * @param name The name of the process
     * @return The {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    @Nullable
    ProcessInformation getProcess(@Nonnull String name);

    /**
     * Gets a process
     *
     * @param uniqueID The uniqueID of the process
     * @return The {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    @Nullable
    ProcessInformation getProcess(@Nonnull UUID uniqueID);

    /**
     * Get all processes
     *
     * @return All started processes
     */
    @Nonnull
    List<ProcessInformation> getAllProcesses();

    /**
     * Get all processes of a specific group
     *
     * @param group The group which should be searched for
     * @return All started processes of the specified groups
     */
    @Nonnull
    List<ProcessInformation> getProcesses(@Nonnull String group);

    /**
     * Executes a command on a process
     *
     * @param name        The name of the process
     * @param commandLine The command line with should be executed
     */
    void executeProcessCommand(@Nonnull String name, @Nonnull String commandLine);

    /**
     * Gets the global online count
     *
     * @param ignoredProxies The ignored proxies
     * @return The global online count
     */
    int getGlobalOnlineCount(@Nonnull Collection<String> ignoredProxies);

    /**
     * Get the current process information
     *
     * @return The current {@link ProcessInformation} or {@code null} if the runtime is not a process
     * @deprecated Has been moved to {@link API#getCurrentProcessInformation()}. Will be removed in a further release
     */
    @Nullable
    @Deprecated
    ProcessInformation getThisProcessInformation();

    /**
     * Iterates through all {@link ProcessInformation}
     *
     * @param action The consumer which will accept by each {@link ProcessInformation}
     */
    default void forEach(@Nonnull Consumer<ProcessInformation> action) {
        getAllProcesses().forEach(action);
    }

    /**
     * Updates a specific {@link ProcessInformation}
     *
     * @param processInformation The process information which should be updated
     */
    void update(@Nonnull ProcessInformation processInformation);
}
