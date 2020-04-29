package systems.reformcloud.reformcloud2.executor.controller.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerServerSwitchEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketPlayerServerSwitch;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIBungeePacketOutPlayerServerSwitch;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

public class PacketInAPIServerSwitchPlayer extends APIBungeePacketOutPlayerServerSwitch {

    public PacketInAPIServerSwitchPlayer() {
        super(null, null, null);
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ControllerExecutor.getInstance().getEventManager().callEvent(new PlayerServerSwitchEvent(
                super.playerUniqueID,
                super.playerName,
                super.originalServer,
                super.targetServer
        ));

        DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new EventPacketPlayerServerSwitch(
                super.playerUniqueID,
                super.playerName,
                super.originalServer,
                super.targetServer
        )));
    }
}
