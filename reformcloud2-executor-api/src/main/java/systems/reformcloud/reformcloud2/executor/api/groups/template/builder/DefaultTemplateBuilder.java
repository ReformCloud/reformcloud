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
package systems.reformcloud.reformcloud2.executor.api.groups.template.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.groups.template.runtime.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.version.Version;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultTemplateBuilder implements TemplateBuilder {

    private String name;
    private Version version;
    private int priority;
    private boolean global;
    private boolean autoCopyOnClose;
    private String backend = FILE_BACKEND;
    private String serverNameSplitter = DEFAULT_SERVER_NAME_SPLITTER;
    private RuntimeConfiguration runtimeConfiguration = DEFAULT_RUNTIME_CONFIGURATION;
    private Collection<Inclusion> templateInclusions = new ArrayList<>();
    private Collection<Inclusion> pathInclusions = new ArrayList<>();
    private JsonConfiguration jsonData = JsonConfiguration.newJsonConfiguration();

    protected DefaultTemplateBuilder(String name, Version version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public @NotNull TemplateBuilder name(@NotNull String name) {
        this.name = name;
        return this;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public @NotNull TemplateBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public @NotNull TemplateBuilder global(boolean global) {
        this.global = global;
        return this;
    }

    @Override
    public boolean global() {
        return this.global;
    }

    @Override
    public @NotNull TemplateBuilder autoCopyOnClose(boolean autoCopyOnClose) {
        this.autoCopyOnClose = autoCopyOnClose;
        return this;
    }

    @Override
    public boolean autoCopyOnClose() {
        return this.autoCopyOnClose;
    }

    @Override
    public @NotNull TemplateBuilder backend(@NotNull String backend) {
        this.backend = backend;
        return this;
    }

    @Override
    public @NotNull String backend() {
        return this.backend;
    }

    @Override
    public @NotNull TemplateBuilder serverNameSplitter(@Nullable String serverNameSplitter) {
        this.serverNameSplitter = serverNameSplitter == null ? DEFAULT_SERVER_NAME_SPLITTER : serverNameSplitter;
        return this;
    }

    @Override
    public @NotNull String serverNameSplitter() {
        return this.serverNameSplitter;
    }

    @Override
    public @NotNull TemplateBuilder runtimeConfiguration(@NotNull RuntimeConfiguration runtimeConfiguration) {
        this.runtimeConfiguration = runtimeConfiguration;
        return this;
    }

    @Override
    public @NotNull RuntimeConfiguration runtimeConfiguration() {
        return this.runtimeConfiguration;
    }

    @Override
    public @NotNull TemplateBuilder version(@NotNull Version version) {
        this.version = version;
        return this;
    }

    @Override
    public @NotNull Version version() {
        return this.version;
    }

    @Override
    public @NotNull TemplateBuilder templateInclusions(@NotNull Collection<Inclusion> templateInclusions) {
        this.templateInclusions = templateInclusions;
        return this;
    }

    @Override
    public @NotNull Collection<Inclusion> templateInclusions() {
        return this.templateInclusions;
    }

    @Override
    public @NotNull TemplateBuilder pathInclusions(@NotNull Collection<Inclusion> pathInclusions) {
        this.pathInclusions = pathInclusions;
        return this;
    }

    @Override
    public @NotNull Collection<Inclusion> pathInclusions() {
        return this.pathInclusions;
    }

    @Override
    public @NotNull TemplateBuilder jsonData(@NotNull JsonConfiguration data) {
        this.jsonData = data;
        return this;
    }

    @Override
    public @NotNull JsonConfiguration jsonData() {
        return this.jsonData;
    }

    @Override
    public @NotNull Template build() {
        return new DefaultTemplate(this.priority, this.global, this.autoCopyOnClose, this.backend, this.serverNameSplitter, this.runtimeConfiguration,
            this.version, this.templateInclusions, this.pathInclusions, this.name, this.jsonData);
    }
}
