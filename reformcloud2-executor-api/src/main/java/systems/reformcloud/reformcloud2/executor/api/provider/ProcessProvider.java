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
package systems.reformcloud.reformcloud2.executor.api.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProcessProvider {

    @NotNull
    Optional<ProcessWrapper> getProcessByName(@NotNull String name);

    @NotNull
    Optional<ProcessWrapper> getProcessByUniqueId(@NotNull UUID uniqueId);

    @NotNull
    ProcessBuilder createProcess();

    @NotNull
    @UnmodifiableView Collection<ProcessInformation> getProcesses();

    @NotNull
    @UnmodifiableView Collection<ProcessInformation> getProcessesByProcessGroup(@NotNull String processGroup);

    @NotNull
    @UnmodifiableView Collection<ProcessInformation> getProcessesByMainGroup(@NotNull String mainGroup);

    @NotNull
    @UnmodifiableView Collection<ProcessInformation> getProcessesByVersion(@NotNull Version version);

    @NotNull
    @UnmodifiableView Collection<UUID> getProcessUniqueIds();

    long getProcessCount();

    long getProcessCount(@NotNull String processGroup);

    void updateProcessInformation(@NotNull ProcessInformation processInformation);

    @NotNull
    default Task<Optional<ProcessWrapper>> getProcessByNameAsync(@NotNull String name) {
        return Task.supply(() -> this.getProcessByName(name));
    }

    @NotNull
    default Task<Optional<ProcessWrapper>> getProcessByUniqueIdAsync(@NotNull UUID processUniqueId) {
        return Task.supply(() -> this.getProcessByUniqueId(processUniqueId));
    }

    @NotNull
    default Task<ProcessBuilder> createProcessAsync() {
        return Task.supply(this::createProcess);
    }

    @NotNull
    default Task<Collection<ProcessInformation>> getProcessesAsync() {
        return Task.supply(this::getProcesses);
    }

    @NotNull
    default Task<Collection<ProcessInformation>> getProcessesByProcessGroupAsync(@NotNull String processGroup) {
        return Task.supply(() -> this.getProcessesByProcessGroup(processGroup));
    }

    @NotNull
    default Task<Collection<ProcessInformation>> getProcessesByMainGroupAsync(@NotNull String mainGroup) {
        return Task.supply(() -> this.getProcessesByMainGroup(mainGroup));
    }

    @NotNull
    default Task<Collection<ProcessInformation>> getProcessesByVersionAsync(@NotNull Version version) {
        return Task.supply(() -> this.getProcessesByVersion(version));
    }

    @NotNull
    default Task<Collection<UUID>> getProcessUniqueIdsAsync() {
        return Task.supply(this::getProcessUniqueIds);
    }

    @NotNull
    default Task<Long> getProcessCountAsync() {
        return Task.supply(this::getProcessCount);
    }

    @NotNull
    default Task<Long> getProcessCountAsync(@NotNull String processGroup) {
        return Task.supply(() -> this.getProcessCount(processGroup));
    }

    @NotNull
    default Task<Void> updateProcessInformationAsync(@NotNull ProcessInformation processInformation) {
        return Task.supply(() -> {
            this.updateProcessInformation(processInformation);
            return null;
        });
    }
}
