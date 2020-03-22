package systems.reformcloud.reformcloud2.executor.api.common.api.process;

import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfigurationBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
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
    default ProcessInformation startProcess(@Nonnull String groupName) {
        return this.startProcess(groupName, null);
    }

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template  The template which should be used
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template) {
        return this.startProcess(groupName, template, new JsonConfiguration());
    }

    /**
     * Starts a process
     *
     * @param groupName    The name of the group which should be started from
     * @param template     The template which should be used
     * @param configurable The data for the process
     * @return The created {@link ProcessInformation}
     */
    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template, @Nonnull JsonConfiguration configurable) {
        return this.startProcess(groupName, template, configurable, UUID.randomUUID());
    }

    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template,
                                            @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID) {
        return this.startProcess(groupName, template, configurable, uniqueID, null);
    }

    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template,
                                            @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                            @Nullable String displayName) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, null);
    }

    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template,
                                            @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, null);
    }

    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template,
                                            @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory,
                                            @Nullable Integer port) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, null);
    }

    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template,
                                            @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory,
                                            @Nullable Integer port, @Nullable Integer id) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, null);
    }

    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template,
                                            @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                            @Nullable String displayName, @Nullable Integer maxMemory,
                                            @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers) {
        return this.startProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, maxPlayers, new ArrayList<>());
    }

    @Nullable
    default ProcessInformation startProcess(@Nonnull String groupName, @Nullable String template,
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

        return this.startProcess(builder.build());
    }

    @Nullable
    ProcessInformation startProcess(@Nonnull ProcessConfiguration configuration);

    /**
     * Starts a prepared process
     *
     * @param processInformation The process information of the prepared process
     * @return The process information after the start call
     */
    @Nonnull
    ProcessInformation startProcess(@Nonnull ProcessInformation processInformation);

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName) {
        return this.prepareProcess(groupName, null);
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template) {
        return this.prepareProcess(groupName, template, new JsonConfiguration());
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template, @Nonnull JsonConfiguration configurable) {
        return this.prepareProcess(groupName, template, configurable, UUID.randomUUID());
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template,
                                              @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, null);
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template,
                                              @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                              @Nullable String displayName) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, null);
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template,
                                              @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, null);
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template,
                                              @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory,
                                              @Nullable Integer port) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, null);
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template,
                                              @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory,
                                              @Nullable Integer port, @Nullable Integer id) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, null);
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template,
                                              @Nonnull JsonConfiguration configurable, @Nonnull UUID uniqueID,
                                              @Nullable String displayName, @Nullable Integer maxMemory,
                                              @Nullable Integer port, @Nullable Integer id, @Nullable Integer maxPlayers) {
        return this.prepareProcess(groupName, template, configurable, uniqueID, displayName, maxMemory, port, id, maxPlayers, new ArrayList<>());
    }

    @Nullable
    default ProcessInformation prepareProcess(@Nonnull String groupName, @Nullable String template,
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

        return this.prepareProcess(builder.build());
    }

    @Nullable
    ProcessInformation prepareProcess(@Nonnull ProcessConfiguration configuration);

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
