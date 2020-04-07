package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;

public final class ExternalAPIPacketOutStartProcess extends JsonPacket {

    public ExternalAPIPacketOutStartProcess(ProcessConfiguration processConfiguration, boolean start) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 31, new JsonConfiguration()
                .add("config", processConfiguration)
                .add("start", start)
        );
    }

    public ExternalAPIPacketOutStartProcess(ProcessInformation information) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 31, new JsonConfiguration().add("info", information));
    }
}
