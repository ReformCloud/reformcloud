package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import java.net.InetSocketAddress;

public abstract class PacketSender implements NetworkChannel {

    /**
     * @return The connection time of the packet sender
     */
    public abstract long getConnectionTime();

    /**
     * @return The ip-address if the channel
     */
    public abstract String getAddress();

    /**
     * @return The socket address of the channel
     */
    public abstract InetSocketAddress getEthernetAddress();
}
