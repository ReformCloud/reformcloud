package systems.reformcloud.reformcloud2.executor.api.common.node;

import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class NodeInformation {

    public static final TypeToken<NodeInformation> TYPE = new TypeToken<NodeInformation>() {};

    private String name;

    private UUID nodeUniqueID;

    private long startupTime;

    private Collection<NodeInformation> connectedNodes;

    private long usedMemory;

    private long maxMemory;

    private Collection<NodeProcess> startedProcesses;

    private Map<String, String> queuedProcesses;

    public NodeInformation(String name, UUID nodeUniqueID, long startupTime, Collection<NodeInformation> connectedNodes,
                           long usedMemory, long maxMemory, Collection<NodeProcess> startedProcesses, Map<String, String> queuedProcesses) {
        this.name = name;
        this.nodeUniqueID = nodeUniqueID;
        this.startupTime = startupTime;
        this.connectedNodes = connectedNodes;
        this.usedMemory = usedMemory;
        this.maxMemory = maxMemory;
        this.startedProcesses = startedProcesses;
        this.queuedProcesses = queuedProcesses;
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

    public Collection<NodeInformation> getConnectedNodes() {
        return connectedNodes;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public Collection<NodeProcess> getStartedProcesses() {
        return startedProcesses;
    }

    public Map<String, String> getQueuedProcesses() {
        return queuedProcesses;
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
                Objects.equals(getConnectedNodes(), that.getConnectedNodes()) &&
                Objects.equals(getStartedProcesses(), that.getStartedProcesses()) &&
                Objects.equals(getQueuedProcesses(), that.getQueuedProcesses());
    }

    public boolean canEqual(NodeInformation other) {
        return getNodeUniqueID().equals(other.getNodeUniqueID());
    }
}
