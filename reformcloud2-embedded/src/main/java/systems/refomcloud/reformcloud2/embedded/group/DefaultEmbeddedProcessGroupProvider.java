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
package systems.refomcloud.reformcloud2.embedded.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.provider.ProcessGroupProvider;

import java.util.Collection;
import java.util.Optional;

public class DefaultEmbeddedProcessGroupProvider implements ProcessGroupProvider {

    @NotNull
    @Override
    public Optional<ProcessGroup> getProcessGroup(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void deleteProcessGroup(@NotNull String name) {

    }

    @Override
    public void updateProcessGroup(@NotNull ProcessGroup processGroup) {

    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessGroup> getProcessGroups() {
        return null;
    }

    @Override
    public long getProcessGroupCount() {
        return 0;
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getProcessGroupNames() {
        return null;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder createProcessGroup(@NotNull String name) {
        return new DefaultEmbeddedProcessGroupBuilder().name(name);
    }
}
