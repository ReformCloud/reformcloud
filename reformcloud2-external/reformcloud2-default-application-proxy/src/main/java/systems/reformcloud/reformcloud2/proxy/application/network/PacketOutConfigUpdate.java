package systems.reformcloud.reformcloud2.proxy.application.network;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.proxy.application.ConfigHelper;

public class PacketOutConfigUpdate extends JsonPacket {

    public PacketOutConfigUpdate() {
        super(NetworkUtil.EXTERNAL_BUS + 3, new JsonConfiguration().add("config", ConfigHelper.getProxyConfiguration()));
    }
}
