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
package systems.reformcloud.reformcloud2.executor.api.client.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.Collection;
import java.util.UUID;

public interface ProcessManager {

    /**
     * Registers the new process in the client
     *
     * @param runningProcess The process which should get registered
     */
    void registerProcess(@NotNull RunningProcess runningProcess);

    /**
     * Unregisters a process by the name
     *
     * @param name The name of the process which should get unregistered
     */
    void unregisterProcess(@NotNull String name);

    /**
     * Gets a specific process which is started in the client by the unique id
     *
     * @param uniqueID The unique id of the process
     * @return An optional which contains the running or is empty when no process can get found with the unique id
     * @see ReferencedOptional#isEmpty()
     * @see ReferencedOptional#isPresent()
     */
    @NotNull
    ReferencedOptional<RunningProcess> getProcess(@NotNull UUID uniqueID);

    /**
     * Gets a specific process which is started in the client by the name
     *
     * @param name The name of the process
     * @return An optional which contains the running or is empty when no process can get found with the name
     * @see ReferencedOptional#isEmpty()
     * @see ReferencedOptional#isPresent()
     */
    @NotNull
    ReferencedOptional<RunningProcess> getProcess(String name);

    /**
     * @return All processes which are registered in the client
     */
    @NotNull
    Collection<RunningProcess> getAll();

    /**
     * Handles the disconnect of a network channel from a process
     *
     * @param uuid The unique id of the process which is disconnected
     */
    void onProcessDisconnect(@NotNull UUID uuid);

    /**
     * Stops all currently running processes
     */
    void stopAll();
}
