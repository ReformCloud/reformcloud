package de.klaro.reformcloud2.executor.client.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ClientConnectionConfig {

    static final Path PATH = Paths.get("reformcloud/connection.json");

    ClientConnectionConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private String host;

    private int port;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
