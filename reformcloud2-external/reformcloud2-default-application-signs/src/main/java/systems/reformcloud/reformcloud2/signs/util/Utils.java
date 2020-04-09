package systems.reformcloud.reformcloud2.signs.util;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignLayout;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static boolean canConnect(@NotNull ProcessInformation target, @NotNull SignLayout layout) {
        if (!target.getNetworkInfo().isConnected()) {
            return false;
        }

        if (target.getProcessGroup().getPlayerAccessConfiguration().isMaintenance()) {
            return layout.isShowMaintenanceProcessesOnSigns();
        }

        ProcessState state = target.getProcessDetail().getProcessState();
        return state.isReady() && !state.equals(ProcessState.INVISIBLE);
    }

    public static boolean canConnectPerState(@NotNull ProcessInformation processInformation) {
        ProcessState state = processInformation.getProcessDetail().getProcessState();
        return !state.equals(ProcessState.INVISIBLE)
                && !state.equals(ProcessState.PREPARED)
                && !state.equals(ProcessState.READY_TO_START)
                && !state.equals(ProcessState.CREATED)
                && !state.equals(ProcessState.STOPPED);
    }
}
