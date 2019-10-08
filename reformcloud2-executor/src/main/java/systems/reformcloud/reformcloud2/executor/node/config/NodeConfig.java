package systems.reformcloud.reformcloud2.executor.node.config;

import com.google.gson.reflect.TypeToken;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class NodeConfig {

    static final TypeToken<NodeConfig> TYPE = new TypeToken<NodeConfig>() {};

    static final Path PATH = Paths.get("reformcloud/config.json");

    NodeConfig(int maxProcesses, List<Map<String, Integer>> networkListener,
                     List<Map<String, Integer>> httpNetworkListener) {
        this.maxProcesses = maxProcesses;
        this.networkListener = networkListener;
        this.httpNetworkListener = httpNetworkListener;
    }

    private int maxProcesses;

    private List<Map<String, Integer>> networkListener;

    private List<Map<String, Integer>> httpNetworkListener;

    public int getMaxProcesses() {
        return maxProcesses;
    }

    public List<Map<String, Integer>> getNetworkListener() {
        return networkListener;
    }

    public List<Map<String, Integer>> getHttpNetworkListener() {
        return httpNetworkListener;
    }
}
