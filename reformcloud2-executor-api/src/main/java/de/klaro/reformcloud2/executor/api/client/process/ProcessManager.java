package de.klaro.reformcloud2.executor.api.client.process;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProcessManager {

    void registerProcess(RunningProcess runningProcess);

    void unregisterProcess(String name);

    Optional<RunningProcess> getProcess(UUID uniqueID);

    Optional<RunningProcess> getProcess(String name);

    Collection<RunningProcess> getAll();

    void onProcessDisconnect(UUID uuid);

    void stopAll();
}
