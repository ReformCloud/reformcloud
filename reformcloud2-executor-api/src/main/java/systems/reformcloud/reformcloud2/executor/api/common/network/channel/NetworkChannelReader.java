package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

public interface NetworkChannelReader {

  /**
   * @return The packet handler of the network channel
   */
  @Nonnull PacketHandler getPacketHandler();

  /**
   * @return The current packet sender of the reader
   */
  @Nonnull PacketSender sender();

  /**
   * Sets a new sender
   *
   * @param sender The sender which should get used
   */
  void setSender(PacketSender sender);

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
   * @param packet The packet which got sent
   */
  void read(ChannelHandlerContext context, Packet packet);

  /**
   * Handles the exceptions which will occur in the channel
   *
   * @param context The context of the channel the exception occurred in
   * @param cause The cause why the exception occurred
   */
  default void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
    if (!(cause instanceof IOException)) {
      System.err.println("Exception in channel " +
                         context.channel().remoteAddress());
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
