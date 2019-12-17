package systems.reformcloud.reformcloud2.commands.application.packet.out;

import systems.reformcloud.reformcloud2.commands.application.ReformCloudApplication;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class PacketOutRegisterCommandsConfig extends DefaultPacket {

    public PacketOutRegisterCommandsConfig() {
        super(NetworkUtil.EXTERNAL_BUS + 1, new JsonConfiguration().add("config", ReformCloudApplication.getCommandsConfig()));
    }
}
