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

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

public class DefaultPlayerAccessConfiguration implements PlayerAccessConfiguration {

    private String fullJoinPermission = DEFAULT_FULL_JOIN_PERMISSION;
    private boolean maintenance;
    private String maintenanceJoinPermission = DEFAULT_MAINTENANCE_JOIN_PERMISSION;
    private boolean joinOnlyWithPermission;
    private String joinPermission = DEFAULT_JOIN_PERMISSION;
    private boolean usePlayerLimit;
    private int maxPlayers = DEFAULT_MAX_PLAYERS;

    protected DefaultPlayerAccessConfiguration() {
    }

    protected DefaultPlayerAccessConfiguration(boolean maintenance, boolean joinOnlyWithPermission, boolean usePlayerLimit) {
        this.maintenance = maintenance;
        this.joinOnlyWithPermission = joinOnlyWithPermission;
        this.usePlayerLimit = usePlayerLimit;
    }

    protected DefaultPlayerAccessConfiguration(String fullJoinPermission, boolean maintenance, String maintenanceJoinPermission,
                                               boolean joinOnlyWithPermission, String joinPermission, boolean usePlayerLimit, int maxPlayers) {
        this.fullJoinPermission = fullJoinPermission;
        this.maintenance = maintenance;
        this.maintenanceJoinPermission = maintenanceJoinPermission;
        this.joinOnlyWithPermission = joinOnlyWithPermission;
        this.joinPermission = joinPermission;
        this.usePlayerLimit = usePlayerLimit;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public @NotNull String getFullJoinPermission() {
        return this.fullJoinPermission;
    }

    @Override
    public void setFullJoinPermission(@NotNull String fullJoinPermission) {
        this.fullJoinPermission = fullJoinPermission;
    }

    @Override public boolean isMaintenance() {
        return this.maintenance;
    }

    @Override
    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    @Override
    public @NotNull String getMaintenanceJoinPermission() {
        return this.maintenanceJoinPermission;
    }

    @Override
    public void setMaintenanceJoinPermission(@NotNull String maintenanceJoinPermission) {
        this.maintenanceJoinPermission = maintenanceJoinPermission;
    }

    @Override
    public boolean isJoinOnlyWithPermission() {
        return this.joinOnlyWithPermission;
    }

    @Override
    public void setJoinOnlyWithPermission(boolean joinOnlyWithPermission) {
        this.joinOnlyWithPermission = joinOnlyWithPermission;
    }

    @Override
    public @NotNull String getJoinPermission() {
        return this.joinPermission;
    }

    @Override
    public void setJoinPermission(@NotNull String joinPermission) {
        this.joinPermission = joinPermission;
    }

    @Override
    public boolean isUsePlayerLimit() {
        return this.usePlayerLimit;
    }

    @Override
    public void setUsePlayerLimit(boolean usePlayerLimit) {
        this.usePlayerLimit = usePlayerLimit;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public @NotNull PlayerAccessConfiguration clone() {
        return new DefaultPlayerAccessConfiguration(
            this.fullJoinPermission,
            this.maintenance,
            this.maintenanceJoinPermission,
            this.joinOnlyWithPermission,
            this.joinPermission,
            this.usePlayerLimit,
            this.maxPlayers
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.fullJoinPermission);
        buffer.writeBoolean(this.maintenance);
        buffer.writeString(this.maintenanceJoinPermission);
        buffer.writeBoolean(this.joinOnlyWithPermission);
        buffer.writeString(this.joinPermission);
        buffer.writeBoolean(this.usePlayerLimit);
        buffer.writeInt(this.maxPlayers);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.fullJoinPermission = buffer.readString();
        this.maintenance = buffer.readBoolean();
        this.maintenanceJoinPermission = buffer.readString();
        this.joinOnlyWithPermission = buffer.readBoolean();
        this.joinPermission = buffer.readString();
        this.usePlayerLimit = buffer.readBoolean();
        this.maxPlayers = buffer.readInt();
    }
}
