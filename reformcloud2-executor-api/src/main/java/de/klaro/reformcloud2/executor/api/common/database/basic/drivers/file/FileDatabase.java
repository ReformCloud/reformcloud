package de.klaro.reformcloud2.executor.api.common.database.basic.drivers.file;

import de.klaro.reformcloud2.executor.api.common.database.Database;

public final class FileDatabase extends Database<Void> { //Void because file database will not give any database back

    @Override
    public void connect(String host, int port, String userName, String password, String table) {
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
    public Void get() {
        return null;
    }
}
