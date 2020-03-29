package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.Player;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Trio;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class CommandPlayers extends GlobalCommand {

    public CommandPlayers() {
        super("players", "reformcloud.command.players", "Manage the players on the proxies", "pl");
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessages((
                "players [list]               | Lists all connected players\n" +
                        "players <name | uuid> [info] | Shows information about a specific online player"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        if (strings.length == 0) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        if (strings[0].equalsIgnoreCase("list")) {
            commandSource.sendMessage("Online-Players: ");
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().stream()
                    .filter(e -> !e.getProcessDetail().getTemplate().isServer())
                    .map(e -> {
                        for (Player onlinePlayer : e.getProcessPlayerManager().getOnlinePlayers()) {
                            return findPlayer(onlinePlayer.getUniqueID());
                        }

                        return null;
                    }).filter(Objects::nonNull).forEach(e -> commandSource.sendMessage(
                    " > " + e.getThird().getName() + " on " + e.getFirst().getName() + "/" + e.getSecond().getName()
            ));
            return true;
        }

        if (strings.length == 2 && strings[1].equalsIgnoreCase("info")) {
            Trio<ProcessInformation, ProcessInformation, Player> trio;
            UUID uniqueID;
            if ((uniqueID = CommonHelper.tryParse(strings[0])) != null) {
                trio = findPlayer(uniqueID);
            } else {
                trio = findPlayer(strings[0]);
            }

            if (trio == null) {
                commandSource.sendMessage(LanguageManager.get("command-players-player-not-found", strings[0]));
                return true;
            }

            Player subServerPlayer = trio.getFirst().getProcessPlayerManager().getOnlinePlayers().stream().filter(e -> uniqueID == null
                    ? e.getName().equals(strings[0]) : e.getUniqueID().equals(uniqueID)).findAny().orElse(null);
            if (subServerPlayer == null) {
                commandSource.sendMessage(LanguageManager.get("command-players-player-not-found", strings[0]));
                return true;
            }

            AtomicReference<StringBuilder> stringBuilder = new AtomicReference<>(new StringBuilder());
            stringBuilder.get().append(" > Name               - ").append(trio.getThird().getName()).append("\n");
            stringBuilder.get().append(" > UUID               - ").append(trio.getThird().getUniqueID()).append("\n");
            stringBuilder.get().append(" > Proxy              - ").append(trio.getSecond().getName()).append("\n");
            stringBuilder.get().append(" > Connected (Proxy)  - ").append(CommonHelper.DATE_FORMAT.format(trio.getThird().getJoined())).append("\n");
            stringBuilder.get().append(" > Server             - ").append(trio.getFirst().getName()).append("\n");
            stringBuilder.get().append(" > Connected (Server) - ").append(CommonHelper.DATE_FORMAT.format(subServerPlayer.getJoined())).append("\n");
            commandSource.sendMessages(stringBuilder.get().toString().split("\n"));
            return true;
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private Trio<ProcessInformation, ProcessInformation, Player> findPlayer(UUID uniqueID) {
        ProcessInformation information = getProcess(null, uniqueID, false);
        ProcessInformation proxy = getProcess(null, uniqueID, true);
        return information == null || proxy == null ? null : new Trio<>(information, proxy, proxy.getProcessPlayerManager().getOnlinePlayers()
                .stream().filter(e -> e.getUniqueID().equals(uniqueID)).findFirst().orElse(null));
    }

    private Trio<ProcessInformation, ProcessInformation, Player> findPlayer(String name) {
        ProcessInformation information = getProcess(name, null, false);
        ProcessInformation proxy = getProcess(name, null, true);
        return information == null || proxy == null ? null : new Trio<>(information, proxy, proxy.getProcessPlayerManager().getOnlinePlayers()
                .stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null));
    }

    private ProcessInformation getProcess(@Nullable String name, @Nullable UUID uuid, boolean proxy) {
        if (name == null && uuid == null) {
            return null;
        }

        return ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses()
                .stream()
                .filter(e -> proxy != e.getProcessDetail().getTemplate().isServer())
                .filter(e -> name == null ? e.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(uuid) : e.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(name))
                .findFirst()
                .orElse(null);
    }
}
