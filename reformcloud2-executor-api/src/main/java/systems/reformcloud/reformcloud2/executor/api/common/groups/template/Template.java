package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public final class Template implements Nameable {

    public static final TypeToken<Template> TYPE = new TypeToken<Template>() {
    };

    public Template(int priority, String name, boolean global, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version) {
        this(priority, name, global, backend, serverNameSplitter, runtimeConfiguration, version, new ArrayList<>(), new ArrayList<>());
    }

    public Template(int priority, String name, boolean global, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version, Collection<Inclusion> templateInclusions,
                    Collection<Inclusion> pathInclusions) {
        this(priority, name, global, false, backend, serverNameSplitter, runtimeConfiguration, version, templateInclusions, pathInclusions);
    }

    public Template(int priority, String name, boolean global, boolean autoCopyOnStop, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version, Collection<Inclusion> templateInclusions,
                    Collection<Inclusion> pathInclusions) {
        this.priority = priority;
        this.name = name;
        this.global = global;
        this.autoCopyOnStop = autoCopyOnStop;
        this.backend = backend;
        this.serverNameSplitter = serverNameSplitter;
        this.runtimeConfiguration = runtimeConfiguration;
        this.version = version;
        this.templateInclusions = templateInclusions;
        this.pathInclusions = pathInclusions;
    }

    private final int priority;

    private final String name;

    private final boolean global;

    private final boolean autoCopyOnStop;

    private final String backend;

    private final String serverNameSplitter;

    private final RuntimeConfiguration runtimeConfiguration;

    private final Version version;

    private final Collection<Inclusion> templateInclusions;

    private final Collection<Inclusion> pathInclusions;

    public int getPriority() {
        return priority;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public boolean isGlobal() {
        return global;
    }

    public boolean isAutoCopyOnStop() {
        return autoCopyOnStop;
    }

    @Nonnull
    public String getBackend() {
        return backend;
    }

    @Nullable
    public String getServerNameSplitter() {
        return serverNameSplitter;
    }

    @Nonnull
    public RuntimeConfiguration getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    @Nonnull
    public Version getVersion() {
        return version;
    }

    public boolean isServer() {
        return version.isServer();
    }

    /* Needs null check, added in version 2.0.4 */
    public Collection<Inclusion> getTemplateInclusions() {
        return templateInclusions == null ? new ArrayList<>() : templateInclusions;
    }

    /* Needs null check, added in version 2.0.4 */
    public Collection<Inclusion> getPathInclusions() {
        return pathInclusions == null ? new ArrayList<>() : pathInclusions;
    }

    public Collection<Duo<String, String>> getPathInclusionsOfType(@Nonnull Inclusion.InclusionLoadType type) {
        return this.getPathInclusions()
                .stream()
                .filter(e -> e.getInclusionLoadType().equals(type))
                .filter(e -> e.getBackend() != null && e.getKey() != null)
                .map(e -> new Duo<>(e.getKey(), e.getBackend()))
                .collect(Collectors.toList());
    }

    public Collection<Duo<String, String>> getTemplateInclusionsOfType(@Nonnull Inclusion.InclusionLoadType type) {
        return this.getTemplateInclusions()
                .stream()
                .filter(e -> e.getInclusionLoadType().equals(type))
                .filter(e -> e.getBackend() != null && e.getKey() != null)
                .map(e -> new Duo<>(e.getKey(), e.getBackend()))
                .collect(Collectors.toList());
    }
}
