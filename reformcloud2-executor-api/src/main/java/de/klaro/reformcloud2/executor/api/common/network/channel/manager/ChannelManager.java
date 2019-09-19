package de.klaro.reformcloud2.executor.api.common.network.channel.manager;

import com.google.common.annotations.Beta;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.List;

public interface ChannelManager {

    void registerChannel(PacketSender packetSender);

    void unregisterChannel(PacketSender packetSender);

    void unregisterAll();

    @Beta
    ReferencedOptional<PacketSender> get(String name);

    List<PacketSender> getAllSender();
}
