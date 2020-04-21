package systems.reformcloud.reformcloud2.executor.controller.network.packets.out;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public final class ControllerPacketOutCopyProcess extends JsonPacket {

    public ControllerPacketOutCopyProcess(UUID uuid, @NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 8, new JsonConfiguration()
                .add("uuid", uuid)
                .add("targetTemplate", targetTemplate)
                .add("targetTemplateStorage", targetTemplateStorage)
                .add("targetTemplateGroup", targetTemplateGroup)
        );
    }
}
