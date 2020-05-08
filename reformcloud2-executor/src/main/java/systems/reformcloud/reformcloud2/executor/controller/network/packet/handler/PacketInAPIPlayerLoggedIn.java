package systems.reformcloud.reformcloud2.executor.controller.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerLoginEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketPlayerConnected;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutPlayerLoggedIn;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

public class PacketInAPIPlayerLoggedIn extends APIPacketOutPlayerLoggedIn {

    public PacketInAPIPlayerLoggedIn() {
        super(null);
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        System.out.println(LanguageManager.get(
                "player-logged-in",
                super.playerName,
                sender.getName()
        ));

        ControllerExecutor.getInstance().getEventManager().callEvent(new PlayerLoginEvent(super.playerName));
        DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new EventPacketPlayerConnected(super.playerName)));
    }
}
