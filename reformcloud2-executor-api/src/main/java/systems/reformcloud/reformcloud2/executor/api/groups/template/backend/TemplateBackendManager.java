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
package systems.reformcloud.reformcloud2.executor.api.groups.template.backend;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.basic.FileTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The template backend manager which manages all template backend which registered
 */
public final class TemplateBackendManager {

    /**
     * All loaded template backend
     */
    private static final Collection<TemplateBackend> LOADED = new CopyOnWriteArrayList<>();

    private TemplateBackendManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get a template backend or the default file backend
     *
     * @param name The name of the template which should get loaded
     * @return The template with the given name or the default file backend
     */
    @NotNull
    public static TemplateBackend getOrDefault(@NotNull String name) {
        TemplateBackend backend = Streams.filterToReference(LOADED, e -> e.getName().equalsIgnoreCase(name)).orNothing();
        return backend != null ? backend : new FileTemplateBackend();
    }

    /**
     * Gets a specified template
     *
     * @param name The name of the template which should get loaded
     * @return The template backend with the given name or an empty optional
     */
    @NotNull
    public static Optional<TemplateBackend> get(@NotNull String name) {
        return Optional.ofNullable(Streams.filter(LOADED, e -> e.getName().equalsIgnoreCase(name)));
    }

    /**
     * Registers a new template backend
     *
     * @param templateBackend The template backend which should get registered
     */
    public static void registerBackend(@NotNull TemplateBackend templateBackend) {
        Streams.filterToReference(LOADED, e -> e.getName().equalsIgnoreCase(templateBackend.getName())).ifEmpty(e -> LOADED.add(templateBackend));
    }

    /**
     * Unregisters the specified template backend
     *
     * @param name The name of the backend which should get unregistered
     */
    public static void unregisterBackend(@NotNull String name) {
        Streams.filterToReference(LOADED, e -> e.getName().equalsIgnoreCase(name)).ifPresent(LOADED::remove);
    }

    /**
     * Registers the default template backend
     */
    public static void registerDefaults() {
        registerBackend(new FileTemplateBackend());
    }
}
