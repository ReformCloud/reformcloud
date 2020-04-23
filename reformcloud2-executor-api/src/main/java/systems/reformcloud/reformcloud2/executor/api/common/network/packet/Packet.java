package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;

public interface Packet extends SerializableObject {

    int getId();

    @NotNull
    PacketCallable onPacketReceive();

}
