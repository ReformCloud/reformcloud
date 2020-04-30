package systems.reformcloud.reformcloud2.signs.packets;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

public class PacketReloadSignConfig implements Packet {

    public PacketReloadSignConfig() {
    }

    public PacketReloadSignConfig(SignConfig signConfig) {
        this.signConfig = signConfig;
    }

    private SignConfig signConfig;

    @Override
    public int getId() {
        return PacketUtil.SIGN_BUS + 6;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        SignSystemAdapter.getInstance().handleSignConfigUpdate(this.signConfig);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.signConfig);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.signConfig = buffer.readObject(SignConfig.class);
    }
}
