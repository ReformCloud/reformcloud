package de.klaro.reformcloud2.executor.api.common.groups.utils;

public final class Template {

    public Template(String name, String downloadURL, RuntimeConfiguration runtimeConfiguration, Version version) {
        this.name = name;
        this.downloadURL = downloadURL;
        this.runtimeConfiguration = runtimeConfiguration;
        this.version = version;
    }

    private String name;

    private String downloadURL;

    private RuntimeConfiguration runtimeConfiguration;

    private Version version;

    public String getName() {
        return name;
    }

    public String getDownloadURL() {
        return downloadURL;
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
