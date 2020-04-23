package systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;

import java.net.InetSocketAddress;

public class DefaultPacketSender extends PacketSender {

    public DefaultPacketSender(ChannelHandlerContext channel) {
        this.channel = channel;
        this.connectionTime = System.currentTimeMillis();
    }

    private final ChannelHandlerContext channel;

    private final long connectionTime;

    private String name;

    @Override
    public long getConnectionTime() {
        return connectionTime;
    }

    @NotNull
    @Override
    public String getAddress() {
        return getEthernetAddress().getAddress().getHostAddress();
    }

    @NotNull
    @Override
    public InetSocketAddress getEthernetAddress() {
        return (InetSocketAddress) channel.channel().remoteAddress();
    }

    @Override
    public boolean isLoopBackSender() {
        return !getEthernetAddress().getAddress().isLoopbackAddress();
    }

    @Override
    public void sendPacket(@NotNull Object packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet);
        }
    }

    @Override
    public void sendPacketSync(@NotNull Object packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet).syncUninterruptibly();
        }
    }

    @Override
    public void sendPackets(@NotNull Object... packets) {
        if (isConnected()) {
            for (Object packet : packets) {
                sendPacket(packet);
            }
        }
    }

    @Override
    public void sendPacketsSync(@NotNull Object... packets) {
        if (isConnected()) {
            for (Object packet : packets) {
                sendPacketSync(packet);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.channel().isOpen();
    }

    @Override
    public void close() {
        channel.close();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String newName) {
        this.name = newName;
    }

    @NotNull
    @Override
    public ChannelHandlerContext getChannelContext() {
        return this.channel;
    }
}
