package de.klaro.reformcloud2.executor.api.common.groups.utils;

public enum StartupEnvironment {

    JAVA_RUNTIME("java", true);

    StartupEnvironment(String command, boolean supported) {
        this.command = command;
        this.supported = supported;
    }

    private String command;

    private boolean supported;

    public String getCommand() {
        return command;
    }

    public boolean isSupported() {
        return supported;
    }
}
