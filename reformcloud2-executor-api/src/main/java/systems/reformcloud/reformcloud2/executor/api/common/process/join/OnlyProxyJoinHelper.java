package systems.reformcloud.reformcloud2.executor.api.common.process.join;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class OnlyProxyJoinHelper {

    private OnlyProxyJoinHelper() {
        throw new UnsupportedOperationException();
    }

    public static boolean walkedOverProxy(@NotNull String senderAddress) {
        for (ProcessInformation allProcess : ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses()) {
            if (!allProcess.getNetworkInfo().isConnected() || allProcess.getProcessDetail().getTemplate().isServer()) {
                continue;
            }

            if (walkedOverProxy(allProcess, senderAddress)) {
                return true;
            }
        }

        return false;
    }

    private static boolean walkedOverProxy(@NotNull ProcessInformation processInformation, @NotNull String senderAddress) {
        try {
            InetAddress address = InetAddress.getByName(processInformation.getNetworkInfo().getHost());
            if (address.isLoopbackAddress() || address.isAnyLocalAddress()) {
                return true;
            }

            return senderAddress.equals(address.getHostAddress());
        } catch (final UnknownHostException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
