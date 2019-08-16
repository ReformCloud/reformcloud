package de.klaro.reformcloud2.executor.api.common.network.channel;

import java.net.InetSocketAddress;

public abstract class PacketSender implements NetworkChannel {

    public abstract long getConnectionTime();

    public abstract String getAddress();

    public abstract InetSocketAddress getEthernetAddress();
}
