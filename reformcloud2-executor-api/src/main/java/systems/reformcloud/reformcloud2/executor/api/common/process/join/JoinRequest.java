package systems.reformcloud.reformcloud2.executor.api.common.process.join;

import java.util.UUID;

public final class JoinRequest {

    JoinRequest(UUID uniqueID, String name) {
        this.uniqueID = uniqueID;
        this.name = name;
    }

    private final UUID uniqueID;

    private final String name;

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }
}
