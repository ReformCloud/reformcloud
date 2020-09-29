/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.StartupConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DefaultProcessGroupBuilder implements ProcessGroupBuilder {

    protected String name;

    protected boolean staticGroup = false;
    protected boolean lobby = false;
    protected boolean showId = true;

    protected List<Template> templates = new ArrayList<>();
    protected PlayerAccessConfiguration playerAccessConfiguration = PlayerAccessConfiguration.createDefault();
    protected StartupConfiguration startupConfiguration = StartupConfiguration.createDefault();

    @NotNull
    @Override
    public ProcessGroupBuilder name(@NotNull String name) {
        this.name = name;
        return this;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder staticGroup(boolean staticGroup) {
        this.staticGroup = staticGroup;
        return this;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder lobby(boolean lobby) {
        this.lobby = lobby;
        return this;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder templates(Template... templates) {
        this.templates = Arrays.asList(templates);
        return this;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder templates(@NotNull List<Template> templates) {
        this.templates = templates;
        return this;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder playerAccessConfig(@NotNull PlayerAccessConfiguration configuration) {
        this.playerAccessConfiguration = configuration;
        return this;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder startupConfiguration(@NotNull StartupConfiguration configuration) {
        this.startupConfiguration = configuration;
        return this;
    }

    @NotNull
    @Override
    public ProcessGroupBuilder showId(boolean showId) {
        this.showId = showId;
        return this;
    }

    @NotNull
    @Override
    public ProcessGroup createTemporary() {
        Conditions.nonNull(this.name, "Unable to create process group without a name");
        return new ProcessGroup(
                this.name,
                this.showId,
                this.startupConfiguration,
                this.templates,
                this.playerAccessConfiguration,
                this.staticGroup,
                this.lobby
        );
    }
}
