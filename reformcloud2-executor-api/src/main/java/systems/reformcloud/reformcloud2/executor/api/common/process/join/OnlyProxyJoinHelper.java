package systems.reformcloud.reformcloud2.executor.api.common.process.join;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.ArrayList;
import java.util.Collection;
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

    public static boolean walkedOverProxy(UUID uuid, String name) {
        JoinRequest joinRequest = Streams.filter(JOIN_REQUESTS, joinRequest1 -> joinRequest1.getUniqueID().equals(uuid) && joinRequest1.getName().equals(name));
        if (joinRequest == null) {
            return false;
        }

        return getOf(uuid).size() == 1; // Checks if the player is only on one proxy
    }

    public static void onDisconnect(UUID uuid) {
        JoinRequest joinRequest = Streams.filter(JOIN_REQUESTS, joinRequest1 -> joinRequest1.getUniqueID().equals(uuid));
        if (joinRequest != null) {
            JOIN_REQUESTS.remove(joinRequest);
        }
    }

    private static Collection<ProcessInformation> getOf(UUID uniqueID) {
        return ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses()
                .stream()
                .filter(e -> !e.getTemplate().isServer())
                .filter(e -> e.isPlayerOnline(uniqueID))
                .collect(Collectors.toList());
    }
}
