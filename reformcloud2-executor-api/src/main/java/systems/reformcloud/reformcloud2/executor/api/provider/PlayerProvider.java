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
package systems.reformcloud.reformcloud2.executor.api.provider;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.PlayerWrapper;

import java.util.Optional;
import java.util.UUID;

public interface PlayerProvider {

    boolean isPlayerOnline(@NotNull String name);

    boolean isPlayerOnline(@NotNull UUID uniqueId);

    @NotNull
    Optional<PlayerWrapper> getPlayer(@NotNull String name);

    @NotNull
    Optional<PlayerWrapper> getPlayer(@NotNull UUID uniqueId);

    @NotNull
    default Task<Boolean> isPlayerOnlineAsync(@NotNull String name) {
        return Task.supply(() -> this.isPlayerOnline(name));
    }

    @NotNull
    default Task<Boolean> isPlayerOnlineAsync(@NotNull UUID uniqueId) {
        return Task.supply(() -> this.isPlayerOnline(uniqueId));
    }

    @NotNull
    default Task<Optional<PlayerWrapper>> getPlayerAsync(@NotNull String name) {
        return Task.supply(() -> this.getPlayer(name));
    }

    @NotNull
    default Task<Optional<PlayerWrapper>> getPlayerAsync(@NotNull UUID uniqueId) {
        return Task.supply(() -> this.getPlayer(uniqueId));
    }
}
