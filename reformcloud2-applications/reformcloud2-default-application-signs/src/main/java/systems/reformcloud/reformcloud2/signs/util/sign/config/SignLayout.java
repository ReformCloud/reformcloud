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
package systems.reformcloud.reformcloud2.signs.util.sign.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.util.List;

public class SignLayout implements SerializableObject, Cloneable {

    private String target;
    private boolean searchingLayoutWhenFull;
    private boolean showMaintenanceProcessesOnSigns;
    private List<SignSubLayout> searchingLayouts;
    private List<SignSubLayout> waitingForConnectLayout;
    private List<SignSubLayout> emptyLayout;
    private List<SignSubLayout> onlineLayout;
    private List<SignSubLayout> fullLayout;
    private List<SignSubLayout> maintenanceLayout;

    public SignLayout() {
    }

    public SignLayout(
            @Nullable String target,
            boolean searchingLayoutWhenFull,
            boolean showMaintenanceProcessesOnSigns,
            @NotNull List<SignSubLayout> searchingLayouts,
            @NotNull List<SignSubLayout> waitingForConnectLayout,
            @NotNull List<SignSubLayout> emptyLayout,
            @NotNull List<SignSubLayout> onlineLayout,
            @NotNull List<SignSubLayout> fullLayout,
            @NotNull List<SignSubLayout> maintenanceLayout) {
        this.target = target;
        this.searchingLayoutWhenFull = searchingLayoutWhenFull;
        this.showMaintenanceProcessesOnSigns = showMaintenanceProcessesOnSigns;
        this.searchingLayouts = searchingLayouts;
        this.waitingForConnectLayout = waitingForConnectLayout;
        this.emptyLayout = emptyLayout;
        this.onlineLayout = onlineLayout;
        this.fullLayout = fullLayout;
        this.maintenanceLayout = maintenanceLayout;
    }

    public String getTarget() {
        return this.target;
    }

    public boolean isSearchingLayoutWhenFull() {
        return this.searchingLayoutWhenFull;
    }

    public boolean isShowMaintenanceProcessesOnSigns() {
        return this.showMaintenanceProcessesOnSigns;
    }

    public List<SignSubLayout> getSearchingLayouts() {
        return this.searchingLayouts;
    }

    public List<SignSubLayout> getWaitingForConnectLayout() {
        return this.waitingForConnectLayout;
    }

    public List<SignSubLayout> getEmptyLayout() {
        return this.emptyLayout;
    }

    public List<SignSubLayout> getOnlineLayout() {
        return this.onlineLayout;
    }

    public List<SignSubLayout> getFullLayout() {
        return this.fullLayout;
    }

    public List<SignSubLayout> getMaintenanceLayout() {
        return this.maintenanceLayout;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.target);
        buffer.writeBoolean(this.searchingLayoutWhenFull);
        buffer.writeBoolean(this.showMaintenanceProcessesOnSigns);
        buffer.writeObjects(this.searchingLayouts);
        buffer.writeObjects(this.waitingForConnectLayout);
        buffer.writeObjects(this.emptyLayout);
        buffer.writeObjects(this.onlineLayout);
        buffer.writeObjects(this.fullLayout);
        buffer.writeObjects(this.maintenanceLayout);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.target = buffer.readString();
        this.searchingLayoutWhenFull = buffer.readBoolean();
        this.showMaintenanceProcessesOnSigns = buffer.readBoolean();
        this.searchingLayouts = buffer.readObjects(SignSubLayout.class);
        this.waitingForConnectLayout = buffer.readObjects(SignSubLayout.class);
        this.emptyLayout = buffer.readObjects(SignSubLayout.class);
        this.onlineLayout = buffer.readObjects(SignSubLayout.class);
        this.fullLayout = buffer.readObjects(SignSubLayout.class);
        this.maintenanceLayout = buffer.readObjects(SignSubLayout.class);
    }
}
