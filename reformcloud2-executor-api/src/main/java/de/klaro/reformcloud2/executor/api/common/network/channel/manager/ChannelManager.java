package de.klaro.reformcloud2.executor.api.common.network.channel.manager;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;

import java.util.List;
import java.util.Optional;

public interface ChannelManager {

    void registerChannel(PacketSender packetSender);

    void unregisterChannel(PacketSender packetSender);

    void unregisterAll();

    Optional<PacketSender> get(String name);

    List<PacketSender> getAllSender();
}
