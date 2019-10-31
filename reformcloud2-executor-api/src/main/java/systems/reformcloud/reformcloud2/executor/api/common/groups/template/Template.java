package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import com.google.gson.reflect.TypeToken;

public final class Template {

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

    public String getName() {
        return name;
    }

    public boolean isGlobal() {
        return global;
    }

    public String getBackend() {
        return backend;
    }

    public String getServerNameSplitter() {
        return serverNameSplitter;
    }

    public RuntimeConfiguration getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    public Version getVersion() {
        return version;
    }

    public boolean isServer() {
        return version.getId() == 1 || version.getId() == 3;
    }
}
