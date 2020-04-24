package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.exception.SilentNetworkException;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.ProtocolBufferWrapper;

public final class ChannelReaderHelper extends SimpleChannelInboundHandler<ProtocolBufferWrapper> {

    public ChannelReaderHelper(NetworkChannelReader channelReader, ChallengeAuthHandler authHandler) {
        this.channelReader = channelReader;
        this.authHandler = authHandler;
    }

    private final NetworkChannelReader channelReader;

    private final ChallengeAuthHandler authHandler;

    public boolean auth = false;

    private boolean wasActive = false;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.channelReader.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (!this.auth && !this.wasActive) {
            this.wasActive = true;
            this.authHandler.handleChannelActive(ctx);
        }

        this.channelReader.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.channelReader.channelInactive(ctx);
        this.wasActive = false;
        this.auth = false;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        this.channelReader.readOperationCompleted(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtocolBufferWrapper input) {
        Packet packet = this.channelReader.getPacketHandler().getNetworkHandler(input.getId());
        if (packet == null) {
            return;
        }

        try {
            packet.read(input.getProtocolBuffer());
        } catch (final Throwable throwable) {
            throw new SilentNetworkException("Unable to read packet with id " + input.getId(), throwable);
        }

        if (!auth) {
            if (input.getId() != -512) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }
        }

        channelReader.read(channelHandlerContext, this.authHandler, this, packet);
    }
}
