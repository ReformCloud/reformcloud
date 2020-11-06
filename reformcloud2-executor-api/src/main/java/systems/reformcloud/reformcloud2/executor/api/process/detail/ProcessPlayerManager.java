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
package systems.reformcloud.reformcloud2.executor.api.process.detail;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.process.Player;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a manager for the player which is located in every process running in the cloud
 */
public final class ProcessPlayerManager {

    private final Set<Player> onlinePlayers = ConcurrentHashMap.newKeySet();

    public ProcessPlayerManager() {
    }

    public ProcessPlayerManager(@NotNull Collection<Player> collection) {
        this.onlinePlayers.addAll(collection);
    }

    /**
     * @return The current online count of the process
     */
    public int getOnlineCount() {
        return this.onlinePlayers.size();
    }

    /**
     * @return An unmodifiable set with all currently connected players
     */
    @NotNull
    @Contract(pure = true)
    public @UnmodifiableView Set<Player> getOnlinePlayers() {
        return Collections.unmodifiableSet(this.onlinePlayers);
    }

    /**
     * Get a player by it's unique identifier
     *
     * @param playerUniqueID The unique identifier of the player
     * @return The player which is connected to the process or {@code null} if no player with the unique id is connected
     */
    @Nullable
    public Player getPlayerByUniqueID(@NotNull UUID playerUniqueID) {
        return Streams.filter(this.onlinePlayers, e -> e.getUniqueID().equals(playerUniqueID));
    }

    /**
     * Get a player by it's name
     *
     * @param playerName The name of the player
     * @return The player which is connected to the process or {@code null} if no player with the name is connected
     */
    @Nullable
    public Player getPlayerByName(@NotNull String playerName) {
        return Streams.filter(this.onlinePlayers, e -> e.getName().equals(playerName));
    }

    /**
     * Checks if the given unique id is online on the process
     *
     * @param playerUniqueID The unique id of the player
     * @return If the player is currently connected to the process
     */
    public boolean isPlayerOnlineOnCurrentProcess(@NotNull UUID playerUniqueID) {
        return Streams.findFirst(this.onlinePlayers, e -> e.getUniqueID().equals(playerUniqueID)).isPresent();
    }

    /**
     * Checks if the given name is online on the process
     *
     * @param playerName The name of the player
     * @return If the player is currently connected to the process
     */
    public boolean isPlayerOnlineOnCurrentProcess(@NotNull String playerName) {
        return Streams.findFirst(this.onlinePlayers, e -> e.getName().equals(playerName)).isPresent();
    }

    /**
     * Handles the login of a player
     *
     * @param playerUniqueID The unique id of the player which is connected
     * @param playerName     The name of the player which is connected
     * @return {@code true} if the player was not already connected
     */
    public boolean onLogin(@NotNull UUID playerUniqueID, @NotNull String playerName) {
        return this.onlinePlayers.add(new Player(playerUniqueID, playerName));
    }

    /**
     * Handles the logout of a player
     *
     * @param uniqueID The unique id of the player who left the process
     */
    public void onLogout(@NotNull UUID uniqueID) {
        this.onlinePlayers.removeIf(e -> e.getUniqueID().equals(uniqueID));
    }

}
