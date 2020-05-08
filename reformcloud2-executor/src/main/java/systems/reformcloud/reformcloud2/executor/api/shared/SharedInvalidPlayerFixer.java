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
