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
package systems.reformcloud.group.process.player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.data.SerializableObject;

/**
 * A player access configuration is used to allow some default restrictions when joining a server or proxy.
 */
public interface PlayerAccessConfiguration extends SerializableObject, Cloneable {
  /**
   * The default full join permission used if not changed after the creation of
   * a {@code PlayerAccessConfiguration}.
   */
  String DEFAULT_FULL_JOIN_PERMISSION = "reformcloud.join.full";
  /**
   * The default maintenance join permission used if not changed after the creation of
   * a {@code PlayerAccessConfiguration}.
   */
  String DEFAULT_MAINTENANCE_JOIN_PERMISSION = "reformcloud.join.maintenance";
  /**
   * The default join permission used if not changed after the creation of
   * a {@code PlayerAccessConfiguration}.
   */
  String DEFAULT_JOIN_PERMISSION = "reformcloud.join";
  /**
   * The default max player amount used if not changed after the creation of
   * a {@code PlayerAccessConfiguration}.
   */
  int DEFAULT_MAX_PLAYERS = 20;

  /**
   * Creates a new default configuration which has all checks enabled.
   *
   * @return A new default configuration which has all checks enabled.
   */
  @NotNull
  @Contract(pure = true)
  static PlayerAccessConfiguration enabled() {
    return new DefaultPlayerAccessConfiguration(true, true, true);
  }

  /**
   * Creates a new default configuration which has all checks disabled.
   *
   * @return A new default configuration which has all checks disabled.
   */
  @NotNull
  @Contract(pure = true)
  static PlayerAccessConfiguration disabled() {
    return new DefaultPlayerAccessConfiguration(false, false, false);
  }

  /**
   * The permission to join a server/proxy even if it is full.
   *
   * @return The permission to join a server/proxy even if it is full.
   */
  @NotNull
  String getFullJoinPermission();

  /**
   * Sets the permission to join a server/proxy even it is full.
   *
   * @param fullJoinPermission The new permission needed to join.
   */
  void setFullJoinPermission(@NotNull String fullJoinPermission);

  /**
   * Get if the current server/proxy is in maintenance.
   *
   * @return If the current server/proxy is in maintenance.
   */
  boolean isMaintenance();

  /**
   * Sets the current maintenance status.
   *
   * @param maintenance The new maintenance status.
   */
  void setMaintenance(boolean maintenance);

  /**
   * Get the permission which is needed to join the proxy/server even if maintenance is enabled.
   *
   * @return The permission which is needed to join the proxy/server even if maintenance is enabled.
   */
  @NotNull
  String getMaintenanceJoinPermission();

  /**
   * Sets the permission which is needed to join the proxy/server event if maintenance is enabled.
   *
   * @param maintenanceJoinPermission The new permission needed to join during maintenance.
   */
  void setMaintenanceJoinPermission(@NotNull String maintenanceJoinPermission);

  /**
   * Get if the join is restricted and only possible for users who have the defined join permission.
   *
   * @return If the join is restricted and only possible for users who have the defined join permission.
   */
  boolean isJoinOnlyWithPermission();

  /**
   * Sets if the join is restricted and only possible for users who have the defined join permission.
   *
   * @param joinOnlyWithPermission If the join is restricted to users with the required permission.
   */
  void setJoinOnlyWithPermission(boolean joinOnlyWithPermission);

  /**
   * The join permission which is needed to join the server if the only-with-permission mode is enabled.
   *
   * @return The join permission which is needed to join the server if the only-with-permission mode is enabled.
   */
  @NotNull
  String getJoinPermission();

  /**
   * Sets the join permission which is needed to join the server if the only-with-permission mode is enabled.
   *
   * @param joinPermission The join permission which is needed to join the server.
   */
  void setJoinPermission(@NotNull String joinPermission);

  /**
   * Get if the player limit defined in the server configuration or defined in this class should be used.
   *
   * @return If the player limit defined in the server configuration or defined in this class should be used.
   */
  boolean isUsePlayerLimit();

  /**
   * Sets if the player limit defined in the server configuration or defined in this class should be used.
   *
   * @param usePlayerLimit If the player limit defined in the server configuration or defined in this class should be used.
   */
  void setUsePlayerLimit(boolean usePlayerLimit);

  /**
   * Get the configured max-players for the server used if {@link #isUsePlayerLimit()} is {@code true}.
   *
   * @return the configured max-players for the server.
   */
  int getMaxPlayers();

  /**
   * Sets the max players for the server used if {@link #isUsePlayerLimit()} is {@code true}.
   *
   * @param maxPlayers The new max player amount for the server.
   */
  void setMaxPlayers(int maxPlayers);

  /**
   * Creates a clone for of this object.
   *
   * @return A clone of this object.
   */
  @NotNull
  PlayerAccessConfiguration clone();
}
