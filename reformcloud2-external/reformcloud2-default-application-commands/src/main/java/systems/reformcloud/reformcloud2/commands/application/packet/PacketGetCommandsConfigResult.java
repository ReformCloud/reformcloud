package systems.reformcloud.reformcloud2.commands.application.packet;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

public class PacketGetCommandsConfigResult extends QueryResultPacket {

    public PacketGetCommandsConfigResult() {
    }

    public PacketGetCommandsConfigResult(CommandsConfig commandsConfig) {
        this.commandsConfig = commandsConfig;
    }

    private CommandsConfig commandsConfig;

    public CommandsConfig getCommandsConfig() {
        return commandsConfig;
    }

    @Override
    public int getId() {
        return NetworkUtil.EXTERNAL_BUS + 5;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.commandsConfig);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.commandsConfig = buffer.readObject(CommandsConfig.class);
    }
}
