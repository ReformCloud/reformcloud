package systems.reformcloud.reformcloud2.executor.api.common.process.detail;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;

import java.util.function.Consumer;

/**
 * Represents a wrapper for processes with some util methods.
 *
 * @see ProcessInformation#toWrapped()
 */
public final class ProcessUtil {

    /**
     * Creates a new instance of the process util
     *
     * @param parent The parent process information for which the wrapper is made
     */
    @ApiStatus.Internal
    public ProcessUtil(@NotNull ProcessInformation parent) {
        this.parent = parent;
    }

    private final ProcessInformation parent;

    /**
     * Starts the current process information if the process is only prepared and ready to start
     *
     * @see systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI#prepareProcess(ProcessConfiguration)
     */
    public void start() {
        if (!parent.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)) {
            return;
        }

        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(parent);
    }

    /**
     * Copies the current process into the loaded template
     */
    public void copy() {
        ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().copyProcessAsync(this.parent);
    }

    /**
     * Starts a new process for the same group as the process util was created for
     */
    public void startNewOfSameGroup() {
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(parent.getProcessGroup().getName());
    }

    /**
     * Sends the specified command to the process which is associated with this process util
     *
     * @param command The command line which should get sent
     */
    public void sendCommand(@NotNull String command) {
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().executeProcessCommand(
                parent.getProcessDetail().getName(), command
        );
    }

    /**
     * Stops the process which is associated with this process util
     */
    public void stop() {
        this.acceptAndStop(ignored -> {
        });
    }

    /**
     * Accepts the associated process and stops the process after the consumer call
     *
     * @param consumer The consumer which should consume the information before the stop
     */
    public void acceptAndStop(@NotNull Consumer<ProcessInformation> consumer) {
        consumer.accept(parent);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(parent.getProcessDetail().getProcessUniqueID());
    }

    /**
     * Updates the process info which is associated with the current process
     */
    public void update() {
        this.acceptAndUpdate(ignored -> {
        });
    }

    /**
     * Updates the current process information but accepts it before to the consumer
     *
     * @param consumer The consumer which should handle the process info before the update
     */
    public void acceptAndUpdate(@NotNull Consumer<ProcessInformation> consumer) {
        consumer.accept(parent);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(parent);
    }
}
