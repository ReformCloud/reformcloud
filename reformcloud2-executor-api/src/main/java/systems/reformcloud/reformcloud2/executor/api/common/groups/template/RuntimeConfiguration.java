package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RuntimeConfiguration {

  public RuntimeConfiguration(int maxMemory, List<String> processParameters,
                              Map<String, String> systemProperties) {
    this(maxMemory, processParameters, new ArrayList<>(), systemProperties);
  }

  public RuntimeConfiguration(int maxMemory, List<String> processParameters,
                              List<String> jvmOptions,
                              Map<String, String> systemProperties) {
    this.maxMemory = maxMemory;
    this.processParameters = processParameters;
    this.jvmOptions = jvmOptions;
    this.systemProperties = systemProperties;
  }

  private int maxMemory;

  private List<String> processParameters;

  private List<String> jvmOptions;

  private Map<String, String> systemProperties;

  public int getMaxMemory() { return maxMemory; }

  public List<String> getProcessParameters() { return processParameters; }

  public List<String> getJvmOptions() { return jvmOptions; }

  public Map<String, String> getSystemProperties() { return systemProperties; }
}
