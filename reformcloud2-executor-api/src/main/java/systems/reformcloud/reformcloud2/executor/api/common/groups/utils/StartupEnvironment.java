package systems.reformcloud.reformcloud2.executor.api.common.groups.utils;

@Deprecated // TODO: remove
public enum StartupEnvironment {

    JAVA_RUNTIME("java", true);

    StartupEnvironment(String command, boolean supported) {
        this.command = command;
        this.supported = supported;
    }

    private final String command;

    private final boolean supported;

    public String getCommand() {
        return command;
    }

    public boolean isSupported() {
        return supported;
    }
}
