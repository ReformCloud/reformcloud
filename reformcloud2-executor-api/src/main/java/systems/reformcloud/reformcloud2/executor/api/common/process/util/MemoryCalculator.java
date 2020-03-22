package systems.reformcloud.reformcloud2.executor.api.common.process.util;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;

import javax.annotation.Nonnull;

public final class MemoryCalculator {

    private MemoryCalculator() {
        throw new UnsupportedOperationException();
    }

    public static int calcMemory(@Nonnull String group, @Nonnull Template template) {
        RuntimeConfiguration configuration = template.getRuntimeConfiguration();
        if (configuration.getMaxMemory() < 0 || configuration.getDynamicMemory() < 0) {
            return configuration.getMaxMemory() < 0 ? 512 : configuration.getMaxMemory();
        }

        if (configuration.getDynamicMemory() <= configuration.getMaxMemory()) {
            return configuration.getMaxMemory();
        }

        int online = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(group).size();
        if (online > 9) {
            return configuration.getMaxMemory();
        }

        if (online == 0) {
            return configuration.getDynamicMemory();
        }

        return ((configuration.getDynamicMemory() - configuration.getMaxMemory()) / 100) * (((10 - online) * 10)) + configuration.getMaxMemory();
    }
}
