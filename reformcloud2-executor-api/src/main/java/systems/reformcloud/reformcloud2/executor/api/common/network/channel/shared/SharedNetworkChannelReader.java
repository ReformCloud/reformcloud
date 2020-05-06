package systems.reformcloud.reformcloud2.executor.api.common.network.channel.shared;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults.DefaultPacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.exception.SilentNetworkException;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.net.InetSocketAddress;

public abstract class SharedNetworkChannelReader implements NetworkChannelReader {

    protected PacketSender packetSender;

    @NotNull
    @Override
    public PacketSender sender() {
        return this.packetSender;
    }

    @Override
    public void setChannelHandlerContext(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull String name) {
        Conditions.isTrue(this.packetSender == null, "Cannot redefine packet sender");
        this.packetSender = new DefaultPacketSender(channelHandlerContext);
        this.packetSender.setName(name);
        DefaultChannelManager.INSTANCE.registerChannel(this.packetSender);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext context) {
        if (packetSender == null) {
            String address = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
            System.out.println(LanguageManager.get("network-channel-connected", address));
        }
    }

    @Override
    public void read(@NotNull ChannelHandlerContext context, @NotNull ChallengeAuthHandler authHandler,
                     @NotNull ChannelReaderHelper parent, @NotNull Packet input) {
        NetworkUtil.EXECUTOR.execute(() -> {
            if (input.getQueryUniqueID() != null) {
                Task<Packet> waitingQuery = ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().getWaitingQuery(input.getQueryUniqueID());
                if (waitingQuery != null) {
                    waitingQuery.complete(input);
                    return;
                }
            }

            try {
                input.handlePacketReceive(this, authHandler, parent, this.packetSender, context);
            } catch (final Throwable throwable) {
                throw new SilentNetworkException(throwable);
            }
        });
    }
}
