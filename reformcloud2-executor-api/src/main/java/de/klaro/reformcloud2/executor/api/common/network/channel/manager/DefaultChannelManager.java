package de.klaro.reformcloud2.executor.api.common.network.channel.manager;

import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannel;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DefaultChannelManager implements ChannelManager {

    public static final ChannelManager INSTANCE = new DefaultChannelManager();

    private final List<PacketSender> senders = new ArrayList<>();

    @Override
    public void registerChannel(PacketSender packetSender) {
        senders.add(packetSender);
    }

    @Override
    public void unregisterChannel(PacketSender packetSender) {
        PacketSender current = Links.filter(senders, sender -> packetSender.getName().equals(sender.getName()));
        if (current == null) {
            return;
        }

        senders.remove(current);
    }

    @Override
    public void unregisterAll() {
        senders.forEach(NetworkChannel::close);
        senders.clear();
    }

    @Override
    public Optional<PacketSender> get(String name) {
        return Links.filterToOptional(senders, packetSender -> packetSender.getName().equals(name));
    }

    @Override
    public List<PacketSender> getAllSender() {
        return new ArrayList<>(senders);
    }
}
