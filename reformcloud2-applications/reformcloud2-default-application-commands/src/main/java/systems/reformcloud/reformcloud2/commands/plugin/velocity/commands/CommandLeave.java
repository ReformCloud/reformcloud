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
package systems.reformcloud.reformcloud2.commands.plugin.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.shared.SharedPlayerFallbackFilter;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;
import systems.reformcloud.reformcloud2.executor.api.velocity.fallback.VelocityFallbackExtraFilter;

import java.util.List;
import java.util.Optional;

public class CommandLeave implements Command {

    private final List<String> aliases;

    public CommandLeave(@NotNull List<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public void execute(CommandSource commandSource, @NotNull String[] strings) {
        if (!(commandSource instanceof Player)) {
            return;
        }

        Player player = (Player) commandSource;
        if (!player.getCurrentServer().isPresent()) {
            player.sendMessage(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                    VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
            )));
            return;
        }

        if (VelocityExecutor.getInstance().getCachedLobbyServices().stream().anyMatch(
                e -> e.getProcessDetail().getName().equals(player.getCurrentServer().get().getServerInfo().getName())
        )) {
            player.sendMessage(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                    VelocityExecutor.getInstance().getMessages().getAlreadyConnectedToHub()
            )));
            return;
        }

        SharedPlayerFallbackFilter.filterFallback(
                player.getUniqueId(),
                VelocityExecutor.getInstance().getCachedLobbyServices(),
                player::hasPermission,
                VelocityFallbackExtraFilter.INSTANCE,
                null // ignored because we are sure the player is not on a lobby
        ).ifPresent(processInformation -> {
            Optional<RegisteredServer> lobby = VelocityExecutor.getInstance().getProxyServer().getServer(processInformation.getProcessDetail().getName());
            if (!lobby.isPresent()) {
                player.sendMessage(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                        VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
                )));
                return;
            }

            player.sendMessage(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                    VelocityExecutor.getInstance().getMessages().getConnectingToHub(), processInformation.getProcessDetail().getName()
            )));
            player.createConnectionRequest(lobby.get()).fireAndForget();
        }).ifEmpty(v -> player.sendMessage(LegacyComponentSerializer.legacyLinking().deserialize(VelocityExecutor.getInstance().getMessages().format(
                VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
        ))));
    }

    @NotNull
    public List<String> getAliases() {
        return this.aliases;
    }
}
