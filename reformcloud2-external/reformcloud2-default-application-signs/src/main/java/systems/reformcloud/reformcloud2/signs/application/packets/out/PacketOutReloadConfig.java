package systems.reformcloud.reformcloud2.signs.application.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

public class PacketOutReloadConfig extends DefaultPacket {

    public PacketOutReloadConfig(SignConfig config) {
        super(PacketUtil.SIGN_BUS + 6, new JsonConfiguration().add("config", config));
    }
}
