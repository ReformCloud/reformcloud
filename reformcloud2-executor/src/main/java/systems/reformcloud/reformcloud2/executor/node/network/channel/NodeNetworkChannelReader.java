package systems.reformcloud.reformcloud2.executor.node.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.shared.SharedNetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public class NodeNetworkChannelReader extends SharedNetworkChannelReader {

    public NodeNetworkChannelReader(PacketHandler packetHandler) {
        super(packetHandler);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        if (packetSender != null) {
            DefaultChannelManager.INSTANCE.unregisterChannel(packetSender);
            System.out.println(LanguageManager.get("network-channel-disconnected", packetSender.getName()));
            NodeExecutor.getInstance().handleChannelDisconnect(this.packetSender);
        }
    }
}
