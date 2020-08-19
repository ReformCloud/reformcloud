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
package systems.reformcloud.reformcloud2.node.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.process.Player;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Trio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class CommandPlayers implements Command {

    private static final String FORMAT_STRING = " > %s/%s is connected to %s <-> %s";

    public void describeCommandToSender(@NotNull CommandSender source) {
        source.sendMessages((
                "players [list]               | Lists all connected players\n" +
                        "players <name | uuid> [info] | Shows information about a specific online player"
        ).split("\n"));
    }

    @Override
    public void process(@NotNull CommandSender sender, String[] strings, @NotNull String commandLine) {
        if (strings.length == 0) {
            this.describeCommandToSender(sender);
            return;
        }

        if (strings[0].equalsIgnoreCase("list")) {
            sender.sendMessage("Online-Players: ");
            ExecutorAPI.getInstance().getProcessProvider().getProcesses()
                    .stream()
                    .filter(e -> !e.getProcessDetail().getTemplate().isServer())
                    .map(e -> e.getProcessPlayerManager().getOnlinePlayers())
                    .map(players -> players.stream().map(p -> this.findPlayer(p.getUniqueID())).filter(Objects::nonNull))
                    .forEach(trioStream -> {
                        StringBuilder stringBuilder = new StringBuilder();
                        trioStream.filter(e -> e.getThird() != null).forEach(player -> stringBuilder.append(String.format(
                                FORMAT_STRING,
                                player.getThird().getName(),
                                player.getThird().getUniqueID().toString(),
                                player.getSecond().getProcessDetail().getName(),
                                player.getFirst().getProcessDetail().getName()
                        )).append("\n"));

                        if (stringBuilder.length() > 0) {
                            sender.sendMessages(stringBuilder.substring(0, stringBuilder.length() - 1).split("\n"));
                        }
                    });
            return;
        }

        if (strings.length == 2 && strings[1].equalsIgnoreCase("info")) {
            Trio<ProcessInformation, ProcessInformation, Player> trio;
            UUID uniqueID;
            if ((uniqueID = CommonHelper.tryParse(strings[0])) != null) {
                trio = this.findPlayer(uniqueID);
            } else {
                trio = this.findPlayer(strings[0]);
            }

            if (trio == null) {
                sender.sendMessage(LanguageManager.get("command-players-player-not-found", strings[0]));
                return;
            }

            Player subServerPlayer = trio.getFirst().getProcessPlayerManager().getOnlinePlayers().stream().filter(e -> uniqueID == null
                    ? e.getName().equals(strings[0]) : e.getUniqueID().equals(uniqueID)).findAny().orElse(null);
            if (subServerPlayer == null) {
                sender.sendMessage(LanguageManager.get("command-players-player-not-found", strings[0]));
                return;
            }

            AtomicReference<StringBuilder> stringBuilder = new AtomicReference<>(new StringBuilder());
            stringBuilder.get().append(" > Name               - ").append(trio.getThird().getName()).append("\n");
            stringBuilder.get().append(" > UUID               - ").append(trio.getThird().getUniqueID()).append("\n");
            stringBuilder.get().append(" > Proxy              - ").append(trio.getSecond().getProcessDetail().getName()).append("\n");
            stringBuilder.get().append(" > Connected (Proxy)  - ").append(CommonHelper.DATE_FORMAT.format(trio.getThird().getJoined())).append("\n");
            stringBuilder.get().append(" > Server             - ").append(trio.getFirst().getProcessDetail().getName()).append("\n");
            stringBuilder.get().append(" > Connected (Server) - ").append(CommonHelper.DATE_FORMAT.format(subServerPlayer.getJoined())).append("\n");
            sender.sendMessages(stringBuilder.get().toString().split("\n"));
            return;
        }

        this.describeCommandToSender(sender);
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender commandSender, String[] strings, int bufferIndex, @NotNull String commandLine) {
        List<String> result = new ArrayList<>();
        switch (bufferIndex) {
            case 0:
                result.add("list");
                break;
            case 1:
                result.add("info");
                break;
            default:
                break;
        }

        return result;
    }

    @Nullable
    private Trio<ProcessInformation, ProcessInformation, Player> findPlayer(UUID uniqueID) {
        ProcessInformation server = this.getProcess(null, uniqueID, false);
        ProcessInformation proxy = this.getProcess(null, uniqueID, true);
        return server == null || proxy == null
                ? null
                : new Trio<>(server, proxy, proxy.getProcessPlayerManager().getPlayerByUniqueID(uniqueID));
    }

    @Nullable
    private Trio<ProcessInformation, ProcessInformation, Player> findPlayer(@NotNull String name) {
        ProcessInformation server = this.getProcess(name, null, false);
        ProcessInformation proxy = this.getProcess(name, null, true);
        return server == null || proxy == null
                ? null
                : new Trio<>(server, proxy, proxy.getProcessPlayerManager().getPlayerByName(name));
    }

    @Nullable
    private ProcessInformation getProcess(@Nullable String name, @Nullable UUID uuid, boolean proxy) {
        if (name == null && uuid == null) {
            return null;
        }

        return ExecutorAPI.getInstance().getProcessProvider().getProcesses()
                .stream()
                .filter(e -> proxy != e.getProcessDetail().getTemplate().isServer())
                .filter(e -> name == null ? e.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(uuid) : e.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(name))
                .findFirst()
                .orElse(null);
    }
}
