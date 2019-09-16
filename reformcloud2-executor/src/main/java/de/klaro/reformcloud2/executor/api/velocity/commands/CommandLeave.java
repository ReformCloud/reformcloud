package de.klaro.reformcloud2.executor.api.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.packets.out.APIPacketOutGetBestLobbyForPlayer;
import de.klaro.reformcloud2.executor.api.velocity.VelocityExecutor;
import net.kyori.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CommandLeave implements Command {

    @Override
    public void execute(CommandSource commandSource, @NonNull String[] strings) {
        if (!(commandSource instanceof Player)) {
            return;
        }

        final Player player = (Player) commandSource;
        if (ExecutorAPI.getInstance().getThisProcessInformation().isLobby()) {
            player.sendMessage(TextComponent.of(
                    VelocityExecutor.getInstance().getMessages().format(
                            VelocityExecutor.getInstance().getMessages().getAlreadyConnectedToHub()
                    )
            ));
            return;
        }

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> {
            Packet result = VelocityExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender,
                    new APIPacketOutGetBestLobbyForPlayer(new ArrayList<>(), Version.VELOCITY)
            ).getTask().getUninterruptedly(TimeUnit.SECONDS, 3);
            if (result != null) {
                ProcessInformation info = result.content().get("result", ProcessInformation.TYPE);
                if (info != null && VelocityExecutor.getInstance().isServerRegistered(info.getName())) {
                    player.sendMessage(TextComponent.of(
                            VelocityExecutor.getInstance().getMessages().format(
                                    VelocityExecutor.getInstance().getMessages().getConnectingToHub(), info.getName()
                            )
                    ));
                    player.createConnectionRequest(VelocityExecutor.getInstance().getProxyServer().getServer(info.getName()).get()).fireAndForget();
                    return;
                }

                player.sendMessage(TextComponent.of(
                        VelocityExecutor.getInstance().getMessages().format(
                                VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
                        )
                ));
            }
        });
    }
}
