package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.WrappedByteInput;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface NetworkChannelReader {

    /**
     * @return The packet handler of the network channel
     */
    @Nonnull
    PacketHandler getPacketHandler();

    /**
     * @return The current packet sender of the reader
     */
    @Nonnull
    PacketSender sender();

    /**
     * Sets the context of the channel and the name if the current channel.
     * <br>
     * This method can only get called once. If you call it twice it will throws an exception.
     *
     * @param channelHandlerContext The context of the channel which should get registered
     * @param name                  The name of the channel which should get registered
     * @see systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper Gets called there
     */
    void setChannelHandlerContext(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull String name);

    /**
     * Gets called when a channel opens
     *
     * @param context The channel context of the channel
     */
    void channelActive(ChannelHandlerContext context);

    /**
     * Gets called when a channel closes
     *
     * @param context The channel context
     */
    void channelInactive(ChannelHandlerContext context);

    /**
     * Gets called when a packet comes into the channel
     *
     * @param context The context of the channel where the packet is from
     * @param input   The sent content by the sender
     */
    void read(ChannelHandlerContext context, WrappedByteInput input);

    /**
     * Handles the exceptions which will occur in the channel
     *
     * @param context The context of the channel the exception occurred in
     * @param cause   The cause why the exception occurred
     */
    default void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        if (!(cause instanceof IOException)) {
            System.err.println("Exception in channel " + context.channel().remoteAddress());
            cause.printStackTrace();
        }
    }

    /**
     * Gets called after the channel read
     *
     * @param context The context of the channel
     */
    default void readOperationCompleted(ChannelHandlerContext context) {
        context.flush();
    }
}
