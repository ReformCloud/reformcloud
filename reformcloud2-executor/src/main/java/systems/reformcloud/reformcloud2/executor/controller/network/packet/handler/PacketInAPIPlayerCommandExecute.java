package systems.reformcloud.reformcloud2.executor.controller.network.packet.handler;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutPlayerCommandExecute;

public class PacketInAPIPlayerCommandExecute extends APIPacketOutPlayerCommandExecute {

    public PacketInAPIPlayerCommandExecute() {
        super(null, null, null);
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        System.out.println(LanguageManager.get(
                "player-executed-command",
                super.playerName,
                super.playerUniqueID,
                super.command,
                sender.getName()
        ));
    }
}
