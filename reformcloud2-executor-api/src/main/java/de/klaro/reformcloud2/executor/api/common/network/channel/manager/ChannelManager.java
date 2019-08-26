package de.klaro.reformcloud2.executor.api.common.network.channel.manager;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;

public interface ChannelManager {

    void registerChannel(PacketSender packetSender);

    void unregisterChannel(PacketSender packetSender);

    void unregisterAll();

    PacketSender get(String name);
}
