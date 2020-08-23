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
package systems.reformcloud.reformcloud2.shared.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessBuilder;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractProcessBuilder implements ProcessBuilder {

    protected AbstractProcessBuilder() {
    }

    protected String processGroupName;
    protected String node;
    protected String displayName;
    protected String messageOfTheDay;
    protected String targetProcessFactory;

    protected ProcessGroup processGroup;
    protected Template template;
    protected Collection<ProcessInclusion> inclusions = new CopyOnWriteArrayList<>();
    protected JsonConfiguration extra = new JsonConfiguration();
    protected ProcessState initialState = ProcessState.READY;
    protected UUID processUniqueId = UUID.randomUUID();

    protected int memory = -1;
    protected int id = -1;
    protected int maxPlayers = -1;

    @NotNull
    @Override
    public ProcessBuilder targetProcessFactory(@Nullable String targetProcessFactory) {
        this.targetProcessFactory = targetProcessFactory;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder group(@NotNull String processGroupName) {
        this.processGroupName = processGroupName;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder group(@NotNull ProcessGroup processGroup) {
        this.processGroup = processGroup;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder node(@NotNull String node) {
        this.node = node;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder memory(int memory) {
        this.memory = memory;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder id(int id) {
        this.id = id;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder displayName(@NotNull String displayName) {
        this.displayName = displayName;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder messageOfTheDay(@NotNull String messageOfTheDay) {
        this.messageOfTheDay = messageOfTheDay;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder maxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder template(@NotNull Template template) {
        this.template = template;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder inclusions(ProcessInclusion... inclusions) {
        this.inclusions = Arrays.asList(inclusions);
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder inclusions(@NotNull Collection<ProcessInclusion> inclusions) {
        this.inclusions = inclusions;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder extra(@NotNull JsonConfiguration extra) {
        this.extra = extra;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder initialState(@NotNull ProcessState initialState) {
        this.initialState = initialState;
        return this;
    }

    @NotNull
    @Override
    public ProcessBuilder uniqueId(@NotNull UUID uniqueId) {
        this.processUniqueId = uniqueId;
        return this;
    }
}
