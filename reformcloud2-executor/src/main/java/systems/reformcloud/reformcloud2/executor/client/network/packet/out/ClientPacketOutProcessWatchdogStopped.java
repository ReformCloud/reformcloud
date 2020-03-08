package systems.reformcloud.reformcloud2.executor.client.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public class ClientPacketOutProcessWatchdogStopped extends JsonPacket {

    public ClientPacketOutProcessWatchdogStopped(String name) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 11, new JsonConfiguration().add("name", name));
    }
}
