package systems.reformcloud.reformcloud2.executor.api.common.process;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessDetail;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessPlayerManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessUtil;
import systems.reformcloud.reformcloud2.executor.api.common.process.event.ProcessInformationConfigureEvent;
import systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations.ReplacedWith;
import systems.reformcloud.reformcloud2.executor.api.common.utility.clone.Clone;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ProcessInformation implements Nameable, Clone<ProcessInformation> {

    public static final TypeToken<ProcessInformation> TYPE = new TypeToken<ProcessInformation>() {
    };

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

    private final ProcessPlayerManager processPlayerManager = new ProcessPlayerManager();

    private final ProcessDetail processDetail;

    private final NetworkInfo networkInfo;

    private final JsonConfiguration extra;

    private final List<DefaultPlugin> plugins = new CopyOnWriteArrayList<>();

    private final Collection<ProcessInclusion> preInclusions;

    private ProcessGroup processGroup;

    public ProcessPlayerManager getProcessPlayerManager() {
        return processPlayerManager;
    }

    public ProcessDetail getProcessDetail() {
        return processDetail;
    }

    public boolean isLobby() {
        return this.processDetail.getTemplate().isServer() && processGroup.isCanBeUsedAsLobby();
    }

    @NotNull
    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    @NotNull
    public ProcessGroup getProcessGroup() {
        return processGroup;
    }

    @NotNull
    public List<DefaultPlugin> getPlugins() {
        return plugins;
    }

    @NotNull
    public JsonConfiguration getExtra() {
        return extra;
    }

    @NotNull
    public Collection<ProcessInclusion> getPreInclusions() {
        return preInclusions;
    }

    public void setProcessGroup(@NotNull ProcessGroup processGroup) {
        this.processGroup = processGroup;
    }

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

    public void updateRuntimeInformation() {
        this.processDetail.setProcessRuntimeInformation(ProcessRuntimeInformation.create());
    }

    @NotNull
    public ProcessUtil toWrapped() {
        return new ProcessUtil(this);
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getProcessState")
    public ProcessState getProcessState() {
        return this.processDetail.getProcessState();
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getProcessRuntimeInformation")
    public ProcessRuntimeInformation getProcessRuntimeInformation() {
        return this.processDetail.getProcessRuntimeInformation();
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#setProcessState")
    public void setProcessState(@NotNull ProcessState processState) {
        this.processDetail.setProcessState(processState);
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getTemplate")
    public Template getTemplate() {
        return this.processDetail.getTemplate();
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getMaxPlayers")
    public int getMaxPlayers() {
        return this.processDetail.getMaxPlayers();
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getMaxMemory")
    public Integer getMaxMemory() {
        return this.processDetail.getMaxMemory();
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getDisplayName")
    public String getDisplayName() {
        return this.processDetail.getDisplayName();
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getParentName")
    public String getParent() {
        return this.processDetail.getParentName();
    }

    @Contract(pure = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getParentUniqueID")
    @NotNull
    public UUID getNodeUniqueID() {
        return this.processDetail.getParentUniqueID();
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getProcessDetail().getProcessUniqueID")
    public UUID getProcessUniqueID() {
        return this.processDetail.getProcessUniqueID();
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessDetail#getId")
    public int getId() {
        return this.processDetail.getId();
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessPlayerManager#getOnlineCount")
    public int getOnlineCount() {
        return this.processPlayerManager.getOnlineCount();
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessPlayerManager#getOnlinePlayers")
    public SortedSet<Player> getOnlinePlayers() {
        return new TreeSet<>(this.processPlayerManager.getOnlinePlayers());
    }

    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @Override
    @ReplacedWith("#getProcessDetail#getName")
    public String getName() {
        return this.processDetail.getName();
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessPlayerManager#onLogin")
    public boolean onLogin(@NotNull UUID playerUuid, @NotNull String playerName) {
        return this.processPlayerManager.onLogin(playerUuid, playerName);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessPlayerManager#onLogout")
    public void onLogout(@NotNull UUID uniqueID) {
        this.processPlayerManager.onLogout(uniqueID);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessPlayerManager#isPlayerOnlineOnCurrentProcess")
    public boolean isPlayerOnline(@NotNull UUID uniqueID) {
        return this.processPlayerManager.isPlayerOnlineOnCurrentProcess(uniqueID);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.3")
    @Deprecated
    @ReplacedWith("#getProcessPlayerManager#isPlayerOnlineOnCurrentProcess")
    public boolean isPlayerOnline(@NotNull String name) {
        return this.processPlayerManager.isPlayerOnlineOnCurrentProcess(name);
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
    @NotNull
    public String toString() {
        return getName() + "/" + getProcessDetail().getProcessUniqueID();
    }
}
