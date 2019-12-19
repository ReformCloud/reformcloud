package systems.reformcloud.reformcloud2.executor.api.common.process;

import com.google.gson.reflect.TypeToken;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.utility.clone.Clone;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public final class ProcessInformation
    implements Nameable, Clone<ProcessInformation> {

  public static final TypeToken<ProcessInformation> TYPE =
      new TypeToken<ProcessInformation>() {};

  public ProcessInformation(
      @Nonnull String processName, @Nonnull String displayName,
      @Nonnull String parent, @Nullable UUID nodeUniqueID,
      @Nonnull UUID processUniqueID, int id, @Nonnull ProcessState processState,
      @Nonnull NetworkInfo networkInfo, @Nonnull ProcessGroup processGroup,
      @Nonnull Template template,
      @Nonnull ProcessRuntimeInformation processRuntimeInformation,
      @Nonnull List<DefaultPlugin> plugins, @Nonnull JsonConfiguration extra,
      int maxPlayers) {
    this.processName = processName;
    this.displayName = displayName;
    this.parent = parent;
    this.nodeUniqueID = nodeUniqueID;
    this.processUniqueID = processUniqueID;
    this.id = id;
    this.processState = processState;
    this.networkInfo = networkInfo;
    this.processGroup = processGroup;
    this.template = template;
    this.processRuntimeInformation = processRuntimeInformation;
    this.plugins = plugins;
    this.extra = extra;
    this.maxPlayers = maxPlayers;
  }

  private String processName;

  private String displayName;

  private String parent;

  private UUID nodeUniqueID;

  private UUID processUniqueID;

  private int id;

  private int maxPlayers;

  private SortedSet<Player> onlinePlayers =
      new TreeSet<>(Comparator.comparingLong(Player::getJoined));

  private ProcessState processState;

  private NetworkInfo networkInfo;

  private ProcessGroup processGroup;

  private Template template;

  private ProcessRuntimeInformation processRuntimeInformation;

  private List<DefaultPlugin> plugins;

  private JsonConfiguration extra;

  @Nonnull
  public String getDisplayName() {
    return displayName;
  }

  @Nonnull
  public String getParent() {
    return parent;
  }

  public UUID getNodeUniqueID() { return nodeUniqueID; }

  @Nonnull
  public UUID getProcessUniqueID() {
    return processUniqueID;
  }

  public int getId() { return id; }

  public int getMaxPlayers() { return maxPlayers; }

  public int getOnlineCount() { return onlinePlayers.size(); }

  @Nonnull
  public SortedSet<Player> getOnlinePlayers() {
    return onlinePlayers;
  }

  @Nonnull
  public ProcessState getProcessState() {
    return processState;
  }

  @Nonnull
  public NetworkInfo getNetworkInfo() {
    return networkInfo;
  }

  @Nonnull
  public ProcessGroup getProcessGroup() {
    return processGroup;
  }

  @Nonnull
  public Template getTemplate() {
    return template;
  }

  @Nonnull
  public ProcessRuntimeInformation getProcessRuntimeInformation() {
    return processRuntimeInformation;
  }

  @Nonnull
  public List<DefaultPlugin> getPlugins() {
    return plugins;
  }

  @Nonnull
  public JsonConfiguration getExtra() {
    return extra;
  }

  @Nonnull
  @Override
  public String getName() {
    return processName;
  }

  public void setProcessState(@Nonnull ProcessState processState) {
    this.processState = processState;
  }

  public void setProcessGroup(@Nonnull ProcessGroup processGroup) {
    this.processGroup = processGroup;
  }

  public boolean onLogin(@Nonnull UUID playerUuid, @Nonnull String playerName) {
    return onlinePlayers.add(new Player(playerUuid, playerName));
  }

  public boolean isLobby() {
    return template.isServer() && processGroup.isCanBeUsedAsLobby();
  }

  public void onLogout(@Nonnull UUID uniqueID) {
    Links
        .filterToReference(onlinePlayers,
                           player -> player.getUniqueID().equals(uniqueID))
        .ifPresent(player -> onlinePlayers.remove(player));
  }

  public boolean isPlayerOnline(@Nonnull UUID uniqueID) {
    return Links
        .filterToReference(onlinePlayers,
                           player -> player.getUniqueID().equals(uniqueID))
        .isPresent();
  }

  public ProcessInformation updateMaxPlayers(@Nullable Integer value) {
    if (processGroup.getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
      this.maxPlayers =
          processGroup.getPlayerAccessConfiguration().getMaxPlayers();
    } else {
      if (value != null) {
        this.maxPlayers = value;
      }
    }
    return this;
  }

  public void updateRuntimeInformation() {
    this.processRuntimeInformation = ProcessRuntimeInformation.create();
  }

  @Override
  @Nonnull
  public ProcessInformation clone() {
    try {
      return (ProcessInformation)super.clone();
    } catch (final CloneNotSupportedException ex) {
      return new ProcessInformation(
          processName, displayName, parent, nodeUniqueID, processUniqueID, id,
          processState, networkInfo, processGroup, template,
          processRuntimeInformation, plugins, extra, maxPlayers);
    }
  }

  @Override
  public boolean equals(@Nonnull Object obj) {
    if (!(obj instanceof ProcessInformation)) {
      return false;
    }

    ProcessInformation compare = (ProcessInformation)obj;
    return Objects.equals(compare.getProcessUniqueID(), getProcessUniqueID());
  }

  @Override
  @Nonnull
  public String toString() {
    return getName() + "/" + getProcessUniqueID();
  }
}
