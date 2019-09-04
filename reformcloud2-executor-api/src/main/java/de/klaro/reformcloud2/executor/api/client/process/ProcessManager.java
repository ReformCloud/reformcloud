package de.klaro.reformcloud2.executor.api.client.process;

import java.util.Optional;

public interface ProcessManager {

    void registerProcess(RunningProcess runningProcess);

    void unregisterProcess(String name);

    Optional<RunningProcess> getProcess(String name);

    void stopAll();
}
