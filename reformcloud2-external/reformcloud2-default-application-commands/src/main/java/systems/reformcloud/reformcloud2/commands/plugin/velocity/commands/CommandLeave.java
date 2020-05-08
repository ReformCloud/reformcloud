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
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

import java.util.List;
import java.util.Optional;

public class CommandLeave implements Command {

    public CommandLeave(@NotNull List<String> aliases) {
        this.aliases = aliases;
    }

    private final List<String> aliases;

    @Override
    public void execute(CommandSource commandSource, @NotNull String[] strings) {
        if (!(commandSource instanceof Player)) {
            return;
        }

        final Player player = (Player) commandSource;

        Optional<ServerConnection> currentServer = player.getCurrentServer();
        ProcessInformation process = null;
        if (currentServer.isPresent()) {
            process = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(currentServer.get().getServerInfo().getName());
        }

        if (!currentServer.isPresent() || process == null || process.isLobby()) {
            player.sendMessage(TextComponent.of(
                    VelocityExecutor.getInstance().getMessages().format(
                            VelocityExecutor.getInstance().getMessages().getAlreadyConnectedToHub()
                    )
            ));
            return;
        }

        ProcessInformation lobby = VelocityExecutor.getBestLobbyForPlayer(
                API.getInstance().getCurrentProcessInformation(),
                player::hasPermission,
                null
        );
        if (lobby != null) {
            player.sendMessage(TextComponent.of(
                    VelocityExecutor.getInstance().getMessages().format(
                            VelocityExecutor.getInstance().getMessages().getConnectingToHub(), lobby.getProcessDetail().getName()
                    )
            ));
            VelocityExecutor.getInstance().getProxyServer().getServer(lobby.getProcessDetail().getName())
                    .ifPresent(e -> player.createConnectionRequest(e).fireAndForget());
            return;
        }

        player.sendMessage(TextComponent.of(
                VelocityExecutor.getInstance().getMessages().format(
                        VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
                )
        ));
    }

    @NotNull
    public List<String> getAliases() {
        return aliases;
    }
}
