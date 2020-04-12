package systems.reformcloud.reformcloud2.commands.application.packet.out;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class PacketOutReleaseCommandsConfig extends JsonPacket {

    public PacketOutReleaseCommandsConfig(@NotNull CommandsConfig commandsConfig) {
        super(NetworkUtil.EXTERNAL_BUS + 4, new JsonConfiguration().add("config", commandsConfig));
    }
}
