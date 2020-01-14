package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public interface NetworkChannel extends DirectIdentifiableChannel {

    /**
     * Sends a packet into the channel
     *
     * @param packet The packet which should get sent
     */
    void sendPacket(Packet packet);

    /**
     * Sends a packet sync into the channel
     *
     * @param packet The packet which should get sent
     */
    void sendPacketSync(Packet packet);

    /**
     * Sends many packets into the channel
     *
     * @param packets The packets which should be sent
     */
    void sendPackets(Packet... packets);

    /**
     * Sends many sync packets into the channel
     *
     * @param packets The packets which should be sent
     */
    void sendPacketsSync(Packet... packets);

    /**
     * @return If the current network channel is connected
     */
    boolean isConnected();

    /**
     * @see #isConnected()
     * Closes the current network channel
     */
    void close();
}
