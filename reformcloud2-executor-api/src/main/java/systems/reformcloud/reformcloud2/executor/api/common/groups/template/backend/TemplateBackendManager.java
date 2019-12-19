package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend;

import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class TemplateBackendManager {

    private TemplateBackendManager() {
        throw new UnsupportedOperationException();
    }

    private static final Collection<TemplateBackend> BACKENDS = new ArrayList<>();

    private static final Collection<TemplateBackend> DEFAULTS = Collections.singletonList(new FileBackend());

    public static TemplateBackend getOrDefault(String name) {
        TemplateBackend backend = Links.filterToReference(BACKENDS, e -> e.getName().equalsIgnoreCase(name)).orNothing();
        return backend != null ? backend : new FileBackend();
    }

    public static void registerBackend(TemplateBackend templateBackend) {
        TemplateBackend backend = Links.filterToReference(BACKENDS, e -> e.getName().equalsIgnoreCase(
                templateBackend.getName()
        )).orNothing();
        if (backend == null) {
            BACKENDS.add(templateBackend);
        }
    }

    public static void unregisterBackend(String name) {
        Links.filterToReference(BACKENDS, e -> e.getName().equalsIgnoreCase(name)).ifPresent(BACKENDS::remove);
    }

    public static void registerDefaults() {
        DEFAULTS.forEach(TemplateBackendManager::registerBackend);
    }
}
