package de.klaro.reformcloud2.executor.client.packet.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class ClientPacketOutProcessWatchdogStopped extends DefaultPacket {

    public ClientPacketOutProcessWatchdogStopped(String name) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 11, new JsonConfiguration().add("name", name));
    }
}
