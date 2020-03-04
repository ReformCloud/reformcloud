package systems.reformcloud.reformcloud2.executor.api.common.process.join;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import javax.annotation.Nullable;
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

    static {
        ExecutorAPI.getInstance().getEventManager().registerListener(new ServerSwitchListener());
    }

    public static void createRequest(UUID uuid, String name) {
        JOIN_REQUESTS.add(new JoinRequest(uuid, name, null));
    }

    public static void handleServerSwitch(UUID uuid, String serverName) {
        JoinRequest request = getRequest(uuid, null);
        if (request == null) {
            return;
        }

        request.setCurrentServer(serverName);
    }

    public static boolean walkedOverProxy(UUID uuid, String name, String senderName) {
        List<JoinRequest> requests = JOIN_REQUESTS.stream().filter(e -> e.getUniqueID().equals(uuid) || e.getName().equals(name)).collect(Collectors.toList());
        if (requests.size() != 1) {
            return false;
        }

        // Check if the player is on no other server than the server who sent the request itself
        return getProxy(uuid, name).size() == 1 && senderName.equals(requests.get(0).getCurrentServer());
    }

    public static void onDisconnect(UUID uuid) {
        JoinRequest joinRequest = getRequest(uuid, null);
        if (joinRequest != null) {
            JOIN_REQUESTS.remove(joinRequest);
        }
    }

    private static JoinRequest getRequest(UUID uuid, @Nullable String name) {
        return Streams.filter(JOIN_REQUESTS, joinRequest1 -> joinRequest1.getUniqueID().equals(uuid) && (name == null || joinRequest1.getName().equals(name)));
    }

    private static Collection<ProcessInformation> getProxy(UUID uniqueID, String name) {
        return ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses()
                .stream()
                .filter(e -> !e.getTemplate().isServer())
                .filter(e -> e.isPlayerOnline(uniqueID) || e.isPlayerOnline(name))
                .collect(Collectors.toList());
    }

    private static Collection<ProcessInformation> getServer(UUID uniqueID, String name) {
        return ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses()
                .stream()
                .filter(e -> e.getTemplate().isServer())
                .filter(e -> e.isPlayerOnline(uniqueID) || e.isPlayerOnline(name))
                .collect(Collectors.toList());
    }
}
