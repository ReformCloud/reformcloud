package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RuntimeConfiguration {

    public RuntimeConfiguration(int maxMemory, List<String> processParameters, Map<String, String> systemProperties) {
        this(maxMemory, processParameters, new ArrayList<>(), systemProperties);
    }

    public RuntimeConfiguration(int maxMemory, List<String> processParameters, List<String> jvmOptions, Map<String, String> systemProperties) {
        this(maxMemory, processParameters, jvmOptions, systemProperties, new ArrayList<>());
    }

    public RuntimeConfiguration(int maxMemory, List<String> processParameters, List<String> jvmOptions,
                                Map<String, String> systemProperties, Collection<String> shutdownCommands) {
        this.maxMemory = maxMemory;
        this.processParameters = processParameters;
        this.jvmOptions = jvmOptions;
        this.systemProperties = systemProperties;
        this.shutdownCommands = shutdownCommands;
    }

    private int maxMemory;

    private List<String> processParameters;

    private List<String> jvmOptions;

    private Map<String, String> systemProperties;

    private Collection<String> shutdownCommands;

    public int getMaxMemory() {
        return maxMemory;
    }

    public List<String> getProcessParameters() {
        return processParameters;
    }

    public List<String> getJvmOptions() {
        return jvmOptions;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    /* Needs a null check because of add in version 2.0.2 */
    public Collection<String> getShutdownCommands() {
        return shutdownCommands == null ? new CopyOnWriteArrayList<>() : new CopyOnWriteArrayList<>(shutdownCommands);
    }
}
