package de.klaro.reformcloud2.executor.api.velocity.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.packets.out.APIPacketOutGetBestLobbyForPlayer;
import de.klaro.reformcloud2.executor.api.velocity.VelocityExecutor;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class ConnectHandler {

    @Subscribe
    public void handleConnect(final ServerPreConnectEvent event) {
        final Player player = event.getPlayer();
        if (!player.getCurrentServer().isPresent()) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
                @Override
                public void accept(PacketSender packetSender) {
                    Packet result = VelocityExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender,
                            new APIPacketOutGetBestLobbyForPlayer(new ArrayList<>(), Version.VELOCITY)
                    ).getTask().getUninterruptedly(TimeUnit.SECONDS, 3);
                    if (result != null) {
                        ProcessInformation info = result.content().get("result", ProcessInformation.TYPE);
                        if (info != null && VelocityExecutor.getInstance().isServerRegistered(info.getName())) {
                            event.setResult(ServerPreConnectEvent.ServerResult.allowed(
                                    VelocityExecutor.getInstance().getProxyServer().getServer(info.getName()).get()
                            ));
                        }
                    }
                }
            });
        }
    }
}
