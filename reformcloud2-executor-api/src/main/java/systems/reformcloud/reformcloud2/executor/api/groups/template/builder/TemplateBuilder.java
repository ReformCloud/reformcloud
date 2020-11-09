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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.groups.template.runtime.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.version.Version;

import java.util.Collection;

public interface TemplateBuilder {

    String FILE_BACKEND = "FILE";
    String DEFAULT_SERVER_NAME_SPLITTER = "-";
    RuntimeConfiguration DEFAULT_RUNTIME_CONFIGURATION = RuntimeConfiguration.configuration(256, 512, 0);

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    static TemplateBuilder newBuilder(@NotNull String name, @NotNull Version version) {
        return new DefaultTemplateBuilder(name, version);
    }

    @NotNull
    TemplateBuilder name(@NotNull String name);

    @NotNull
    String name();

    @NotNull
    TemplateBuilder priority(int priority);

    int priority();

    @NotNull
    TemplateBuilder global(boolean global);

    boolean global();

    @NotNull
    TemplateBuilder autoCopyOnClose(boolean autoCopyOnClose);

    boolean autoCopyOnClose();

    @NotNull
    TemplateBuilder backend(@NotNull String backend);

    @NotNull
    String backend();

    @NotNull
    TemplateBuilder serverNameSplitter(@Nullable String serverNameSplitter);

    @NotNull
    String serverNameSplitter();

    @NotNull
    TemplateBuilder runtimeConfiguration(@NotNull RuntimeConfiguration runtimeConfiguration);

    @NotNull
    RuntimeConfiguration runtimeConfiguration();

    @NotNull
    TemplateBuilder version(@NotNull Version version);

    @NotNull
    Version version();

    @NotNull
    TemplateBuilder templateInclusions(@NotNull Collection<Inclusion> templateInclusions);

    @NotNull
    Collection<Inclusion> templateInclusions();

    @NotNull
    TemplateBuilder pathInclusions(@NotNull Collection<Inclusion> pathInclusions);

    @NotNull
    Collection<Inclusion> pathInclusions();

    @NotNull
    TemplateBuilder jsonData(@NotNull JsonConfiguration data);

    @NotNull
    JsonConfiguration jsonData();

    @NotNull
    Template build();
}
