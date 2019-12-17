package systems.reformcloud.reformcloud2.executor.api.client.process;

import java.util.Collection;
import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

public interface ProcessManager {

  void registerProcess(RunningProcess runningProcess);

  void unregisterProcess(String name);

  ReferencedOptional<RunningProcess> getProcess(UUID uniqueID);

  ReferencedOptional<RunningProcess> getProcess(String name);

  Collection<RunningProcess> getAll();

  void onProcessDisconnect(UUID uuid);

  void stopAll();
}
