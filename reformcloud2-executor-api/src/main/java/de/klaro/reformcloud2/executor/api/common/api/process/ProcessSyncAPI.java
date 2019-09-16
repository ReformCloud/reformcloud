package de.klaro.reformcloud2.executor.api.common.api.process;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;
import de.klaro.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ProcessSyncAPI {

    ProcessInformation startProcess(String groupName);

    ProcessInformation startProcess(String groupName, String template);

    ProcessInformation startProcess(String groupName, String template, JsonConfiguration configurable);

    ProcessInformation stopProcess(String name);

    ProcessInformation stopProcess(UUID uniqueID);

    ProcessInformation getProcess(String name);

    ProcessInformation getProcess(UUID uniqueID);

    List<ProcessInformation> getAllProcesses();

    List<ProcessInformation> getProcesses(String group);

    void executeProcessCommand(String name, String commandLine);

    int getGlobalOnlineCount(Collection<String> ignoredProxies);

    ProcessInformation getThisProcessInformation();

    default void forEach(Consumer<ProcessInformation> action) {
        getAllProcesses().forEach(action);
    }

    void update(ProcessInformation processInformation);

    default Task<Void> updateAsync(ProcessInformation processInformation) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            update(processInformation);
            task.complete(null);
        });
        return task;
    }
}
