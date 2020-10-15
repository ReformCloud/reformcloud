/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.refomcloud.reformcloud2.embedded.plugin.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.controller.ProxyServerController;
import systems.refomcloud.reformcloud2.embedded.plugin.velocity.VelocityExecutor;
import systems.refomcloud.reformcloud2.embedded.plugin.velocity.fallback.VelocityFallbackExtraFilter;
import systems.refomcloud.reformcloud2.embedded.shared.SharedJoinAllowChecker;
import systems.refomcloud.reformcloud2.embedded.shared.SharedPlayerFallbackFilter;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
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
                this.getServerController().getCachedLobbyServers(),
                event.getPlayer()::hasPermission,
                VelocityFallbackExtraFilter.INSTANCE,
                null
            ).ifPresent(processInformation -> {
                Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(processInformation.getProcessDetail().getName());
                if (!server.isPresent()) {
                    event.getPlayer().disconnect(VelocityExecutor.SERIALIZER.deserialize(VelocityExecutor.getInstance().getIngameMessages().format(
                        VelocityExecutor.getInstance().getIngameMessages().getNoHubServerAvailable()
                    )));
                    return;
                }

                event.setResult(ServerPreConnectEvent.ServerResult.allowed(server.get()));
            }).ifEmpty(v -> {
                event.getPlayer().disconnect(VelocityExecutor.SERIALIZER.deserialize(VelocityExecutor.getInstance().getIngameMessages().format(
                    VelocityExecutor.getInstance().getIngameMessages().getNoHubServerAvailable()
                )));
            });
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final @NotNull LoginEvent event) {
        if (!Embedded.getInstance().isReady()) {
            event.setResult(ResultedEvent.ComponentResult.denied(VelocityExecutor.SERIALIZER.deserialize(VelocityExecutor.getInstance().getIngameMessages().format(
                VelocityExecutor.getInstance().getIngameMessages().getProcessNotReadyToAcceptPlayersMessage()
            ))));
            return;
        }

        Duo<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
            event.getPlayer()::hasPermission,
            VelocityExecutor.getInstance().getIngameMessages(),
            this.getServerController().getCachedProxies(),
            event.getPlayer().getUniqueId(),
            event.getPlayer().getUsername()
        );
        if (!checked.getFirst() && checked.getSecond() != null) {
            event.setResult(ResultedEvent.ComponentResult.denied(VelocityExecutor.SERIALIZER.deserialize(checked.getSecond())));
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final @NotNull KickedFromServerEvent event) {
        SharedPlayerFallbackFilter.filterFallback(
            event.getPlayer().getUniqueId(),
            this.getServerController().getCachedLobbyServers(),
            event.getPlayer()::hasPermission,
            VelocityFallbackExtraFilter.INSTANCE,
            event.getPlayer().getCurrentServer().map(e -> e.getServerInfo().getName()).orElse(null)
        ).ifPresent(processInformation -> {
            Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(processInformation.getProcessDetail().getName());
            if (!server.isPresent()) {
                event.setResult(KickedFromServerEvent.DisconnectPlayer.create(VelocityExecutor.SERIALIZER.deserialize(
                    VelocityExecutor.getInstance().getIngameMessages().format(VelocityExecutor.getInstance().getIngameMessages().getNoHubServerAvailable())
                )));
                return;
            }

            event.setResult(KickedFromServerEvent.RedirectPlayer.create(server.get()));
            event.getServerKickReason().ifPresent(reason -> event.getPlayer().sendMessage(Identity.nil(), reason));
        }).ifEmpty(v -> event.setResult(KickedFromServerEvent.DisconnectPlayer.create(VelocityExecutor.SERIALIZER.deserialize(
            VelocityExecutor.getInstance().getIngameMessages().format(VelocityExecutor.getInstance().getIngameMessages().getNoHubServerAvailable())
        ))));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final DisconnectEvent event) {
        VelocityExecutor.getInstance().getProxyServer().getScheduler().buildTask(VelocityExecutor.getInstance().getPlugin(), () -> {
            ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
            if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() < current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                current.getProcessDetail().setProcessState(ProcessState.READY);
            }

            current.updateRuntimeInformation();
            current.getProcessPlayerManager().onLogout(event.getPlayer().getUniqueId());

            Embedded.getInstance().updateCurrentProcessInformation();
        }).delay(20, TimeUnit.MILLISECONDS).schedule();
    }

    private @NotNull ProxyServerController getServerController() {
        return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class);
    }
}
