package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.WrappedByteInput;

public final class ChannelReaderHelper extends SimpleChannelInboundHandler<WrappedByteInput> {

    public ChannelReaderHelper(NetworkChannelReader channelReader, ChallengeAuthHandler authHandler) {
        this.channelReader = channelReader;
        this.authHandler = authHandler;
    }

    private final NetworkChannelReader channelReader;

    private final ChallengeAuthHandler authHandler;

    private boolean auth = false;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        channelReader.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (!this.auth) {
            this.authHandler.handleChannelActive(ctx);
        }

        channelReader.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        channelReader.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        channelReader.readOperationCompleted(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WrappedByteInput input) {
        if (!auth) {
            try {
                Packet packet = DefaultJsonNetworkHandler.readPacket(-512, input.toObjectStream());
                String name = packet.content().getOrDefault("name", (String) null);
                if (name == null || DefaultChannelManager.INSTANCE.get(name).isPresent()) {
                    System.out.println("Unknown connect from channel (Name=" + name + "). If the name is null, that might be an attack");
                    channelHandlerContext.channel().close().syncUninterruptibly();
                    return;
                }

                this.auth = this.authHandler.handle(channelHandlerContext, packet, name);

                if (this.auth) {
                    this.channelReader.setChannelHandlerContext(channelHandlerContext, name);
                }
            } catch (final Throwable throwable) {
                channelHandlerContext.channel().close().syncUninterruptibly();
            }

            return;
        }

        channelReader.read(channelHandlerContext, input);
    }
}
