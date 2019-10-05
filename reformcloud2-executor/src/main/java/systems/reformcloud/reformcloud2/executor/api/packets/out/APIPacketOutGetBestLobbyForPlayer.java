package systems.reformcloud.reformcloud2.executor.api.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.Version;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.Collection;

public final class APIPacketOutGetBestLobbyForPlayer extends DefaultPacket {

    public APIPacketOutGetBestLobbyForPlayer(Collection<String> permissions, Version version) {
        super(NetworkUtil.CONTROLLER_QUERY_BUS + 1, new JsonConfiguration().add("perms", permissions).add("version", version));
    }
}
