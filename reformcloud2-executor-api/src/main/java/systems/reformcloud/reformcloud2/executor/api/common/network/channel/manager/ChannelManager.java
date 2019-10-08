package systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.List;

public interface ChannelManager {

    void registerChannel(PacketSender packetSender);

    void unregisterChannel(PacketSender packetSender);

    void unregisterAll();

    ReferencedOptional<PacketSender> get(String name);

    List<PacketSender> getAllSender();
}
