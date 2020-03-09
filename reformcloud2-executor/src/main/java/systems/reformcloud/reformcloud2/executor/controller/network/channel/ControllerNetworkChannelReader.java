package systems.reformcloud.reformcloud2.executor.controller.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.shared.SharedNetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

public class ControllerNetworkChannelReader extends SharedNetworkChannelReader {

    public ControllerNetworkChannelReader(PacketHandler packetHandler) {
        super(packetHandler);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        if (packetSender != null) {
            DefaultChannelManager.INSTANCE.unregisterChannel(packetSender);
            System.out.println(LanguageManager.get("network-channel-disconnected", packetSender.getName()));
            ControllerExecutor.getInstance().handleChannelDisconnect(this.packetSender);
        }
    }
}
