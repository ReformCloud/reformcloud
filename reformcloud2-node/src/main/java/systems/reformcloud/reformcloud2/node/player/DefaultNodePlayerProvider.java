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
package systems.reformcloud.reformcloud2.node.player;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.process.Player;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.PlayerProvider;
import systems.reformcloud.reformcloud2.executor.api.provider.ProcessProvider;
import systems.reformcloud.reformcloud2.executor.api.wrappers.PlayerWrapper;

import java.util.Optional;
import java.util.UUID;

public class DefaultNodePlayerProvider implements PlayerProvider {

    @Override
    public boolean isPlayerOnline(@NotNull String name) {
        for (ProcessInformation process : this.getProcessProvider().getProcesses()) {
            if (process.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isPlayerOnline(@NotNull UUID uniqueId) {
        for (ProcessInformation process : this.getProcessProvider().getProcesses()) {
            if (process.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(uniqueId)) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    @Override
    public Optional<PlayerWrapper> getPlayer(@NotNull String name) {
        for (ProcessInformation process : this.getProcessProvider().getProcesses()) {
            Player playerByName = process.getProcessPlayerManager().getPlayerByName(name);
            if (playerByName != null) {
                return Optional.of(new DefaultNodePlayerWrapper(playerByName.getUniqueID()));
            }
        }

        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<PlayerWrapper> getPlayer(@NotNull UUID uniqueId) {
        for (ProcessInformation process : this.getProcessProvider().getProcesses()) {
            if (process.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(uniqueId)) {
                return Optional.of(new DefaultNodePlayerWrapper(uniqueId));
            }
        }

        return Optional.empty();
    }

    private @NotNull ProcessProvider getProcessProvider() {
        return ExecutorAPI.getInstance().getProcessProvider();
    }
}
