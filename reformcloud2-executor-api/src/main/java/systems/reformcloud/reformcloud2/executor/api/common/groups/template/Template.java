package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Template implements Nameable {

    public static final TypeToken<Template> TYPE = new TypeToken<Template>() {};

    public Template(int priority, String name, boolean global, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version) {
        this.priority = priority;
        this.name = name;
        this.global = global;
        this.backend = backend;
        this.serverNameSplitter = serverNameSplitter;
        this.runtimeConfiguration = runtimeConfiguration;
        this.version = version;
    }

    private int priority;

    private String name;

    private boolean global;

    private String backend;

    private String serverNameSplitter;

    private RuntimeConfiguration runtimeConfiguration;

    private Version version;

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
}
