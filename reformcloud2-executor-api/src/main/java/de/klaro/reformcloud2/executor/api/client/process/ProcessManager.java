package de.klaro.reformcloud2.executor.api.client.process;

import de.klaro.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.Collection;
import java.util.UUID;

public interface ProcessManager {

    void registerProcess(RunningProcess runningProcess);

    void unregisterProcess(String name);

    ReferencedOptional<RunningProcess> getProcess(UUID uniqueID);

    ReferencedOptional<RunningProcess> getProcess(String name);

    Collection<RunningProcess> getAll();

    void onProcessDisconnect(UUID uuid);

    void stopAll();
}
