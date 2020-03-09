package systems.reformcloud.reformcloud2.executor.api.common.process.join;

import java.util.UUID;

public final class JoinRequest {

    public JoinRequest(UUID uniqueID, String name, String currentServer) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.currentServer = currentServer;
    }

    private final UUID uniqueID;

    private final String name;

    private String currentServer;

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }
}
