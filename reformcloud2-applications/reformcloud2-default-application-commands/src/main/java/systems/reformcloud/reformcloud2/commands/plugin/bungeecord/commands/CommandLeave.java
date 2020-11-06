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
package systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.controller.ProxyServerController;
import systems.refomcloud.reformcloud2.embedded.plugin.bungee.fallback.BungeeFallbackExtraFilter;
import systems.refomcloud.reformcloud2.embedded.shared.SharedPlayerFallbackFilter;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;

import java.util.List;

public class CommandLeave extends Command {

    public CommandLeave(@NotNull String name, @NotNull List<String> aliases) {
        super(name, null, aliases.toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        if (proxiedPlayer.getServer() == null) {
            return;
        }

        if (ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class).getCachedLobbyServers().stream().anyMatch(
            e -> e.getProcessDetail().getName().equals(proxiedPlayer.getServer().getInfo().getName()))
        ) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Embedded.getInstance().getIngameMessages().format(
                Embedded.getInstance().getIngameMessages().getAlreadyConnectedToHub()
            )));
            return;
        }

        SharedPlayerFallbackFilter.filterFallback(
            proxiedPlayer.getUniqueId(),
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class).getCachedLobbyServers(),
            proxiedPlayer::hasPermission,
            BungeeFallbackExtraFilter.INSTANCE,
            proxiedPlayer.getServer().getInfo().getName()
        ).ifPresentOrElse(processInformation -> {
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(processInformation.getProcessDetail().getName());
            if (serverInfo == null) {
                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Embedded.getInstance().getIngameMessages().format(
                    Embedded.getInstance().getIngameMessages().getNoHubServerAvailable()
                )));
                return;
            }

            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Embedded.getInstance().getIngameMessages().format(
                Embedded.getInstance().getIngameMessages().getConnectingToHub(), processInformation.getProcessDetail().getName()
            )));
            proxiedPlayer.connect(serverInfo);
        }, () -> proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Embedded.getInstance().getIngameMessages().format(
            Embedded.getInstance().getIngameMessages().getNoHubServerAvailable()
        ))));
    }
}
