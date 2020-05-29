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
package systems.reformcloud.reformcloud2.node.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.provider.ProcessGroupProvider;
import systems.reformcloud.reformcloud2.executor.api.registry.io.FileRegistry;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.shared.registry.io.DefaultFileRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class DefaultNodeProcessGroupProvider implements ProcessGroupProvider {

    public DefaultNodeProcessGroupProvider(@NotNull String registryFolder) {
        this.fileRegistry = new DefaultFileRegistry(registryFolder);
        this.processGroups = this.fileRegistry.readKeys(e -> e.get("key", ProcessGroup.TYPE));
    }

    private final Collection<ProcessGroup> processGroups;
    private final FileRegistry fileRegistry;

    @NotNull
    @Override
    public Optional<ProcessGroup> getProcessGroup(@NotNull String name) {
        return Optional.ofNullable(Streams.filter(this.processGroups, e -> e.getName().equals(name)));
    }

    @Override
    public void deleteProcessGroup(@NotNull String name) {
        this.getProcessGroup(name).ifPresent(group -> {
            this.processGroups.remove(group);
            this.fileRegistry.deleteKey(group.getName());
        });
    }

    @Override
    public void updateProcessGroup(@NotNull ProcessGroup processGroup) {
        this.getProcessGroup(processGroup.getName()).ifPresent(group -> {
            this.processGroups.remove(group);
            this.processGroups.add(processGroup);

            this.fileRegistry.updateKey(processGroup.getName(), processGroup);
        });
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessGroup> getProcessGroups() {
        return Collections.unmodifiableCollection(this.processGroups);
    }

    @Override
    public long getProcessGroupCount() {
        return this.processGroups.size();
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getProcessGroupNames() {
        return Streams.map(this.processGroups, ProcessGroup::getName);
    }

    @NotNull
    @Override
    public ProcessGroupBuilder createProcessGroup(@NotNull String name) {
        return new NodeProcessGroupBuilder();
    }

    void addProcessGroup(@NotNull ProcessGroup processGroup) {
        this.processGroups.add(processGroup);
        this.fileRegistry.createKey(processGroup.getName(), processGroup);
    }
}
