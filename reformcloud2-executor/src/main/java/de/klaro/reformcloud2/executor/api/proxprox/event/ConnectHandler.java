package de.klaro.reformcloud2.executor.api.proxprox.event;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.packets.out.APIPacketOutGetBestLobbyForPlayer;
import de.klaro.reformcloud2.executor.api.proxprox.ProxProxExecutor;
import io.gomint.proxprox.api.event.PlayerSwitchEvent;
import io.gomint.proxprox.api.plugin.event.EventHandler;
import io.gomint.proxprox.api.plugin.event.Listener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class ConnectHandler implements Listener {

    @EventHandler
    public void handleConnect(final PlayerSwitchEvent event) {
        if (event.getFrom() == null) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
                @Override
                public void accept(PacketSender packetSender) {
                    Packet result = ProxProxExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender,
                            new APIPacketOutGetBestLobbyForPlayer(new ArrayList<>(), 0)
                    ).getTask().getUninterruptedly(TimeUnit.SECONDS, 3);
                    if (result != null) {
                        ProcessInformation info = result.content().get("result", ProcessInformation.TYPE);
                        if (info != null && ProxProxExecutor.isServerKnown(info)) {
                            event.setTo(ProxProxExecutor.toServerDataHolder(info));
                        }
                    }
                }
            });
        }
    }
}
