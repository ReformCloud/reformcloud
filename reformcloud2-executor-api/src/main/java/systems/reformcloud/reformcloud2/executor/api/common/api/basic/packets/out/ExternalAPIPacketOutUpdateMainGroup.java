package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutUpdateMainGroup extends JsonPacket {

    public ExternalAPIPacketOutUpdateMainGroup(MainGroup group) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 18, new JsonConfiguration().add("group", group));
    }
}
