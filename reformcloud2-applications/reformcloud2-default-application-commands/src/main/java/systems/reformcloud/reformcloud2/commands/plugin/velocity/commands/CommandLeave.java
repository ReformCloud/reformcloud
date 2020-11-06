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
package systems.reformcloud.reformcloud2.commands.plugin.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.controller.ProxyServerController;
import systems.refomcloud.reformcloud2.embedded.plugin.velocity.VelocityExecutor;
import systems.refomcloud.reformcloud2.embedded.plugin.velocity.fallback.VelocityFallbackExtraFilter;
import systems.refomcloud.reformcloud2.embedded.shared.SharedPlayerFallbackFilter;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;

import java.util.List;
import java.util.Optional;

public class CommandLeave implements SimpleCommand {

    private final List<String> aliases;

    public CommandLeave(@NotNull List<String> aliases) {
        this.aliases = aliases;
    }

    @NotNull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            return;
        }

        Player player = (Player) invocation.source();
        if (player.getCurrentServer().isEmpty()) {
            player.sendMessage(Identity.nil(), VelocityExecutor.SERIALIZER.deserialize(Embedded.getInstance().getIngameMessages().format(
                Embedded.getInstance().getIngameMessages().getNoHubServerAvailable()
            )));
            return;
        }

        if (ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class).getCachedLobbyServers().stream().anyMatch(
            e -> e.getProcessDetail().getName().equals(player.getCurrentServer().get().getServerInfo().getName())
        )) {
            player.sendMessage(Identity.nil(), VelocityExecutor.SERIALIZER.deserialize(Embedded.getInstance().getIngameMessages().format(
                Embedded.getInstance().getIngameMessages().getAlreadyConnectedToHub()
            )));
            return;
        }

        SharedPlayerFallbackFilter.filterFallback(
            player.getUniqueId(),
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class).getCachedLobbyServers(),
            player::hasPermission,
            VelocityFallbackExtraFilter.INSTANCE,
            null // ignored because we are sure the player is not on a lobby
        ).ifPresentOrElse(processInformation -> {
            Optional<RegisteredServer> lobby = VelocityExecutor.getInstance().getProxyServer().getServer(processInformation.getProcessDetail().getName());
            if (lobby.isEmpty()) {
                player.sendMessage(Identity.nil(), VelocityExecutor.SERIALIZER.deserialize(Embedded.getInstance().getIngameMessages().format(
                    Embedded.getInstance().getIngameMessages().getNoHubServerAvailable()
                )));
                return;
            }

            player.sendMessage(Identity.nil(), VelocityExecutor.SERIALIZER.deserialize(Embedded.getInstance().getIngameMessages().format(
                Embedded.getInstance().getIngameMessages().getConnectingToHub(), processInformation.getProcessDetail().getName()
            )));
            player.createConnectionRequest(lobby.get()).fireAndForget();
        }, () -> player.sendMessage(Identity.nil(), VelocityExecutor.SERIALIZER.deserialize(Embedded.getInstance().getIngameMessages().format(
            Embedded.getInstance().getIngameMessages().getNoHubServerAvailable()
        ))));
    }
}
