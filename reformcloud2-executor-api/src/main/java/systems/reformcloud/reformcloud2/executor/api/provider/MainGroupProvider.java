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
import systems.reformcloud.reformcloud2.executor.api.builder.MainGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Collection;
import java.util.Optional;

public interface MainGroupProvider {

    @NotNull
    Optional<MainGroup> getMainGroup(@NotNull String name);

    void deleteMainGroup(@NotNull String name);

    @NotNull
    @UnmodifiableView Collection<MainGroup> getMainGroups();

    long getMainGroupCount();

    @NotNull
    @UnmodifiableView Collection<String> getMainGroupNames();

    @NotNull
    MainGroupBuilder createMainGroup(@NotNull String name);

    @NotNull
    default Task<Optional<MainGroup>> getMainGroupAsync(@NotNull String name) {
        return Task.supply(() -> this.getMainGroup(name));
    }

    @NotNull
    default Task<Void> deleteMainGroupAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.deleteMainGroup(name);
            return null;
        });
    }

    @NotNull
    default Task<Collection<MainGroup>> getMainGroupsAsync() {
        return Task.supply(this::getMainGroups);
    }

    @NotNull
    default Task<Long> getMainGroupCountAsync() {
        return Task.supply(this::getMainGroupCount);
    }

    @NotNull
    default Task<Collection<String>> getMainGroupNamesAsync() {
        return Task.supply(this::getMainGroupNames);
    }
}
