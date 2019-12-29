package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutCreateProcessGroup extends JsonPacket {

    public ExternalAPIPacketOutCreateProcessGroup(ProcessGroup group) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 17, new JsonConfiguration().add("group", group));
    }
}
