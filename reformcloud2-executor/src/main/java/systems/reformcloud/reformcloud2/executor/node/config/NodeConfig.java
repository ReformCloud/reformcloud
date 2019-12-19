package systems.reformcloud.reformcloud2.executor.node.config;

import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

public class NodeConfig {

  static final TypeToken<NodeConfig> TYPE = new TypeToken<NodeConfig>() {};

  static final Path PATH = Paths.get("reformcloud/config.json");

  NodeConfig(long maxMemory, String startHost,
             List<Map<String, Integer>> networkListener,
             List<Map<String, Integer>> httpNetworkListener,
             List<Map<String, Integer>> otherNodes) {
    this.name = "Node-" + UUID.randomUUID().toString().split("-")[0];
    this.uniqueID = UUID.randomUUID();
    this.maxMemory = maxMemory;
    this.startHost = startHost;
    this.networkListener = networkListener;
    this.httpNetworkListener = httpNetworkListener;
    this.otherNodes = otherNodes;
  }

  private String name;

  private UUID uniqueID;

  private long maxMemory;

  private String startHost;

  private List<Map<String, Integer>> networkListener;

  private List<Map<String, Integer>> httpNetworkListener;

  private List<Map<String, Integer>> otherNodes;

  public String getName() { return name; }

  public UUID getUniqueID() { return uniqueID; }

  public long getMaxMemory() { return maxMemory; }

  public String getStartHost() { return startHost; }

  public List<Map<String, Integer>> getNetworkListener() {
    return networkListener;
  }

  public List<Map<String, Integer>> getHttpNetworkListener() {
    return httpNetworkListener;
  }

  public List<Map<String, Integer>> getOtherNodes() { return otherNodes; }

  NodeInformation prepare() {
    return new NodeInformation(name, uniqueID, System.nanoTime(), 0L, maxMemory,
                               new ArrayList<>());
  }

  public void save() {
    new JsonConfiguration().add("config", this).write(PATH);
  }
}
