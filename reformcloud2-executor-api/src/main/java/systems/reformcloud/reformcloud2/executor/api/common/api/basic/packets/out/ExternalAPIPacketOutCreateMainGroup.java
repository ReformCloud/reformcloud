package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutCreateMainGroup extends DefaultPacket {

    public ExternalAPIPacketOutCreateMainGroup(MainGroup mainGroup) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 16, new JsonConfiguration()
                .add("group", mainGroup)
        );
    }
}
