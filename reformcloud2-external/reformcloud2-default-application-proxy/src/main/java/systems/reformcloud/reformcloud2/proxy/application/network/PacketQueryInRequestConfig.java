package systems.reformcloud.reformcloud2.proxy.application.network;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.proxy.application.ConfigHelper;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PacketQueryInRequestConfig implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.EXTERNAL_BUS + 2;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", ConfigHelper.getProxyConfiguration())));
    }
}
