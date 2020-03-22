package systems.reformcloud.reformcloud2.proxy.network;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.proxy.ProxyConfiguration;
import systems.reformcloud.reformcloud2.proxy.plugin.PluginConfigHandler;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PacketInConfigUpdate extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.EXTERNAL_BUS + 3;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        ProxyConfiguration configuration = packet.content().get("config", ProxyConfiguration.TYPE);
        if (configuration != null) {
            PluginConfigHandler.setConfiguration(configuration);
        }
    }
}
