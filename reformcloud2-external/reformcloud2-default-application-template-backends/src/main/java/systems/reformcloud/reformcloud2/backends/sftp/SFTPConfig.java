package systems.reformcloud.reformcloud2.backends.sftp;

public final class SFTPConfig {

    public SFTPConfig(boolean enabled, String host, int port, String user, String password, String baseDirectory) {
        this.enabled = enabled;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.baseDirectory = baseDirectory;
    }

    private final boolean enabled;

    private final String host;

    private final int port;

    private final String user;

    private final String password;

    private final String baseDirectory;

    public boolean isEnabled() {
        return enabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getBaseDirectory() {
        return baseDirectory.endsWith("/") ? baseDirectory : baseDirectory + "/";
    }
}
