package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import java.util.List;
import java.util.Map;

public final class RuntimeConfiguration {

    public RuntimeConfiguration(int maxMemory, List<String> processParameters, Map<String, String> systemProperties) {
        this.maxMemory = maxMemory;
        this.processParameters = processParameters;
        this.systemProperties = systemProperties;
    }

    private int maxMemory;

    private List<String> processParameters;

    private Map<String, String> systemProperties;

    public int getMaxMemory() {
        return maxMemory;
    }

    public List<String> getProcessParameters() {
        return processParameters;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }
}
