package systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager;

import java.util.ArrayList;
import java.util.List;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

public final class DefaultChannelManager implements ChannelManager {

  public static final ChannelManager INSTANCE = new DefaultChannelManager();

  private final List<PacketSender> senders = new ArrayList<>();

  @Override
  public void registerChannel(PacketSender packetSender) {
    synchronized (senders) { senders.add(packetSender); }
  }

  @Override
  public void unregisterChannel(PacketSender packetSender) {
    synchronized (senders) {
      PacketSender current = Links.filter(
          senders, sender -> packetSender.getName().equals(sender.getName()));
      if (current == null) {
        return;
      }

      senders.remove(current);
    }
  }

  @Override
  public void unregisterAll() {
    synchronized (senders) {
      senders.forEach(NetworkChannel::close);
      senders.clear();
    }
  }

  @Override
  public ReferencedOptional<PacketSender> get(String name) {
    if (name == null) {
      return ReferencedOptional.empty();
    }

    synchronized (senders) {
      return Links.filterToReference(
          senders, packetSender -> packetSender.getName().equals(name));
    }
  }

  @Override
  public List<PacketSender> getAllSender() {
    return new ArrayList<>(senders);
  }
}
