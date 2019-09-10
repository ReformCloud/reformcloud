package de.klaro.reformcloud2.executor.api.common.process;

public final class NetworkInfo {

    public NetworkInfo(String host, int port, boolean connected) {
        this.host = host;
        this.port = port;
        this.connected = connected;
    }

    private String host;

    private int port;

    private boolean connected;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
