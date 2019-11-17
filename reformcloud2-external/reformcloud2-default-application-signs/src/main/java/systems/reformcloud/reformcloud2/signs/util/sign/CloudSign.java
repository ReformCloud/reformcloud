package systems.reformcloud.reformcloud2.signs.util.sign;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.UUID;

public class CloudSign {

    public static final TypeToken<CloudSign> TYPE = new TypeToken<CloudSign>() {};

    public CloudSign(String group, CloudLocation location) {
        this.group = group;
        this.location = location;
        this.uniqueID = UUID.randomUUID();
        this.currentTarget = null;
    }

    private final String group;

    private final CloudLocation location;

    private final UUID uniqueID;

    private ProcessInformation currentTarget;

    public String getGroup() {
        return group;
    }

    public CloudLocation getLocation() {
        return location;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public ProcessInformation getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(ProcessInformation currentTarget) {
        this.currentTarget = currentTarget;
    }
}
