package systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultChannelManager implements ChannelManager {

    public static final ChannelManager INSTANCE = new DefaultChannelManager();

    private final List<PacketSender> senders = new CopyOnWriteArrayList<>();

    @Override
    public void registerChannel(PacketSender packetSender) {
        senders.add(packetSender);
    }

    @Override
    public void unregisterChannel(PacketSender packetSender) {
        PacketSender current = Streams.filter(senders, sender -> packetSender.getName().equals(sender.getName()));
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
    public ReferencedOptional<PacketSender> get(String name) {
        if (name == null) {
            return ReferencedOptional.empty();
        }

        return Streams.filterToReference(senders, packetSender -> packetSender.getName().equals(name));
    }

    @Override
    public List<PacketSender> getAllSender() {
        return Collections.unmodifiableList(senders);
    }
}
