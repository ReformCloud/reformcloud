package systems.reformcloud.reformcloud2.executor.api.common.network.auth.packet;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class PacketOutAuth extends JsonPacket {

    public PacketOutAuth(Auth auth) {
        super(-512, new JsonConfiguration().add("auth", auth));
    }
}
