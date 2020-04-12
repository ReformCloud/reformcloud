package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The template backend manager which manages all template backend which registered
 */
public final class TemplateBackendManager {

    private TemplateBackendManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * All loaded template backend
     */
    private static final Collection<TemplateBackend> LOADED = new CopyOnWriteArrayList<>();

    /**
     * Get a template backend or the default file backend
     *
     * @param name The name of the template which should get loaded
     * @return The template with the given name or the default file backend
     */
    @NotNull
    public static TemplateBackend getOrDefault(@NotNull String name) {
        TemplateBackend backend = Streams.filterToReference(LOADED, e -> e.getName().equalsIgnoreCase(name)).orNothing();
        return backend != null ? backend : new FileBackend();
    }

    /**
     * Gets a specified template
     *
     * @param name The name of the template which should get loaded
     * @return The template backend with the given name or {@code null}
     */
    @Nullable
    public static TemplateBackend get(@NotNull String name) {
        return Streams.filterToReference(LOADED, e -> e.getName().equalsIgnoreCase(name)).orNothing();
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
        registerBackend(new FileBackend());
    }
}
