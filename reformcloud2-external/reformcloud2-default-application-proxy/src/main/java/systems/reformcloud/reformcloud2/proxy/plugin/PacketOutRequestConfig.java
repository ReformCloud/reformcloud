package systems.reformcloud.reformcloud2.proxy.plugin;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public class PacketOutRequestConfig extends JsonPacket {

    public PacketOutRequestConfig() {
        super(NetworkUtil.EXTERNAL_BUS + 2, new JsonConfiguration());
    }
}
