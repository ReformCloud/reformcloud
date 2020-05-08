package systems.reformcloud.reformcloud2.executor.node.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerLogoutEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketLogoutPlayer;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutLogoutPlayer;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;

public class PacketInAPILogoutPlayer extends APIPacketOutLogoutPlayer {

    public PacketInAPILogoutPlayer() {
        super(null, null, null);
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        System.out.println(LanguageManager.get(
                "player-logged-out",
                super.playerName,
                super.playerUniqueID,
                sender.getName(),
                super.lastServer == null ? "unknown" : super.lastServer
        ));

        NodeExecutor.getInstance().getEventManager().callEvent(new PlayerLogoutEvent(super.playerName, super.playerUniqueID, super.lastServer));
        DefaultClusterSyncManager.sendToAllExcludedNodes(new EventPacketLogoutPlayer(super.playerName, super.playerUniqueID, super.lastServer));
    }
}
