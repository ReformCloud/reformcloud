package de.klaro.reformcloud2.executor.api.common.network.channel.defaults;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class DefaultPacketSender extends PacketSender {

    public DefaultPacketSender(Channel channel) {
        this.channel = channel;
        this.connectionTime = System.currentTimeMillis();
    }

    private Channel channel;

    private final long connectionTime;

    private String name;

    @Override
    public long getConnectionTime() {
        return connectionTime;
    }

    @Override
    public String getAddress() {
        return getEthernetAddress().getAddress().getHostAddress();
    }

    @Override
    public InetSocketAddress getEthernetAddress() {
        return (InetSocketAddress) channel.remoteAddress();
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
        return channel != null && channel.isOpen();
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String newName) {
        this.name = newName;
    }
}
