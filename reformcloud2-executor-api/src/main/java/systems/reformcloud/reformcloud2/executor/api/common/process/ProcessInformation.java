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
package systems.reformcloud.reformcloud2.executor.api.common.process;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessDetail;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessPlayerManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessUtil;
import systems.reformcloud.reformcloud2.executor.api.common.process.event.ProcessInformationConfigureEvent;
import systems.reformcloud.reformcloud2.executor.api.common.utility.clone.Clone;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ProcessInformation implements Clone<ProcessInformation>, SerializableObject {

    public static final TypeToken<ProcessInformation> TYPE = new TypeToken<ProcessInformation>() {
    };
    private ProcessPlayerManager processPlayerManager = new ProcessPlayerManager();
    private ProcessDetail processDetail;
    private NetworkInfo networkInfo;
    private JsonConfiguration extra;
    private List<DefaultPlugin> plugins = new CopyOnWriteArrayList<>();
    private Collection<ProcessInclusion> preInclusions;
    private ProcessGroup processGroup;

    @ApiStatus.Internal
    public ProcessInformation() {
    }

    @ApiStatus.Internal
    public ProcessInformation(
            @NotNull ProcessDetail processDetail, @NotNull NetworkInfo networkInfo, @NotNull ProcessGroup processGroup,
            @NotNull JsonConfiguration extra, @NotNull Collection<ProcessInclusion> preInclusions
    ) {
        this.processDetail = processDetail;
        this.networkInfo = networkInfo;
        this.processGroup = processGroup;
        this.extra = extra;
        this.preInclusions = preInclusions;

        ExecutorAPI.getInstance().getEventManager().callEvent(new ProcessInformationConfigureEvent(this));
    }

    public ProcessPlayerManager getProcessPlayerManager() {
        return processPlayerManager;
    }

    public ProcessDetail getProcessDetail() {
        return processDetail;
    }

    /**
     * @return If the current process is a lobby process
     */
    public boolean isLobby() {
        return this.processDetail.getTemplate().isServer() && processGroup.isCanBeUsedAsLobby();
    }

    /**
     * @return The network information of the current process
     */
    @NotNull
    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    /**
     * @return The process group on which the current process is based
     */
    @NotNull
    public ProcessGroup getProcessGroup() {
        return processGroup;
    }

    /**
     * Sets the process group on which this process is based
     *
     * @param processGroup The updated process group
     */
    @ApiStatus.Internal
    public void setProcessGroup(@NotNull ProcessGroup processGroup) {
        this.processGroup = processGroup;
    }

    /**
     * @return All plugins which are registered on the current process
     */
    @NotNull
    public List<DefaultPlugin> getPlugins() {
        return plugins;
    }

    /**
     * @return The extra configuration which was given by the user
     */
    @NotNull
    public JsonConfiguration getExtra() {
        return extra;
    }

    /**
     * @return All inclusions which are loaded before the start of the process
     */
    @NotNull
    public Collection<ProcessInclusion> getPreInclusions() {
        return preInclusions;
    }

    /**
     * Updates the max player count of the process. This will not force the process to use the given
     * player count. If {@link PlayerAccessConfiguration#isUseCloudPlayerLimit()} then the count will
     * be set to the {@link PlayerAccessConfiguration#getMaxPlayers()} value. If you want to force set
     * the max players of the process use {@link ProcessDetail#setMaxPlayers(int)} instead.
     *
     * @param value The new maximum amount of players or {@code null} if the cloud should only check
     *              if it should use the internal count and set it to the given value.
     * @return The same instance of the process information
     */
    @NotNull
    public ProcessInformation updateMaxPlayers(@Nullable Integer value) {
        if (processGroup.getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
            this.processDetail.setMaxPlayers(processGroup.getPlayerAccessConfiguration().getMaxPlayers());
        } else {
            if (value != null) {
                this.processDetail.setMaxPlayers(value);
            }
        }

        return this;
    }

    /**
     * Updates the runtime information of the current process
     */
    public void updateRuntimeInformation() {
        this.processDetail.setProcessRuntimeInformation(ProcessRuntimeInformation.create());
    }

    /**
     * @return A new process util which wraps the current process information and brings some util methods
     */
    @NotNull
    public ProcessUtil toWrapped() {
        return new ProcessUtil(this);
    }

    @Override
    @Nullable
    public ProcessInformation clone() {
        try {
            return (ProcessInformation) super.clone();
        } catch (final CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        if (!(obj instanceof ProcessInformation)) {
            return false;
        }

        ProcessInformation compare = (ProcessInformation) obj;
        return Objects.equals(compare.getProcessDetail().getProcessUniqueID(), getProcessDetail().getProcessUniqueID());
    }

    @Override
    public int hashCode() {
        return this.getProcessDetail().getProcessUniqueID().hashCode();
    }

    @Override
    @NotNull
    public String toString() {
        return this.getProcessDetail().getName() + "/" + getProcessDetail().getProcessUniqueID();
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.processPlayerManager.getOnlinePlayers());
        buffer.writeObject(this.processDetail);
        buffer.writeObject(this.networkInfo);
        buffer.writeObjects(this.plugins);
        buffer.writeObjects(this.preInclusions);
        buffer.writeObject(this.processGroup);
        buffer.writeArray(this.extra.toPrettyBytes());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processPlayerManager = new ProcessPlayerManager(buffer.readObjects(Player.class));
        this.processDetail = buffer.readObject(ProcessDetail.class);
        this.networkInfo = buffer.readObject(NetworkInfo.class);
        this.plugins = buffer.readObjects(DefaultPlugin.class);
        this.preInclusions = buffer.readObjects(ProcessInclusion.class);
        this.processGroup = buffer.readObject(ProcessGroup.class);

        try (InputStream stream = new ByteArrayInputStream(buffer.readArray())) {
            this.extra = new JsonConfiguration(stream);
        } catch (final IOException ex) {
            ex.printStackTrace();
            this.extra = new JsonConfiguration();
        }
    }
}
