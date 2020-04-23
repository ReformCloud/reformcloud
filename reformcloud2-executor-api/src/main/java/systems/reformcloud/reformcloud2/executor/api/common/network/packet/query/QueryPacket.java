package systems.reformcloud.reformcloud2.executor.api.common.network.packet.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public interface QueryPacket extends Packet {

    @NotNull
    UUID getQueryUniqueId();
}
