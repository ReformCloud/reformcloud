package systems.reformcloud.reformcloud2.executor.api.common.node;

import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class NodeInformation {

    public static final TypeToken<NodeInformation> TYPE = new TypeToken<NodeInformation>() {};

    private final String name;

    private final UUID nodeUniqueID;

    private final long startupTime;

    private long usedMemory;

    private final long maxMemory;

    private final Collection<NodeProcess> startedProcesses;

    public NodeInformation(String name, UUID nodeUniqueID, long startupTime,
                           long usedMemory, long maxMemory, Collection<NodeProcess> startedProcesses) {
        this.name = name;
        this.nodeUniqueID = nodeUniqueID;
        this.startupTime = startupTime;
        this.usedMemory = usedMemory;
        this.maxMemory = maxMemory;
        this.startedProcesses = startedProcesses;
    }

    public String getName() {
        return name;
    }

    public UUID getNodeUniqueID() {
        return nodeUniqueID;
    }

    public long getStartupTime() {
        return startupTime;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public Collection<NodeProcess> getStartedProcesses() {
        return startedProcesses;
    }

    public void addUsedMemory(int memory) {
        this.usedMemory += memory;
    }

    public void removeUsedMemory(int memory) {
        this.usedMemory -= memory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInformation)) return false;
        NodeInformation that = (NodeInformation) o;
        return getStartupTime() == that.getStartupTime() &&
                getUsedMemory() == that.getUsedMemory() &&
                getMaxMemory() == that.getMaxMemory() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getNodeUniqueID(), that.getNodeUniqueID()) &&
                Objects.equals(getStartedProcesses(), that.getStartedProcesses());
    }

    public boolean canEqual(NodeInformation other) {
        return getNodeUniqueID().equals(other.getNodeUniqueID());
    }
}
