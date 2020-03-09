package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend;

import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class TemplateBackendManager {

    private TemplateBackendManager() {
        throw new UnsupportedOperationException();
    }

    private static final Collection<TemplateBackend> BACKENDS = new ArrayList<>();

    private static final Collection<TemplateBackend> DEFAULTS = Collections.singletonList(new FileBackend());

    @Nonnull
    public static TemplateBackend getOrDefault(String name) {
        TemplateBackend backend = Streams.filterToReference(BACKENDS, e -> e.getName().equalsIgnoreCase(name)).orNothing();
        return backend != null ? backend : new FileBackend();
    }

    @Nullable
    public static TemplateBackend get(String name) {
        return Streams.filterToReference(BACKENDS, e -> e.getName().equalsIgnoreCase(name)).orNothing();
    }

    public static void registerBackend(TemplateBackend templateBackend) {
        TemplateBackend backend = Streams.filterToReference(BACKENDS, e -> e.getName().equalsIgnoreCase(
                templateBackend.getName()
        )).orNothing();
        if (backend == null) {
            BACKENDS.add(templateBackend);
        }
    }

    public static void unregisterBackend(String name) {
        Streams.filterToReference(BACKENDS, e -> e.getName().equalsIgnoreCase(name)).ifPresent(BACKENDS::remove);
    }

    public static void registerDefaults() {
        DEFAULTS.forEach(TemplateBackendManager::registerBackend);
    }
}
