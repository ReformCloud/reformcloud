package de.klaro.reformcloud2.executor.client.process;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.client.process.basic.DefaultRunningProcess;

final class RunningProcessBuilder {

    static RunningProcess build(ProcessInformation processInformation) {
        return new DefaultRunningProcess(processInformation);
    }
}
