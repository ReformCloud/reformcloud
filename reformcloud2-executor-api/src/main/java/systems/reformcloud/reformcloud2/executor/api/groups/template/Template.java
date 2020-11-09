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
package systems.reformcloud.reformcloud2.executor.api.groups.template;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonDataHolder;
import systems.reformcloud.reformcloud2.executor.api.functional.Sorted;
import systems.reformcloud.reformcloud2.executor.api.groups.template.builder.TemplateBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.InclusionHolder;
import systems.reformcloud.reformcloud2.executor.api.groups.template.runtime.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.version.Version;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.utility.name.ReNameable;

public interface Template extends ReNameable, JsonDataHolder<Template>, InclusionHolder, SerializableObject, Sorted<Template>, Cloneable {

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    static TemplateBuilder builder(@NotNull String name, @NotNull Version version) {
        return TemplateBuilder.newBuilder(name, version);
    }

    int getPriority();

    void setPriority(int priority);

    boolean isGlobal();

    void setGlobal(boolean global);

    boolean isAutoCopyOnClose();

    void setAutoCopyOnClose(boolean autoCopyOnClose);

    @NotNull
    String getBackend();

    void setBackend(@NotNull String backend);

    @Nullable
    String getServerNameSplitter();

    void setServerNameSplitter(@Nullable String serverNameSplitter);

    @NotNull
    RuntimeConfiguration getRuntimeConfiguration();

    void setRuntimeConfiguration(@NotNull RuntimeConfiguration runtimeConfiguration);

    @NotNull
    Version getVersion();

    void setVersion(@NotNull Version version);

    @NotNull
    Template clone();
}
