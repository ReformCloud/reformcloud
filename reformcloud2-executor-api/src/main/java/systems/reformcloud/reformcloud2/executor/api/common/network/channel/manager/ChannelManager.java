package systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.List;
import java.util.function.Consumer;

public interface ChannelManager {

    /**
     * Registers a network channel
     *
     * @param packetSender The network channel which should get registered
     */
    void registerChannel(PacketSender packetSender);

    /**
     * Unregisters a channel
     *
     * @param packetSender The packet sender of the channel which should get unregistered
     */
    void unregisterChannel(PacketSender packetSender);

    /**
     * Unregisters all channels
     */
    void unregisterAll();

    /**
     * @param name The name of the packet sender who should be found
     * @return A {@link ReferencedOptional} which is
     *  a) empty if the packet sender is not registered
     *  b) contains the packet sender if the channel is registered
     *
     * @see ReferencedOptional#isPresent()
     * @see ReferencedOptional#ifPresent(Consumer)
     */
    ReferencedOptional<PacketSender> get(String name);

    /**
     * @return All registered packet senders
     */
    List<PacketSender> getAllSender();
}
