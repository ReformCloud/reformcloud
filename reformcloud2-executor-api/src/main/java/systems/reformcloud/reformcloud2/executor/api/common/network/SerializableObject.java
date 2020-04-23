package systems.reformcloud.reformcloud2.executor.api.common.network;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.io.Serializable;

public interface SerializableObject extends Serializable {

    void write(@NotNull ProtocolBuffer buffer);

    void read(@NotNull ProtocolBuffer buffer);
}
