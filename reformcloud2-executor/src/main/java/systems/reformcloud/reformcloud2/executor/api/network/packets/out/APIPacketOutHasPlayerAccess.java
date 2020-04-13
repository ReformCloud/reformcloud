package systems.reformcloud.reformcloud2.executor.api.network.packets.out;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class APIPacketOutHasPlayerAccess extends JsonPacket {

    public APIPacketOutHasPlayerAccess(@NotNull String address) {
        super(NetworkUtil.PLAYER_INFORMATION_BUS + 4, new JsonConfiguration().add("address", address));
    }
}
