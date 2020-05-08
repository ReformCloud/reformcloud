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
package systems.reformcloud.reformcloud2.executor.api.common.process.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.*;

public final class ProcessConfigurationBuilder {

    @NotNull
    public static ProcessConfigurationBuilder newBuilder(@NotNull String processGroupName) {
        ProcessGroup group = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(processGroupName);
        Conditions.nonNull(group, "Unable to find group with name " + processGroupName);
        return newBuilder(group);
    }

    @NotNull
    public static ProcessConfigurationBuilder newBuilder(@NotNull ProcessGroup processGroup) {
        return new ProcessConfigurationBuilder(processGroup);
    }

    /* =================================== */

    /**
     * Use {@link #newBuilder(String)} )} instead
     *
     * @param processGroup The group for which the builder is made
     */
    private ProcessConfigurationBuilder(@NotNull ProcessGroup processGroup) {
        this.base = processGroup;
        this.maxPlayers = processGroup.getPlayerAccessConfiguration().getMaxPlayers();
    }

    private final ProcessGroup base;

    private UUID uniqueID = UUID.randomUUID();

    private String displayName;

    private Integer maxMemory;

    private Integer port;

    private Template template;

    private JsonConfiguration extra = new JsonConfiguration();

    private int id = -1;

    private int maxPlayers;

    private ProcessState initialState = ProcessState.READY;

    private Collection<ProcessInclusion> inclusions = new ArrayList<>();

    @NotNull
    public ProcessConfigurationBuilder uniqueId(@NotNull UUID uniqueID) {
        Conditions.nonNull(uniqueID, "Unable to set null unique id");

        this.uniqueID = uniqueID;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder id(int processId) {
        Conditions.isTrue(processId > 0, "Unable to start process with id smaller than 0");

        this.id = processId;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder displayName(@NotNull String displayName) {
        Conditions.nonNull(displayName, "Unable to set null display name");

        this.displayName = displayName;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder maxMemory(int maxMemory) {
        Conditions.isTrue(maxMemory > 100, "Unable to start process with memory lower than 100MB");

        this.maxMemory = maxMemory;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder port(int port) {
        Conditions.isTrue(port > 0, "Unable to use port smaller than 0");

        this.port = port;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder template(@NotNull Template template) {
        Conditions.nonNull(template, "Unable to use null template");

        this.template = template;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder template(@NotNull String template) {
        Conditions.nonNull(template, "Unable to use null template");

        this.template = Streams.filterToReference(this.base.getTemplates(), e -> e.getName().equals(template)).orElse(this.randomTemplate());
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder extra(@NotNull JsonConfiguration extra) {
        Conditions.nonNull(extra, "Unable to set null extra data");

        this.extra = extra;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder maxPlayers(int maxPlayers) {
        Conditions.isTrue(maxPlayers > 0, "Unable to set max players to a value lower or equal to 0");

        this.maxPlayers = maxPlayers;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder inclusions(@NotNull Collection<ProcessInclusion> inclusions) {
        Conditions.nonNull(inclusions, "Unable to set null inclusions");

        this.inclusions = inclusions;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder initialState(@NotNull ProcessState initialState) {
        Conditions.nonNull(initialState, "Unable to set initial state");

        this.initialState = initialState;
        return this;
    }

    @NotNull
    public ProcessConfigurationBuilder inclusion(@NotNull ProcessInclusion inclusion) {
        Conditions.nonNull(inclusion, "Unable to add null inclusion");

        this.inclusions.add(inclusion);
        return this;
    }

    @NotNull
    public ProcessConfiguration build() {
        return new ProcessConfiguration(
                this.base,
                this.uniqueID,
                this.displayName,
                this.maxMemory,
                this.port,
                this.template,
                this.extra,
                this.id,
                this.maxPlayers,
                this.inclusions,
                this.initialState
        );
    }

    private Template randomTemplate() {
        if (this.base.getTemplates().isEmpty()) {
            return new Template(0, "default", false, FileTemplateBackend.NAME, "#", new RuntimeConfiguration(
                    512, new ArrayList<>(), new HashMap<>()
            ), Version.PAPER_1_8_8);
        }

        if (this.base.getTemplates().size() == 1) {
            return this.base.getTemplates().get(0);
        }

        return this.base.getTemplates().get(new Random().nextInt(this.base.getTemplates().size()));
    }
}
