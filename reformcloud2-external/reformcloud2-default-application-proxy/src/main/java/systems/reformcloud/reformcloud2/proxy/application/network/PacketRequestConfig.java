package systems.reformcloud.reformcloud2.proxy.application.network;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.proxy.application.ConfigHelper;

public class PacketRequestConfig extends Packet {

    public PacketRequestConfig() {
    }

    @Override
    public int getId() {
        return NetworkUtil.EXTERNAL_BUS + 2;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        sender.sendQueryResult(
                this.getQueryUniqueID(),
                new PacketRequestConfigResult(ConfigHelper.getProxyConfiguration())
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
    }
}
