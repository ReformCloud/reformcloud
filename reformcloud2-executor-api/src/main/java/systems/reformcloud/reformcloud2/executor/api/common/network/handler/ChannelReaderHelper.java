package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class ChannelReaderHelper extends SimpleChannelInboundHandler<Packet> {

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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet input) {
        if (!auth) {
            if (input.getId() > NetworkUtil.AUTH_BUS + 4 || input.getId() < NetworkUtil.AUTH_BUS) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }
        }

        channelReader.read(channelHandlerContext, this.authHandler, this, input);
    }

    public NetworkChannelReader getChannelReader() {
        return channelReader;
    }
}
