/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.data.SerializableObject;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * A holder of {@link Player}s.
 */
public interface PlayerHolder extends SerializableObject, Cloneable {

  /**
   * Get the online count on this service.
   *
   * @return The online count on this service.
   */
  int getOnlineCount();

  /**
   * Gets the players which are currently connected known to this holder.
   *
   * @return The players which are currently connected known to this holder.
   */
  @NotNull
  Collection<Player> getPlayers();

  /**
   * Gets a specific player by the given {@code name}.
   *
   * @param name The name of player.
   * @return The player with the given {@code name}.
   */
  @NotNull
  Optional<Player> getPlayerByName(@NotNull String name);

  /**
   * Gets a specific player by the given {@code uniqueId}.
   *
   * @param uniqueId The unique id of the player.
   * @return The player with the given {@code uniqueId}.
   */
  @NotNull
  Optional<Player> getPlayerByUniqueId(@NotNull UUID uniqueId);

  /**
   * Creates a clone of this player holder.
   *
   * @return A clone of this player holder.
   */
  @NotNull
  PlayerHolder clone();
}
