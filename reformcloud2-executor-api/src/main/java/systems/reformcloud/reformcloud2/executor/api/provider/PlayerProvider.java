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
package systems.reformcloud.reformcloud2.executor.api.provider;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.PlayerWrapper;

import java.util.Optional;
import java.util.UUID;

/**
 * Provides utility methods for managing the connected players on the network
 */
public interface PlayerProvider {

    /**
     * Checks if the player by the name is online
     *
     * @param name The name of the player to check
     * @return {@code true} if the player is online, {@code false} otherwise
     */
    boolean isPlayerOnline(@NotNull String name);

    /**
     * Checks if the player by the unique id is online
     *
     * @param uniqueId The unique id of the player to check
     * @return {@code true} if the player is online, {@code false} otherwise
     */
    boolean isPlayerOnline(@NotNull UUID uniqueId);

    /**
     * Get a player wrapper by the player's name
     *
     * @param name The name of the player to create a wrapper for
     * @return An optional player wrapper which is present if the unique for the name could be resolved in the online players list
     */
    @NotNull
    Optional<PlayerWrapper> getPlayer(@NotNull String name);

    /**
     * Get a player wrapper by the player's unique id. This optional should by default never be absent.
     *
     * @param uniqueId The unique if of the player to create a wrapper
     * @return An optional player wrapper which is present if the player optional could be created using the unique id
     */
    @NotNull
    Optional<PlayerWrapper> getPlayer(@NotNull UUID uniqueId);

    /**
     * This method does the same as {@link #isPlayerOnline(String)} but asynchronously.
     *
     * @param name The name of the player to check
     * @return {@code true} if the player is online, {@code false} otherwise
     */
    @NotNull
    default Task<Boolean> isPlayerOnlineAsync(@NotNull String name) {
        return Task.supply(() -> this.isPlayerOnline(name));
    }

    /**
     * This method does the same as {@link #isPlayerOnline(UUID)} but asynchronously.
     *
     * @param uniqueId The unique id of the player to check
     * @return {@code true} if the player is online, {@code false} otherwise
     */
    @NotNull
    default Task<Boolean> isPlayerOnlineAsync(@NotNull UUID uniqueId) {
        return Task.supply(() -> this.isPlayerOnline(uniqueId));
    }

    /**
     * This method does the same as {@link #getPlayer(String)} but asynchronously.
     *
     * @param name The name of the player to create a wrapper for
     * @return An optional player wrapper which is present if the unique for the name could be resolved in the online players list
     */
    @NotNull
    default Task<Optional<PlayerWrapper>> getPlayerAsync(@NotNull String name) {
        return Task.supply(() -> this.getPlayer(name));
    }

    /**
     * This method does the same as {@link #getPlayer(UUID)} but asynchronously.
     *
     * @param uniqueId The unique if of the player to create a wrapper
     * @return An optional player wrapper which is present if the player optional could be created using the unique id
     */
    @NotNull
    default Task<Optional<PlayerWrapper>> getPlayerAsync(@NotNull UUID uniqueId) {
        return Task.supply(() -> this.getPlayer(uniqueId));
    }
}
