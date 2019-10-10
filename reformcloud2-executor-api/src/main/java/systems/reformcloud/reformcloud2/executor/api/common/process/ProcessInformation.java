package systems.reformcloud.reformcloud2.executor.api.common.process;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.Template;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.*;

public final class ProcessInformation implements Nameable {

    public static final TypeToken<ProcessInformation> TYPE = new TypeToken<ProcessInformation>() {};

    public ProcessInformation(String processName, String parent, UUID nodeUniqueID, UUID processUniqueID, int id,
                              ProcessState processState, NetworkInfo networkInfo,
                              ProcessGroup processGroup, Template template,
                              ProcessRuntimeInformation processRuntimeInformation,
                              List<DefaultPlugin> plugins, JsonConfiguration extra, int maxPlayers) {
        this.processName = processName;
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

    private String parent;

    private UUID nodeUniqueID;

    private UUID processUniqueID;

    private int id;

    private int maxPlayers;

    private SortedSet<Player> onlinePlayers = new TreeSet<>(Comparator.comparingLong(Player::getJoined));

    private ProcessState processState;

    private NetworkInfo networkInfo;

    private ProcessGroup processGroup;

    private Template template;

    private ProcessRuntimeInformation processRuntimeInformation;

    private List<DefaultPlugin> plugins;

    private JsonConfiguration extra;

    public String getParent() {
        return parent;
    }

    public UUID getNodeUniqueID() {
        return nodeUniqueID;
    }

    public UUID getProcessUniqueID() {
        return processUniqueID;
    }

    public int getId() {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getOnlineCount() {
        return onlinePlayers.size();
    }

    public SortedSet<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public ProcessGroup getProcessGroup() {
        return processGroup;
    }

    public Template getTemplate() {
        return template;
    }

    public ProcessRuntimeInformation getProcessRuntimeInformation() {
        return processRuntimeInformation;
    }

    public List<DefaultPlugin> getPlugins() {
        return plugins;
    }

    public JsonConfiguration getExtra() {
        return extra;
    }

    @Override
    public String getName() {
        return processName;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public void setProcessGroup(ProcessGroup processGroup) {
        this.processGroup = processGroup;
    }

    public boolean onLogin(UUID playerUuid, String playerName) {
        if (isPlayerOnline(playerUuid)) {
            return false;
        }

        return onlinePlayers.add(new Player(playerUuid, playerName));
    }

    public boolean isLobby() {
        return template.isServer() && processGroup.isCanBeUsedAsLobby();
    }

    public void onLogout(UUID uniqueID) {
        Links.filterToReference(onlinePlayers, player -> player.getUniqueID().equals(uniqueID)).ifPresent(player -> onlinePlayers.remove(player));
    }

    public boolean isPlayerOnline(UUID uniqueID) {
        return Links.filterToReference(onlinePlayers, player -> player.getUniqueID().equals(uniqueID)).isPresent();
    }

    public ProcessInformation updateMaxPlayers(Integer value) {
        if (processGroup.getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
            this.maxPlayers = processGroup.getPlayerAccessConfiguration().getMaxPlayers();
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
    public boolean equals(Object obj) {
        if (!(obj instanceof ProcessInformation)) {
            return false;
        }

        ProcessInformation compare = (ProcessInformation) obj;
        return Objects.equals(compare.getProcessUniqueID(), getProcessUniqueID());
    }

    @Override
    public String toString() {
        return getName() + "/" + getProcessUniqueID();
    }
}
