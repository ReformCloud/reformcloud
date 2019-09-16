package de.klaro.reformcloud2.executor.controller.join;

import de.klaro.reformcloud2.executor.api.common.process.join.JoinRequest;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class OnlyProxyJoinHelper {

    private OnlyProxyJoinHelper() {
    }

    private static final List<JoinRequest> JOIN_REQUESTS = new ArrayList<>();

    public static void createRequest(UUID uuid, String name) {
        JOIN_REQUESTS.add(new JoinRequest(uuid, name));
    }

    public static boolean walkedOverProxy(UUID uuid, String name) {
        JoinRequest joinRequest = Links.filter(JOIN_REQUESTS, joinRequest1 -> joinRequest1.getUniqueID().equals(uuid) && joinRequest1.getName().equals(name));
        return joinRequest != null;
    }

    public static void onDisconnect(UUID uuid) {
        JoinRequest joinRequest = Links.filter(JOIN_REQUESTS, joinRequest1 -> joinRequest1.getUniqueID().equals(uuid));
        if (joinRequest != null) {
            JOIN_REQUESTS.remove(joinRequest);
        }
    }
}
