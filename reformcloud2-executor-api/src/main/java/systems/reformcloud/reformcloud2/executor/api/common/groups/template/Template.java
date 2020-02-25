package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public final class Template implements Nameable {

    public static final TypeToken<Template> TYPE = new TypeToken<Template>() {};

    public Template(int priority, String name, boolean global, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version) {
        this(priority, name, global, backend, serverNameSplitter, runtimeConfiguration, version, new ArrayList<>(), new ArrayList<>());
    }

    public Template(int priority, String name, boolean global, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version, Collection<Inclusion> templateInclusions,
                    Collection<Inclusion> pathInclusions) {
        this.priority = priority;
        this.name = name;
        this.global = global;
        this.backend = backend;
        this.serverNameSplitter = serverNameSplitter;
        this.runtimeConfiguration = runtimeConfiguration;
        this.version = version;
        this.templateInclusions = templateInclusions;
        this.pathInclusions = pathInclusions;
    }

    private int priority;

    private String name;

    private boolean global;

    private String backend;

    private String serverNameSplitter;

    private RuntimeConfiguration runtimeConfiguration;

    private Version version;

    private Collection<Inclusion> templateInclusions;

    private Collection<Inclusion> pathInclusions;

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

    public Collection<Double<String, String>> getPathInclusionsOfType(@Nonnull Inclusion.InclusionLoadType type) {
        return this.getPathInclusions()
                .stream()
                .filter(e -> e.getInclusionLoadType().equals(type))
                .filter(e -> e.getBackend() != null && e.getKey() != null)
                .map(e -> new Double<>(e.getKey(), e.getBackend()))
                .collect(Collectors.toList());
    }

    public Collection<Double<String, String>> getTemplateInclusionsOfType(@Nonnull Inclusion.InclusionLoadType type) {
        return this.getTemplateInclusions()
                .stream()
                .filter(e -> e.getInclusionLoadType().equals(type))
                .filter(e -> e.getBackend() != null && e.getKey() != null)
                .map(e -> new Double<>(e.getKey(), e.getBackend()))
                .collect(Collectors.toList());
    }
}
