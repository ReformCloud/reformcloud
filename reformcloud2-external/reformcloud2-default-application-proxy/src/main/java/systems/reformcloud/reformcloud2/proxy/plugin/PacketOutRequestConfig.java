package systems.reformcloud.reformcloud2.proxy.plugin;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class PacketOutRequestConfig extends DefaultPacket {

    public PacketOutRequestConfig() {
        super(NetworkUtil.EXTERNAL_BUS + 2, new JsonConfiguration());
    }
}
