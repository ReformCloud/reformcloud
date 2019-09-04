package de.klaro.reformcloud2.executor.api.common.api.process;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProcessAsyncAPI extends ProcessSyncAPI {

    Task<ProcessInformation> startProcessAsync(String groupName);

    Task<ProcessInformation> startProcessAsync(String groupName, String template);

    Task<ProcessInformation> startProcessAsync(String groupName, String template, JsonConfiguration configurable);

    Task<ProcessInformation> stopProcessAsync(String name);

    Task<ProcessInformation> stopProcessAsync(UUID uniqueID);

    Task<ProcessInformation> getProcessAsync(String name);

    Task<ProcessInformation> getProcessAsync(UUID uniqueID);

    Task<List<ProcessInformation>> getAllProcessesAsync();

    Task<List<ProcessInformation>> getProcessesAsync(String group);

    Task<Void> executeProcessCommandAsync(String name, String commandLine);

    Task<Integer> getGlobalOnlineCountAsync(Collection<String> ignoredProxies);
}
