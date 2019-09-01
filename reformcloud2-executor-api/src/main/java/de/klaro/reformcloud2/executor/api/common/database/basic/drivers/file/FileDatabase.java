package de.klaro.reformcloud2.executor.api.common.database.basic.drivers.file;

import de.klaro.reformcloud2.executor.api.common.database.Database;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileDatabase extends Database<Path> {

    private String table;

    @Override
    public void connect(String host, int port, String userName, String password, String table) {
        this.table = table;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void reconnect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public Path get() {
        return Paths.get(table);
    }
}
