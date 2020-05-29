/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.refomcloud.reformcloud2.embedded.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIBungeePacketOutPlayerServerSwitch;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIPacketOutLogoutPlayer;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIPacketOutPlayerLoggedIn;
import systems.refomcloud.reformcloud2.embedded.shared.SharedJoinAllowChecker;
import systems.refomcloud.reformcloud2.embedded.shared.SharedPlayerFallbackFilter;
import systems.refomcloud.reformcloud2.embedded.velocity.VelocityExecutor;
import systems.refomcloud.reformcloud2.embedded.velocity.fallback.VelocityFallbackExtraFilter;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler {

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final @NotNull ServerPreConnectEvent event) {
        if (!event.getPlayer().getCurrentServer().isPresent()) {
            SharedPlayerFallbackFilter.filterFallback(
                    event.getPlayer().getUniqueId(),
                    VelocityExecutor.getInstance().getCachedLobbyServices(),
                    event.getPlayer()::hasPermission,
                    VelocityFallbackExtraFilter.INSTANCE,
                    null
            ).ifPresent(processInformation -> {
                Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(processInformation.getProcessDetail().getName());
                if (!server.isPresent()) {
                    System.out.println("Unable to find reg server");
                    event.getPlayer().disconnect(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                            VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
                    )));
                    return;
                }

                System.out.println("redirect");
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(server.get()));
            }).ifEmpty(v -> {
                System.out.println("nop");
                event.getPlayer().disconnect(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                        VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
                )));
            });
        }

        if (event.getResult().getServer().isPresent()) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(new APIBungeePacketOutPlayerServerSwitch(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getCurrentServer().isPresent() ? event.getPlayer().getCurrentServer().get().getServerInfo().getName() : null,
                    event.getResult().getServer().get().getServerInfo().getName()
            )));
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final @NotNull LoginEvent event) {
        PacketSender sender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
        if (sender == null) {
            event.setResult(ResultedEvent.ComponentResult.denied(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                    VelocityExecutor.getInstance().getMessages().getProcessNotReadyToAcceptPlayersMessage()
            ))));
            return;
        }

        Duo<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
                event.getPlayer()::hasPermission,
                VelocityExecutor.getInstance().getMessages(),
                VelocityExecutor.getInstance().getCachedProxyServices(),
                event.getPlayer().getUniqueId(),
                event.getPlayer().getUsername()
        );
        if (!checked.getFirst() && checked.getSecond() != null) {
            event.setResult(ResultedEvent.ComponentResult.denied(LegacyComponentSerializer.legacyLinking().deserialize(checked.getSecond())));
        }
    }

    @Subscribe
    public void handle(final PostLoginEvent event) {
        CommonHelper.EXECUTOR.execute(() -> DefaultChannelManager.INSTANCE
                .get("Controller")
                .ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutPlayerLoggedIn(event.getPlayer().getUsername())))
        );
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final @NotNull KickedFromServerEvent event) {
        SharedPlayerFallbackFilter.filterFallback(
                event.getPlayer().getUniqueId(),
                VelocityExecutor.getInstance().getCachedLobbyServices(),
                event.getPlayer()::hasPermission,
                VelocityFallbackExtraFilter.INSTANCE,
                event.getPlayer().getCurrentServer().map(e -> e.getServerInfo().getName()).orElse(null)
        ).ifPresent(processInformation -> {
            Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(processInformation.getProcessDetail().getName());
            if (!server.isPresent()) {
                event.setResult(KickedFromServerEvent.DisconnectPlayer.create(LegacyComponentSerializer.legacyLinking().deserialize(
                        VelocityExecutor.getInstance().getMessages().format(VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable())
                )));
                return;
            }

            event.setResult(KickedFromServerEvent.RedirectPlayer.create(server.get()));
            event.getOriginalReason().ifPresent(event.getPlayer()::sendMessage);
        }).ifEmpty(v -> event.setResult(KickedFromServerEvent.DisconnectPlayer.create(LegacyComponentSerializer.legacyLinking().deserialize(
                VelocityExecutor.getInstance().getMessages().format(VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable())
        ))));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final DisconnectEvent event) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutLogoutPlayer(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getUsername(),
                event.getPlayer().getCurrentServer().isPresent() ? event.getPlayer().getCurrentServer().get().getServerInfo().getName() : null
        )));

        VelocityExecutor.getInstance().getProxyServer().getScheduler().buildTask(VelocityExecutor.getInstance().getPlugin(), () -> {
            ProcessInformation current = API.getInstance().getCurrentProcessInformation();
            if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() < current.getProcessDetail().getMaxPlayers()
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                current.getProcessDetail().setProcessState(ProcessState.READY);
            }

            current.updateRuntimeInformation();
            current.getProcessPlayerManager().onLogout(event.getPlayer().getUniqueId());
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
        }).delay(20, TimeUnit.MILLISECONDS).schedule();
    }

    /* todo: wait until velocity 1.1.0 is published. The chat event is not fired when the player types in a command
       todo: see https://github.com/VelocityPowered/Velocity/blob/10680f16d376371401f4a203d94c888b22f24f14/proxy/src/main/java/com/velocitypowered/proxy/connection/client/ClientPlaySessionHandler.java#L115-L127
    @Subscribe
    public void handle(final PlayerChatEvent event) {
        if (API.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isPlayerControllerCommandReporting()
                && event.getMessage().startsWith("/")) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutPlayerCommandExecute(
                    event.getPlayer().getUsername(),
                    event.getPlayer().getUniqueId(),
                    event.getMessage().replaceFirst("/", "")
            )));
        }
    }*/

}
