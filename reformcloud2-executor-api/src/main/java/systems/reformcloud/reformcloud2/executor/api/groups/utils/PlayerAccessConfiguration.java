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
package systems.reformcloud.reformcloud2.executor.api.groups.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.util.Objects;

public final class PlayerAccessConfiguration implements SerializableObject {

    private String fullJoinPermission;
    private boolean maintenance;
    private String maintenanceJoinPermission;
    private boolean joinOnlyPerPermission;
    private String joinPermission;
    private boolean useCloudPlayerLimit;
    private int maxPlayers;

    @ApiStatus.Internal
    public PlayerAccessConfiguration() {
    }

    public PlayerAccessConfiguration(String fullJoinPermission, boolean maintenance, String maintenanceJoinPermission,
                                     boolean joinOnlyPerPermission, String joinPermission, boolean useCloudPlayerLimit, int maxPlayers) {
        this.fullJoinPermission = fullJoinPermission;
        this.maintenance = maintenance;
        this.maintenanceJoinPermission = maintenanceJoinPermission;
        this.joinOnlyPerPermission = joinOnlyPerPermission;
        this.joinPermission = joinPermission;
        this.useCloudPlayerLimit = useCloudPlayerLimit;
        this.maxPlayers = maxPlayers;
    }

    public static PlayerAccessConfiguration createDefault() {
        return new PlayerAccessConfiguration(
            "reformcloud.join.full",
            true,
            "reformcloud.join.maintenance",
            false,
            "reformcloud.join",
            true,
            512
        );
    }

    @NotNull
    public String getFullJoinPermission() {
        return this.fullJoinPermission == null ? "reformcloud.join.full" : this.fullJoinPermission;
    }

    public boolean isMaintenance() {
        return this.maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    @NotNull
    public String getMaintenanceJoinPermission() {
        return this.maintenanceJoinPermission == null ? "reformcloud.join.maintenance" : this.maintenanceJoinPermission;
    }

    public boolean isJoinOnlyPerPermission() {
        return this.joinOnlyPerPermission;
    }

    @NotNull
    public String getJoinPermission() {
        return this.joinPermission == null ? "reformcloud.custom.permission" : this.joinPermission;
    }

    public boolean isUseCloudPlayerLimit() {
        return this.useCloudPlayerLimit;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.useCloudPlayerLimit = true;
    }

    public void toggleMaintenance() {
        this.maintenance = !this.maintenance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PlayerAccessConfiguration)) {
            return false;
        }

        PlayerAccessConfiguration that = (PlayerAccessConfiguration) o;
        return this.isMaintenance() == that.isMaintenance()
            && this.isJoinOnlyPerPermission() == that.isJoinOnlyPerPermission()
            && this.isUseCloudPlayerLimit() == that.isUseCloudPlayerLimit()
            && this.getMaxPlayers() == that.getMaxPlayers()
            && Objects.equals(this.getMaintenanceJoinPermission(), that.getMaintenanceJoinPermission())
            && Objects.equals(this.getJoinPermission(), that.getJoinPermission());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.fullJoinPermission);
        buffer.writeBoolean(this.maintenance);
        buffer.writeString(this.maintenanceJoinPermission);
        buffer.writeBoolean(this.joinOnlyPerPermission);
        buffer.writeString(this.joinPermission);
        buffer.writeBoolean(this.useCloudPlayerLimit);
        buffer.writeVarInt(this.maxPlayers);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.fullJoinPermission = buffer.readString();
        this.maintenance = buffer.readBoolean();
        this.maintenanceJoinPermission = buffer.readString();
        this.joinOnlyPerPermission = buffer.readBoolean();
        this.joinPermission = buffer.readString();
        this.useCloudPlayerLimit = buffer.readBoolean();
        this.maxPlayers = buffer.readVarInt();
    }
}
