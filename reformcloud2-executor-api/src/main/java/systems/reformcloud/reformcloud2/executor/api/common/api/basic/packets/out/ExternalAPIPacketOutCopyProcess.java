package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public class ExternalAPIPacketOutCopyProcess extends JsonPacket {

    public ExternalAPIPacketOutCopyProcess(@NotNull String name, @Nullable String targetTemplate, @Nullable String targetTemplateStorage, @Nullable String targetTemplateGroup) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 38, new JsonConfiguration()
                .add("name", name)
                .add("targetTemplate", targetTemplate)
                .add("targetTemplateStorage", targetTemplateStorage)
                .add("targetTemplateGroup", targetTemplateGroup)
        );
    }

    public ExternalAPIPacketOutCopyProcess(@NotNull UUID processUniqueID, @Nullable String targetTemplate, @Nullable String targetTemplateStorage, @Nullable String targetTemplateGroup) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 38, new JsonConfiguration()
                .add("uuid", processUniqueID)
                .add("targetTemplate", targetTemplate)
                .add("targetTemplateStorage", targetTemplateStorage)
                .add("targetTemplateGroup", targetTemplateGroup)
        );
    }
}
