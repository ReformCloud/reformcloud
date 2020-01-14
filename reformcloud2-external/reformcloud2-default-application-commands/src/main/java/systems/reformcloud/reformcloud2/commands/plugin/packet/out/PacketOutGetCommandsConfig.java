package systems.reformcloud.reformcloud2.commands.plugin.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public class PacketOutGetCommandsConfig extends JsonPacket {

    public PacketOutGetCommandsConfig() {
        super(NetworkUtil.EXTERNAL_BUS + 1, new JsonConfiguration());
    }
}
