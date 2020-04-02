package systems.reformcloud.reformcloud2.executor.api.common.process.join;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class OnlyProxyJoinHelper {

    private OnlyProxyJoinHelper() {
        throw new UnsupportedOperationException();
    }

    private static final List<JoinRequest> JOIN_REQUESTS = new ArrayList<>();

    public static void createRequest(UUID uuid, String name) {
        JOIN_REQUESTS.add(new JoinRequest(uuid, name));
    }

    public static boolean walkedOverProxy(UUID uuid, String name, String senderAddress) {
        List<JoinRequest> requests = JOIN_REQUESTS.stream().filter(e -> e.getUniqueID().equals(uuid) || e.getName().equals(name)).collect(Collectors.toList());
        if (requests.size() != 1) {
            return false;
        }

        List<ProcessInformation> proxies = getProxy(uuid, name);
        if (proxies.size() != 1) {
            return false;
        }

        try {
            InetAddress address = InetAddress.getByName(proxies.get(0).getNetworkInfo().getHost());
            if (address.isLoopbackAddress() || address.isAnyLocalAddress()) {
                return true;
            }

            return senderAddress.equals(address.getHostAddress());
        } catch (final UnknownHostException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static void onDisconnect(UUID uuid) {
        JoinRequest joinRequest = getRequest(uuid);
        if (joinRequest != null) {
            JOIN_REQUESTS.remove(joinRequest);
        }
    }

    private static JoinRequest getRequest(UUID uuid) {
        return Streams.filter(JOIN_REQUESTS, joinRequest1 -> joinRequest1.getUniqueID().equals(uuid));
    }

    private static List<ProcessInformation> getProxy(UUID uniqueID, String name) {
        return ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses()
                .stream()
                .filter(e -> !e.getProcessDetail().getTemplate().isServer())
                .filter(e -> e.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(uniqueID) || e.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(name))
                .collect(Collectors.toList());
    }
}
