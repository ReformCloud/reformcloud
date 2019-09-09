package de.klaro.reformcloud2.executor.api.common.groups.utils;

public final class Template {

    public Template(int priority, String name, String serverNameSplitter, String downloadURL, String motd,
                    RuntimeConfiguration runtimeConfiguration, Version version) {
        this.priority = priority;
        this.name = name;
        this.serverNameSplitter = serverNameSplitter;
        this.downloadURL = downloadURL;
        this.motd = motd;
        this.runtimeConfiguration = runtimeConfiguration;
        this.version = version;
    }

    private int priority;

    private String name;

    private String serverNameSplitter;

    private String downloadURL;

    private String motd;

    private RuntimeConfiguration runtimeConfiguration;

    private Version version;

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public String getServerNameSplitter() {
        return serverNameSplitter;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public String getMotd() {
        return motd;
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
