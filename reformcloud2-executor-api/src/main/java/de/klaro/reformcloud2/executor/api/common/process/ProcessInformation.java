package de.klaro.reformcloud2.executor.api.common.process;

import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Template;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ProcessInformation implements Nameable {

    public ProcessInformation(String processName, String parent, UUID processUniqueID, int id,
                              ProcessState processState, NetworkInfo networkInfo,
                              ProcessGroup processGroup, Template template,
                              ProcessRuntimeInformation processRuntimeInformation,
                              List<Plugin> plugins, Configurable extra, int maxPlayers) {
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

    private List<Plugin> plugins;

    private Configurable extra;

    public String getProcessName() {
        return processName;
    }

    public String getParent() {
        return parent;
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

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public Configurable getExtra() {
        return extra;
    }

    @Override
    public String getName() {
        return processName;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public void setProcessRuntimeInformation(ProcessRuntimeInformation processRuntimeInformation) {
        this.processRuntimeInformation = processRuntimeInformation;
    }

    public boolean onLogin(UUID playerUuid, String playerName) {
        if (isPlayerOnline(playerUuid)) {
            return false;
        }

        return onlinePlayers.add(new Player(playerUuid, playerName));
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

    public ProcessInformation updateMaxPlayers(Properties properties) {
        if (processGroup.getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
            this.maxPlayers = processGroup.getPlayerAccessConfiguration().getMaxPlayers();
        } else {
            if (properties != null) {
                this.maxPlayers = Integer.parseInt(properties.getProperty("max-players", "20"));
            }
        }
        return this;
    }
}
