package systems.reformcloud.reformcloud2.executor.api.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.Player;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SharedInvalidPlayerFixer {

    private SharedInvalidPlayerFixer() {
        throw new UnsupportedOperationException();
    }

    public static void start(@NotNull Function<UUID, Boolean> onlineChecker, @NotNull Supplier<Integer> onlineCountSupplier) {
        CommonHelper.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            ProcessInformation current = API.getInstance().getCurrentProcessInformation();
            Collection<UUID> forRemoval = new ArrayList<>();

            for (Player onlinePlayer : current.getProcessPlayerManager().getOnlinePlayers()) {
                if (onlineChecker.apply(onlinePlayer.getUniqueID())) {
                    continue;
                }

                forRemoval.add(onlinePlayer.getUniqueID());
            }

            if (forRemoval.isEmpty()) {
                return;
            }

            for (UUID uuid : forRemoval) {
                current.getProcessPlayerManager().onLogout(uuid);
            }

            if (onlineCountSupplier.get() < current.getProcessDetail().getMaxPlayers()
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                current.getProcessDetail().setProcessState(ProcessState.READY);
            }

            current.updateRuntimeInformation();
            BungeeExecutor.getInstance().setThisProcessInformation(current);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
        }, 2, 2, TimeUnit.SECONDS);
    }
}
