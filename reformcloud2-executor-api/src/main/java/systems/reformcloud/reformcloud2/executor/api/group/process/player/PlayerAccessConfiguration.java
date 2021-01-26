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
package systems.reformcloud.reformcloud2.executor.api.group.process.player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;

public interface PlayerAccessConfiguration extends SerializableObject, Cloneable {

    String DEFAULT_FULL_JOIN_PERMISSION = "reformcloud.join.full";
    String DEFAULT_MAINTENANCE_JOIN_PERMISSION = "reformcloud.join.maintenance";
    String DEFAULT_JOIN_PERMISSION = "reformcloud.join";
    int DEFAULT_MAX_PLAYERS = 20;

    @NotNull
    @Contract(pure = true)
    static PlayerAccessConfiguration enabled() {
        return new DefaultPlayerAccessConfiguration(true, true, true);
    }

    @NotNull
    @Contract(pure = true)
    static PlayerAccessConfiguration disabled() {
        return new DefaultPlayerAccessConfiguration(false, false, false);
    }

    @NotNull
    String getFullJoinPermission();

    void setFullJoinPermission(@NotNull String fullJoinPermission);

    boolean isMaintenance();

    void setMaintenance(boolean maintenance);

    @NotNull
    String getMaintenanceJoinPermission();

    void setMaintenanceJoinPermission(@NotNull String maintenanceJoinPermission);

    boolean isJoinOnlyWithPermission();

    void setJoinOnlyWithPermission(boolean joinOnlyWithPermission);

    @NotNull
    String getJoinPermission();

    void setJoinPermission(@NotNull String joinPermission);

    boolean isUsePlayerLimit();

    void setUsePlayerLimit(boolean usePlayerLimit);

    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    @NotNull
    PlayerAccessConfiguration clone();
}
