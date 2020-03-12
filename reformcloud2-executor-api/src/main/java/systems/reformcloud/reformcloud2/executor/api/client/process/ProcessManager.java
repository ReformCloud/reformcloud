package systems.reformcloud.reformcloud2.executor.api.client.process;

import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;

public interface ProcessManager {

    /**
     * Registers the new process in the client
     *
     * @param runningProcess The process which should get registered
     */
    void registerProcess(@Nonnull RunningProcess runningProcess);

    /**
     * Unregisters a process by the name
     *
     * @param name The name of the process which should get unregistered
     */
    void unregisterProcess(@Nonnull String name);

    /**
     * Gets a specific process which is started in the client by the unique id
     *
     * @param uniqueID The unique id of the process
     * @return An optional which contains the running or is empty when no process can get found with the unique id
     * @see ReferencedOptional#isEmpty()
     * @see ReferencedOptional#isPresent()
     */
    @Nonnull
    ReferencedOptional<RunningProcess> getProcess(@Nonnull UUID uniqueID);

    /**
     * Gets a specific process which is started in the client by the name
     *
     * @param name The name of the process
     * @return An optional which contains the running or is empty when no process can get found with the name
     * @see ReferencedOptional#isEmpty()
     * @see ReferencedOptional#isPresent()
     */
    @Nonnull
    ReferencedOptional<RunningProcess> getProcess(String name);

    /**
     * @return All processes which are registered in the client
     */
    @Nonnull
    Collection<RunningProcess> getAll();

    /**
     * Handles the disconnect of a network channel from a process
     *
     * @param uuid The unique id of the process which is disconnected
     */
    void onProcessDisconnect(@Nonnull UUID uuid);

    /**
     * Stops all currently running processes
     */
    void stopAll();
}
