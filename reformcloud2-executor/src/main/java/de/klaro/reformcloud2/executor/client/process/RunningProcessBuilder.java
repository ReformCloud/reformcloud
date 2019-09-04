package de.klaro.reformcloud2.executor.client.process;

import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.CommonHelper;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.client.ClientExecutor;
import de.klaro.reformcloud2.executor.client.process.basic.DefaultRunningProcess;

final class RunningProcessBuilder {

    static RunningProcess build(ProcessInformation processInformation) {
        if (isStartupNowLogic()) {
            return new DefaultRunningProcess(processInformation).prepare();
        }

        return null;
    }

    private static boolean isStartupNowLogic() {
        if (ClientExecutor.getInstance().getClientConfig().getMaxCpu() <= 0D) {
            return true;
        }

        return CommonHelper.cpuUsageSystem() <= ClientExecutor.getInstance().getClientConfig().getMaxCpu();
    }
}
