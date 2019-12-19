package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.DoubleFunction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class ChannelReaderHelper extends SimpleChannelInboundHandler<Packet> {

    ChannelReaderHelper(NetworkChannelReader channelReader, DoubleFunction<Packet, String, Boolean> function,
                        BiFunction<String, ChannelHandlerContext, PacketSender> onAuthSuccess, Consumer<ChannelHandlerContext> onAuthFailure) {
        this.channelReader = channelReader;
        this.function = function;
        this.onSuccess = onAuthSuccess;
        this.onAuthFailure = onAuthFailure;
    }

    private NetworkChannelReader channelReader;

    private final DoubleFunction<Packet, String, Boolean> function;

    private final BiFunction<String, ChannelHandlerContext, PacketSender> onSuccess;

    private final Consumer<ChannelHandlerContext> onAuthFailure;

    private final AtomicBoolean auth = new AtomicBoolean(false);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        channelReader.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        //controller auth
        if (packet.packetID() == -512 && !auth.get()) {
            Double<String, Boolean> result = function.apply(packet);
            if (result.getSecond()) {
                auth.set(true);
                PacketSender sender = onSuccess.apply(result.getFirst(), channelHandlerContext);
                channelReader.setSender(sender);
            } else {
                onAuthFailure.accept(channelHandlerContext);
            }

            return;
        }

        //client auth success
        if (packet.packetID() == -511 && !auth.get()) {
            Boolean access = packet.content().getOrDefault("access", false);
            if (access) {
                auth.set(true);
                Double<String, Boolean> result = function.apply(packet);
                channelReader.setSender(onSuccess.apply(result.getFirst(), channelHandlerContext));
            } else {
                channelHandlerContext.channel().close();
            }

            return;
        }

        if (!auth.get()) {
            return;
        }

        channelReader.read(channelHandlerContext, packet);
    }
}
