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
package systems.reformcloud.reformcloud2.executor.api.process.detail;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessConfiguration;

import java.util.function.Consumer;

/**
 * Represents a wrapper for processes with some util methods.
 *
 * @see ProcessInformation#toWrapped()
 */
public final class ProcessUtil {

    private final ProcessInformation parent;

    /**
     * Creates a new instance of the process util
     *
     * @param parent The parent process information for which the wrapper is made
     */
    @ApiStatus.Internal
    public ProcessUtil(@NotNull ProcessInformation parent) {
        this.parent = parent;
    }

    /**
     * Starts the current process information if the process is only prepared and ready to start
     *
     * @see systems.reformcloud.reformcloud2.executor.api.api.process.ProcessSyncAPI#prepareProcess(ProcessConfiguration)
     */
    public void start() {
        if (!this.parent.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)) {
            return;
        }

        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(this.parent);
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
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(this.parent.getProcessGroup().getName());
    }

    /**
     * Sends the specified command to the process which is associated with this process util
     *
     * @param command The command line which should get sent
     */
    public void sendCommand(@NotNull String command) {
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().executeProcessCommand(
                this.parent.getProcessDetail().getName(), command
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
        consumer.accept(this.parent);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(this.parent.getProcessDetail().getProcessUniqueID());
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
        consumer.accept(this.parent);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.parent);
    }
}
