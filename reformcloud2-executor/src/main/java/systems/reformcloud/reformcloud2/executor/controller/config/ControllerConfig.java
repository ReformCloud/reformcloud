package systems.reformcloud.reformcloud2.executor.controller.config;

import com.google.gson.reflect.TypeToken;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public final class ControllerConfig {

    static final TypeToken<ControllerConfig> TYPE = new TypeToken<ControllerConfig>() {
    };

    static final Path PATH = Paths.get("reformcloud/config.json");
    private int maxProcesses;
    private List<Map<String, Integer>> networkListener;
    private List<Map<String, Integer>> httpNetworkListener;

    ControllerConfig(int maxProcesses, List<Map<String, Integer>> networkListener,
                     List<Map<String, Integer>> httpNetworkListener) {
        this.maxProcesses = maxProcesses;
        this.networkListener = networkListener;
        this.httpNetworkListener = httpNetworkListener;
    }

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
