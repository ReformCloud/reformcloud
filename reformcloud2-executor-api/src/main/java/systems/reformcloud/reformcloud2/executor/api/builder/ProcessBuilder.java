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
package systems.reformcloud.reformcloud2.executor.api.builder;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;

import java.util.Collection;
import java.util.UUID;

public interface ProcessBuilder {

    @NotNull
    ProcessBuilder group(@NotNull String processGroupName);

    @NotNull
    ProcessBuilder group(@NotNull ProcessGroup processGroup);

    @NotNull
    ProcessBuilder node(@NotNull String node);

    @NotNull
    ProcessBuilder memory(int memory);

    @NotNull
    ProcessBuilder id(int id);

    @NotNull
    ProcessBuilder displayName(@NotNull String displayName);

    @NotNull
    ProcessBuilder messageOfTheDay(@NotNull String messageOfTheDay);

    @NotNull
    ProcessBuilder maxPlayers(int maxPlayers);

    @NotNull
    ProcessBuilder template(@NotNull Template template);

    @NotNull
    ProcessBuilder inclusions(@NotNull ProcessInclusion... inclusions);

    @NotNull
    ProcessBuilder inclusions(@NotNull Collection<ProcessInclusion> inclusions);

    @NotNull
    ProcessBuilder extra(@NotNull JsonConfiguration extra);

    @NotNull
    ProcessBuilder initialState(@NotNull ProcessState initialState);

    @NotNull
    ProcessBuilder uniqueId(@NotNull UUID uniqueId);

    @NotNull
    Task<ProcessWrapper> prepare();
}
