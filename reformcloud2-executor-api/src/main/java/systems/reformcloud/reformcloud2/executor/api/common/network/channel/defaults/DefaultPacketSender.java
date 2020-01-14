package systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

public class DefaultPacketSender extends PacketSender {

    public DefaultPacketSender(ChannelHandlerContext channel) {
        this.channel = channel;
        this.connectionTime = System.currentTimeMillis();
    }

    private ChannelHandlerContext channel;

    private final long connectionTime;

    private String name;

    @Override
    public long getConnectionTime() {
        return connectionTime;
    }

    @Nonnull
    @Override
    public String getAddress() {
        return getEthernetAddress().getAddress().getHostAddress();
    }

    @Nonnull
    @Override
    public InetSocketAddress getEthernetAddress() {
        return (InetSocketAddress) channel.channel().remoteAddress();
    }

    @Override
    public boolean isLoopBackSender() {
        return !getEthernetAddress().getAddress().isLoopbackAddress();
    }

    @Override
    public void sendPacket(Packet packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet);
        }
    }

    @Override
    public void sendPacketSync(Packet packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet).syncUninterruptibly();
        }
    }

    @Override
    public void sendPackets(Packet... packets) {
        if (isConnected()) {
            for (Packet packet : packets) {
                sendPacket(packet);
            }
        }
    }

    @Override
    public void sendPacketsSync(Packet... packets) {
        if (isConnected()) {
            for (Packet packet : packets) {
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

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nonnull String newName) {
        this.name = newName;
    }

    @Nonnull
    @Override
    public ChannelHandlerContext getChannelContext() {
        return this.channel;
    }
}
