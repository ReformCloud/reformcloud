package systems.reformcloud.reformcloud2.executor.client.process;

import systems.reformcloud.reformcloud2.executor.api.client.process.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.client.process.basic.DefaultRunningProcess;

final class RunningProcessBuilder {

  static RunningProcess build(ProcessInformation processInformation) {
    return new DefaultRunningProcess(processInformation);
  }
}
