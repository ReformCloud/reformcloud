package systems.reformcloud.reformcloud2.executor.api.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults.DefaultPacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

public final class NetworkUtil {

  /* ============================= */

  public static final int NODE_TO_NODE_BUS = 20000;

  public static final int NODE_TO_NODE_QUERY_BUS = 25000;

  public static final int CONTROLLER_INFORMATION_BUS = 2000;

  public static final int EVENT_BUS = 3000;

  public static final int CONTROLLER_QUERY_BUS = 4000;

  public static final int PLAYER_INFORMATION_BUS = 5000;

  public static final int EXTERNAL_BUS = 50000;

  /* ============================ */

  public static final Executor EXECUTOR = Executors.newCachedThreadPool();

  public static final Consumer<ChannelHandlerContext>
      DEFAULT_AUTH_FAILURE_HANDLER = context
      -> context.channel()
             .writeAndFlush(new DefaultPacket(
                 -511, new JsonConfiguration().add("access", false)))
             .syncUninterruptibly()
             .channel()
             .close();

  public static final BiFunction<String, ChannelHandlerContext, PacketSender>
      DEFAULT_SUCCESS_HANDLER = (s, context) -> {
    PacketSender packetSender = new DefaultPacketSender(context.channel());
    packetSender.setName(s);
    return packetSender;
  };

  private static final boolean EPOLL = Epoll.isAvailable();

  private static final ThreadFactory THREAD_FACTORY = new DefaultThreadFactory(
      MultithreadEventExecutorGroup.class, true, Thread.MIN_PRIORITY);

  public static EventLoopGroup eventLoopGroup() {
    if (!Boolean.getBoolean("reformcloud.disable.native")) {
      if (EPOLL) {
        return new EpollEventLoopGroup(
            Runtime.getRuntime().availableProcessors(), THREAD_FACTORY);
      }
    }

    return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(),
                                 THREAD_FACTORY);
  }

  public static Class<? extends ServerSocketChannel> serverSocketChannel() {
    if (!Boolean.getBoolean("reformcloud.disable.native")) {
      if (EPOLL) {
        return EpollServerSocketChannel.class;
      }
    }

    return NioServerSocketChannel.class;
  }

  public static Class<? extends SocketChannel> socketChannel() {
    if (!Boolean.getBoolean("reformcloud.disable.native")) {
      if (EPOLL) {
        return EpollSocketChannel.class;
      }
    }

    return NioSocketChannel.class;
  }

  public static synchronized ByteBuf write(ByteBuf byteBuf, int value) {
    do {
      byte temp = (byte)(value & 0b01111111);
      value >>>= 7;
      if (value != 0) {
        temp |= 0b10000000;
      }
      byteBuf.writeByte(temp);
    } while (value != 0);

    return byteBuf;
  }

  public static synchronized int read(ByteBuf byteBuf) {
    int numRead = 0;
    int result = 0;
    byte read;
    do {
      read = byteBuf.readByte();
      int value = (read & 0b01111111);
      result |= (value << (7 * numRead));

      numRead++;
      if (numRead > 5) {
        throw new RuntimeException("VarInt is too big");
      }
    } while ((read & 0b10000000) != 0);

    return result;
  }

  public static synchronized ByteBuf write(ByteBuf byteBuf, String s) {
    byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
    write(byteBuf, bytes.length);
    byteBuf.writeBytes(bytes);
    return byteBuf;
  }

  public static synchronized String readString(ByteBuf byteBuf) {
    int size = read(byteBuf);
    byte[] bytes = new byte[size];
    byteBuf.readBytes(bytes, 0, size);
    return new String(bytes, StandardCharsets.UTF_8);
  }

  public static NetworkChannelReader
  newReader(PacketHandler packetHandler, Consumer<PacketSender> consumer) {
    return new NetworkChannelReader() {
      private PacketSender sender;

      @Nonnull
      @Override
      public PacketHandler getPacketHandler() {
        return packetHandler;
      }

      @Nonnull
      @Override
      public PacketSender sender() {
        return sender;
      }

      @Override
      public void setSender(PacketSender sender) {
        Conditions.isTrue(this.sender == null);
        this.sender = sender;
        DefaultChannelManager.INSTANCE.registerChannel(sender);
      }

      @Override
      public void channelActive(ChannelHandlerContext context) {
        if (sender == null) {
          String address =
              ((InetSocketAddress)context.channel().remoteAddress())
                  .getAddress()
                  .getHostAddress();
          System.out.println(
              LanguageManager.get("network-channel-connected", address));
        }
      }

      @Override
      public void channelInactive(ChannelHandlerContext context) {
        if (sender != null) {
          DefaultChannelManager.INSTANCE.unregisterChannel(sender);
          consumer.accept(sender);
          System.out.println(LanguageManager.get("network-channel-disconnected",
                                                 sender.getName()));
        }
      }

      @Override
      public void read(ChannelHandlerContext context, Packet packet) {
        NetworkUtil.EXECUTOR.execute(() -> {
          if (packet.queryUniqueID() != null &&
              getPacketHandler().getQueryHandler().hasWaitingQuery(
                  packet.queryUniqueID())) {
            getPacketHandler()
                .getQueryHandler()
                .getWaitingQuery(packet.queryUniqueID())
                .complete(packet);
          } else {
            getPacketHandler()
                .getNetworkHandlers(packet.packetID())
                .forEach(networkHandler
                         -> networkHandler.handlePacket(sender, packet, out -> {
                  if (packet.queryUniqueID() != null) {
                    out.setQueryID(packet.queryUniqueID());
                    sender.sendPacket(out);
                  }
                }));
          }
        });
      }
    };
  }

  private NetworkUtil() { throw new UnsupportedOperationException(); }
}
