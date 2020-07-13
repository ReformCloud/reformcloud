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
package systems.reformcloud.reformcloud2.executor.api.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.basic.FileTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Optional;
import java.util.Queue;

/**
 * Represents a process which is registered in the node(s)
 */
public interface ProcessWrapper {

    /**
     * @return The process information this wrapper is based on
     */
    @NotNull
    ProcessInformation getProcessInformation();

    /**
     * Requests a process information of the process this wrapper is based on
     *
     * @return An optional which is present if the process is still registered, started and sent an information update
     */
    @NotNull
    Optional<ProcessInformation> requestProcessInformationUpdate();

    /**
     * Uploads the log of this process to a paste/haste server
     *
     * @return An optional which is present if the process is still registered, started and the log was successfully uploaded
     */
    @NotNull
    Optional<String> uploadLog();

    /**
     * Get the last log lines of the process if the process is still registered and started
     *
     * @return An unmodifiable view of the last log lines the process printed into the console
     */
    @NotNull
    @UnmodifiableView Queue<String> getLastLogLines();

    /**
     * Sends the specified command to the process if the process is still registered and started
     *
     * @param commandLine The command to send to the process
     */
    void sendCommand(@NotNull String commandLine);

    /**
     * Sets the process runtime state. This method is used for starting, stopping, pausing... the process
     * if the provided process state has {@link ProcessState#isRuntimeState()} == {@code true}. Otherwise
     * only the state in the process information will be updated.
     * This method has no effect if the process isn't registered anymore.
     *
     * @param state The new process state
     */
    void setRuntimeState(@NotNull ProcessState state);

    /**
     * Copies the process files to given template if the process is still registered, using the group name
     * of the current wrapper's process information, the template name and the template backend.
     *
     * @param template The template to copy the process files to
     */
    default void copy(@NotNull Template template) {
        this.copy(this.getProcessInformation().getProcessGroup().getName(), template.getName(), template.getBackend());
    }

    /**
     * Copies the process files to the given template based on the given template group name and template
     * name. If the template group is {@code test} and the the template name is {@code zero} the template
     * will get copied to {@code reformcloud/templates/test/zero}.
     *
     * @param templateGroup The group which is the template prefix
     * @param templateName  The name of the template
     */
    default void copy(@NotNull String templateGroup, @NotNull String templateName) {
        this.copy(templateGroup, templateName, FileTemplateBackend.NAME);
    }

    /**
     * Copies the process files to the given template based on the given template group name and template
     * name. If the template group is {@code test} and the the template name is {@code zero} the template
     * will get copied to {@code reformcloud/templates/test/zero}.
     *
     * @param templateGroup   The group which is the template prefix
     * @param templateName    The name of the template
     * @param templateBackend The backend in which the template should get stored
     */
    void copy(@NotNull String templateGroup, @NotNull String templateName, @NotNull String templateBackend);

    /**
     * This method does the same as {@link #getProcessInformation()} but asynchronously.
     *
     * @return The process information this wrapper is based on
     */
    @NotNull
    default Task<ProcessInformation> getProcessInformationAsync() {
        return Task.supply(this::getProcessInformation);
    }

    /**
     * This method does the same as {@link #requestProcessInformationUpdate()} but asynchronously.
     *
     * @return An optional which is present if the process is still registered, started and sent an information update
     */
    @NotNull
    default Task<Optional<ProcessInformation>> requestProcessInformationUpdateAsync() {
        return Task.supply(this::requestProcessInformationUpdate);
    }

    /**
     * This method does the same as {@link #uploadLog()} but asynchronously.
     *
     * @return An optional which is present if the process is still registered, started and the log was successfully uploaded
     */
    @NotNull
    default Task<Optional<String>> uploadLogAsync() {
        return Task.supply(this::uploadLog);
    }

    /**
     * This method does the same as {@link #getLastLogLines()} but asynchronously.
     *
     * @return An unmodifiable view of the last log lines the process printed into the console
     */
    @NotNull
    default Task<Queue<String>> getLastLogLinesAsync() {
        return Task.supply(this::getLastLogLines);
    }

    /**
     * This method does the same as {@link #sendCommand(String)} but asynchronously.
     *
     * @param commandLine The command to send to the process
     * @return A task completed after sending the command or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> sendCommandAsync(@NotNull String commandLine) {
        return Task.supply(() -> {
            this.sendCommand(commandLine);
            return null;
        });
    }

    /**
     * This method does the same as {@link #setRuntimeState(ProcessState)} but asynchronously.
     *
     * @param state The new process state
     * @return A task completed after setting the state or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> setRuntimeStateAsync(@NotNull ProcessState state) {
        return Task.supply(() -> {
            this.setRuntimeState(state);
            return null;
        });
    }

    /**
     * This method does the same as {@link #copy(Template)} but asynchronously.
     *
     * @param template The template to copy the process files to
     * @return A task completed after copying or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> copyAsync(@NotNull Template template) {
        return Task.supply(() -> {
            this.copy(template);
            return null;
        });
    }

    /**
     * This method does the same as {@link #copy(String, String)} but asynchronously.
     *
     * @param templateGroup The group which is the template prefix
     * @param templateName  The name of the template
     * @return A task completed after copying or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> copyAsync(@NotNull String templateGroup, @NotNull String templateName) {
        return Task.supply(() -> {
            this.copy(templateGroup, templateName);
            return null;
        });
    }

    /**
     * This method does the same as {@link #copy(String, String, String)} but asynchronously.
     *
     * @param templateGroup   The group which is the template prefix
     * @param templateName    The name of the template
     * @param templateBackend The backend in which the template should get stored
     * @return A task completed after copying or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> copyAsync(@NotNull String templateGroup, @NotNull String templateName, @NotNull String templateBackend) {
        return Task.supply(() -> {
            this.copy(templateGroup, templateName, templateBackend);
            return null;
        });
    }
}
