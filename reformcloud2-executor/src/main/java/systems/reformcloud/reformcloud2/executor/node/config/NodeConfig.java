package systems.reformcloud.reformcloud2.executor.node.config;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NodeConfig {

    static final TypeToken<NodeConfig> TYPE = new TypeToken<NodeConfig>() {
    };

    static final Path PATH = Paths.get("reformcloud/config.json");

    public NodeConfig(String name, UUID uniqueID, long maxMemory, String startHost,
                      List<NetworkAddress> networkListeners, List<NetworkAddress> httpNetworkListeners,
                      List<NetworkAddress> clusterNodes) {
        this.name = name;
        this.uniqueID = uniqueID;
        this.maxMemory = maxMemory;
        this.startHost = startHost;
        this.networkListeners = networkListeners;
        this.httpNetworkListeners = httpNetworkListeners;
        this.clusterNodes = clusterNodes;
    }

    private final String name;

    private final UUID uniqueID;

    private final long maxMemory;

    private final String startHost;

    private final List<Map<String, Integer>> networkListener = new ArrayList<>();

    private final List<Map<String, Integer>> httpNetworkListener = new ArrayList<>();

    private final List<Map<String, Integer>> otherNodes = new ArrayList<>();

    private List<NetworkAddress> networkListeners;

    private List<NetworkAddress> httpNetworkListeners;

    private List<NetworkAddress> clusterNodes;

    public String getName() {
        return name;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public String getStartHost() {
        return startHost;
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    public List<Map<String, Integer>> getNetworkListener() {
        return networkListener;
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    public List<Map<String, Integer>> getHttpNetworkListener() {
        return httpNetworkListener;
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    public List<Map<String, Integer>> getOtherNodes() {
        return otherNodes;
    }

    public List<NetworkAddress> getNetworkListeners() {
        return networkListeners;
    }

    public List<NetworkAddress> getHttpNetworkListeners() {
        return httpNetworkListeners;
    }

    public List<NetworkAddress> getClusterNodes() {
        return clusterNodes;
    }

    /**
     * @deprecated Util method for upgrading configs from version 2.2 to 2.3
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    void tryTransform() {
        if (this.networkListeners == null) {
            this.networkListeners = new ArrayList<>();
            this.networkListener.forEach(e -> e.forEach((key, value) -> this.networkListeners.add(new NetworkAddress(key, value))));
        }

        if (this.httpNetworkListeners == null) {
            this.httpNetworkListeners = new ArrayList<>();
            this.httpNetworkListener.forEach(e -> e.forEach((key, value) -> this.httpNetworkListeners.add(new NetworkAddress(key, value))));
        }

        if (this.clusterNodes == null) {
            this.clusterNodes = new ArrayList<>();
            this.otherNodes.forEach(e -> e.forEach((key, value) -> this.clusterNodes.add(new NetworkAddress(key, value))));
        }

        this.save();
    }

    @NotNull
    @Contract(" -> new")
    NodeInformation prepare() {
        return new NodeInformation(
                name,
                uniqueID,
                System.currentTimeMillis(),
                0L,
                maxMemory,
                CommonHelper.cpuUsageSystem(),
                new CopyOnWriteArrayList<>()
        );
    }

    public void save() {
        new JsonConfiguration().add("config", this).write(PATH);
    }

    public static class NetworkAddress {

        public NetworkAddress(String host, int port) {
            this.host = host;
            this.port = port;
        }

        private final String host;

        private final int port;

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}
