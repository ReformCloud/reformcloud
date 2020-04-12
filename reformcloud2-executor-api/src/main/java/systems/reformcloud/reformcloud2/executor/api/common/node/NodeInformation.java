package systems.reformcloud.reformcloud2.executor.api.common.node;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessRuntimeInformation;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class NodeInformation {

    public static final TypeToken<NodeInformation> TYPE = new TypeToken<NodeInformation>() {
    };

    private final String name;

    private final UUID nodeUniqueID;

    private final long startupTime;

    private long lastUpdate;

    private long usedMemory;

    private long maxMemory;

    private ProcessRuntimeInformation processRuntimeInformation;

    private final Collection<NodeProcess> startedProcesses;

    public NodeInformation(String name, UUID nodeUniqueID, long startupTime,
                           long usedMemory, long maxMemory, Collection<NodeProcess> startedProcesses) {
        this.name = name;
        this.nodeUniqueID = nodeUniqueID;
        this.startupTime = this.lastUpdate = startupTime;
        this.usedMemory = usedMemory;
        this.maxMemory = maxMemory;
        this.processRuntimeInformation = ProcessRuntimeInformation.create();
        this.startedProcesses = new CopyOnWriteArrayList<>(startedProcesses);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public UUID getNodeUniqueID() {
        return nodeUniqueID;
    }

    public long getStartupTime() {
        return startupTime;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    @NotNull
    public ProcessRuntimeInformation getProcessRuntimeInformation() {
        return processRuntimeInformation;
    }

    @NotNull
    public Collection<NodeProcess> getStartedProcesses() {
        return startedProcesses;
    }

    public void addUsedMemory(int memory) {
        this.usedMemory += memory;
    }

    public void removeUsedMemory(int memory) {
        this.usedMemory -= memory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public void update() {
        this.processRuntimeInformation = ProcessRuntimeInformation.create();
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInformation)) return false;
        NodeInformation that = (NodeInformation) o;
        return getStartupTime() == that.getStartupTime() &&
                getLastUpdate() == that.getLastUpdate() &&
                getUsedMemory() == that.getUsedMemory() &&
                getMaxMemory() == that.getMaxMemory() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getNodeUniqueID(), that.getNodeUniqueID()) &&
                Objects.equals(getProcessRuntimeInformation(), that.getProcessRuntimeInformation()) &&
                Objects.equals(getStartedProcesses(), that.getStartedProcesses());
    }

    public boolean canEqual(NodeInformation other) {
        return getNodeUniqueID().equals(other.getNodeUniqueID());
    }
}
