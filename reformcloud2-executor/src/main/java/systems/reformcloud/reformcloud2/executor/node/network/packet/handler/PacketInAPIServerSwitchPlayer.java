package systems.reformcloud.reformcloud2.executor.node.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerServerSwitchEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketPlayerServerSwitch;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIBungeePacketOutPlayerServerSwitch;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;

public class PacketInAPIServerSwitchPlayer extends APIBungeePacketOutPlayerServerSwitch {

    public PacketInAPIServerSwitchPlayer() {
        super(null, null, null);
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        NodeExecutor.getInstance().getEventManager().callEvent(new PlayerServerSwitchEvent(
                super.playerUniqueID,
                super.playerName,
                super.originalServer,
                super.targetServer
        ));

        DefaultClusterSyncManager.sendToAllExcludedNodes(new EventPacketPlayerServerSwitch(
                super.playerUniqueID,
                super.playerName,
                super.originalServer,
                super.targetServer
        ));
    }
}
