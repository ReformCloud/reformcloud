package systems.reformcloud.reformcloud2.commands.plugin.packet;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public class PacketReleaseCommandsConfig extends Packet {

    public PacketReleaseCommandsConfig() {
    }

    public PacketReleaseCommandsConfig(CommandsConfig commandsConfig) {
        this.commandsConfig = commandsConfig;
    }

    private CommandsConfig commandsConfig;

    @Override
    public int getId() {
        return NetworkUtil.EXTERNAL_BUS + 4;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        CommandConfigHandler.getInstance().handleCommandConfigRelease(this.commandsConfig);
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
