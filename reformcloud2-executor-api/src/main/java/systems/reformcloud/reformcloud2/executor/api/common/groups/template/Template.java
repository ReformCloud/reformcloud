package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

public final class Template {

    public Template(int priority, String name, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version) {
        this.priority = priority;
        this.name = name;
        this.backend = backend;
        this.serverNameSplitter = serverNameSplitter;
        this.runtimeConfiguration = runtimeConfiguration;
        this.version = version;
    }

    private int priority;

    private String name;

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
