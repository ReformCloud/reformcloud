package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutUpdateProcessGroup extends JsonPacket {

    public ExternalAPIPacketOutUpdateProcessGroup(ProcessGroup processGroup) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 19, new JsonConfiguration().add("group", processGroup));
    }
}
