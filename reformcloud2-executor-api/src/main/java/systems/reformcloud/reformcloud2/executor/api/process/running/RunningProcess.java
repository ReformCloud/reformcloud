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
package systems.reformcloud.reformcloud2.executor.api.process.running;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.running.screen.RunningProcessScreen;
import systems.reformcloud.reformcloud2.executor.api.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents a running process either in the node or client
 */
public interface RunningProcess {

    /**
     * Prepares the current process
     *
     * @return A task which will get completed when the complete process is prepared
     */
    @NotNull
    Task<Void> prepare();

    /**
     * Handles the queue of the process into the process queue of a node/client
     */
    void handleEnqueue();

    /**
     * Starts the current process
     *
     * @return If the process got started successfully
     */
    boolean bootstrap();

    /**
     * Closes the current process if it's running
     */
    void shutdown();

    /**
     * Copies the current process into the template
     */
    void copy();

    /**
     * Copies the current process into the template
     *
     * @param targetTemplate        The target template to which the server should get copied
     * @param targetTemplateStorage The target template storage to which the template should get copied
     * @param targetTemplateGroup   The target process group to which the template should get copied
     */
    void copy(@NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup);

    /**
     * Uploads the log file of the current process to the reformcloud paste server
     *
     * @return The uploaded log file url
     */
    @NotNull
    String uploadLog();

    /**
     * @return An optional which is either empty if the process is not started or contains the current process
     */
    @NotNull
    ReferencedOptional<Process> getProcess();

    /**
     * @return The process information of the current service
     */
    @NotNull
    ProcessInformation getProcessInformation();

    /**
     * @return The screen for the current process
     */
    @NotNull
    RunningProcessScreen getProcessScreen();

    /**
     * Sends the specified command to the console
     *
     * @param line The command line which should get sent
     */
    void sendCommand(@NotNull String line);

    /**
     * @return If the process is started an currently alive
     */
    boolean isAlive();

    /**
     * @return The startup time of the process or {@code -1} if the process is not started
     */
    long getStartupTime();

    /**
     * @return The path in which the process (should) operate
     */
    @NotNull
    Path getPath();

    /**
     * @return All shutdown commands for the current process
     */
    @NotNull
    default String[] getShutdownCommands() {
        Collection<String> commands = this.getProcessInformation().getProcessDetail().getTemplate().getRuntimeConfiguration().getShutdownCommands();
        commands.addAll(Arrays.asList("stop", "end"));
        return commands.stream().map(e -> e + "\n").toArray(String[]::new);
    }
}
