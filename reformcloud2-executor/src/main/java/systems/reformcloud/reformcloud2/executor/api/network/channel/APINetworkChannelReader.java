package systems.reformcloud.reformcloud2.executor.api.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.shared.SharedNetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

public class APINetworkChannelReader extends SharedNetworkChannelReader {

    public APINetworkChannelReader(PacketHandler packetHandler) {
        super(packetHandler);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        if (packetSender != null) {
            DefaultChannelManager.INSTANCE.unregisterChannel(packetSender);
            System.out.println(LanguageManager.get("network-channel-disconnected", packetSender.getName()));
            System.exit(0);
        }
    }
}
