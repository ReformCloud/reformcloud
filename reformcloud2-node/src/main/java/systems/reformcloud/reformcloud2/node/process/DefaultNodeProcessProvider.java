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
package systems.reformcloud.reformcloud2.node.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.ProcessProvider;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultNodeProcessProvider implements ProcessProvider {

    private final Collection<DefaultNodeProcessWrapper> processes = new CopyOnWriteArrayList<>();

    @NotNull
    @Override
    public Optional<ProcessWrapper> getProcessByName(@NotNull String name) {
        return Optional.ofNullable(Streams.filter(this.processes, process -> process.getProcessInformation().getProcessDetail().getName().equals(name)));
    }

    @NotNull
    @Override
    public Optional<ProcessWrapper> getProcessByUniqueId(@NotNull UUID uniqueId) {
        return Optional.ofNullable(Streams.filter(this.processes, process -> process.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uniqueId)));
    }

    @NotNull
    @Override
    public ProcessBuilder createProcess() {
        return new DefaultNodeProcessBuilder();
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessInformation> getProcesses() {
        return Streams.map(this.processes, ProcessWrapper::getProcessInformation);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessInformation> getProcessesByProcessGroup(@NotNull String processGroup) {
        return Streams.newCollection(
                this.processes,
                process -> process.getProcessInformation().getProcessGroup().getName().equals(processGroup),
                ProcessWrapper::getProcessInformation
        );
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessInformation> getProcessesByMainGroup(@NotNull String mainGroup) {
        return ExecutorAPI.getInstance().getMainGroupProvider().getMainGroup(mainGroup)
                .map(group -> {
                    Collection<ProcessInformation> result = new ArrayList<>();
                    for (String subGroup : group.getSubGroups()) {
                        result.addAll(this.getProcessesByProcessGroup(subGroup));
                    }

                    return result;
                }).orElse(Collections.emptyList());
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessInformation> getProcessesByVersion(@NotNull Version version) {
        return Streams.newCollection(
                this.processes,
                process -> process.getProcessInformation().getProcessDetail().getTemplate().getVersion() == version,
                ProcessWrapper::getProcessInformation
        );
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<UUID> getProcessUniqueIds() {
        return Streams.map(this.processes, processWrapper -> processWrapper.getProcessInformation().getProcessDetail().getProcessUniqueID());
    }

    @Override
    public long getProcessCount() {
        return this.processes.size();
    }

    @Override
    public long getProcessCount(@NotNull String processGroup) {
        return Streams.count(this.processes, process -> process.getProcessInformation().getProcessGroup().getName().equals(processGroup));
    }

    @Override
    public void updateProcessInformation(@NotNull ProcessInformation processInformation) {
        DefaultNodeProcessWrapper old = Streams.filter(
                this.processes,
                process -> process.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())
        );
        if (old != null) {
            old.setProcessInformation(processInformation);
        }
    }
}
