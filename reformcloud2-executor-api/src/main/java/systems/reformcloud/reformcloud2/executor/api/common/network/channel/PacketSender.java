package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public abstract class PacketSender implements NetworkChannel {

    /**
     * @return The connection time of the packet sender
     */
    public abstract long getConnectionTime();

    /**
     * @return The ip-address if the channel
     */
    @NotNull
    public abstract String getAddress();

    /**
     * @return The socket address of the channel
     */
    @NotNull
    public abstract InetSocketAddress getEthernetAddress();

    /**
     * @return If the current channel address is a loopback address
     */
    public abstract boolean isLoopBackSender();
}
