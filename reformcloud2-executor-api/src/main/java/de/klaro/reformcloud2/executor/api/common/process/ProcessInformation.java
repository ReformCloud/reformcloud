package de.klaro.reformcloud2.executor.api.common.process;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Template;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ProcessInformation implements Nameable {

    public static final TypeToken<ProcessInformation> TYPE = new TypeToken<ProcessInformation>() {};

    public ProcessInformation(String processName, String parent, UUID processUniqueID, int id,
                              ProcessState processState, NetworkInfo networkInfo,
                              ProcessGroup processGroup, Template template,
                              ProcessRuntimeInformation processRuntimeInformation,
                              List<DefaultPlugin> plugins, JsonConfiguration extra, int maxPlayers) {
        this.processName = processName;
        this.parent = parent;
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

    private UUID processUniqueID;

    private int id;

    private int maxPlayers;

    private String motd = "A ReformCloud2 server";

    private SortedSet<Player> onlinePlayers = new TreeSet<>(new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            return Long.compare(o1.getJoined(), o2.getJoined());
        }
    });

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

    public UUID getProcessUniqueID() {
        return processUniqueID;
    }

    public int getId() {
        return id;
    }

    public String getMotd() {
        return motd;
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

    public void setMotd(String motd) {
        this.motd = motd;
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
        Links.filterToOptional(onlinePlayers, new Predicate<Player>() {
            @Override
            public boolean test(Player player) {
                return player.getUniqueID().equals(uniqueID);
            }
        }).ifPresent(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                onlinePlayers.remove(player);
            }
        });
    }

    public boolean isPlayerOnline(UUID uniqueID) {
        return Links.filterToOptional(onlinePlayers, new Predicate<Player>() {
            @Override
            public boolean test(Player player) {
                return player.getUniqueID().equals(uniqueID);
            }
        }).isPresent();
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
}
