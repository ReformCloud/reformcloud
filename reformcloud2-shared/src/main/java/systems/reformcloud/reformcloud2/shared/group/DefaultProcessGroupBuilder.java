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
package systems.reformcloud.reformcloud2.shared.group;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Collection;

public abstract class DefaultProcessGroupBuilder implements ProcessGroupBuilder {
    @NotNull
    @Override
    public ProcessGroupBuilder name(@NotNull String name) {
        return null;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder staticGroup(boolean staticGroup) {
        return null;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder lobby(boolean lobby) {
        return null;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder templates(Template... templates) {
        return null;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder templates(@NotNull Collection<Template> templates) {
        return null;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder playerAccessConfig(@NotNull PlayerAccessConfiguration configuration) {
        return null;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder startupConfiguration(@NotNull StartupConfiguration configuration) {
        return null;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder showId(boolean showId) {
        return null;
    }

    @NotNull
    @Override
    public Task<ProcessGroup> createTemporary() {
        return null;
    }
}
