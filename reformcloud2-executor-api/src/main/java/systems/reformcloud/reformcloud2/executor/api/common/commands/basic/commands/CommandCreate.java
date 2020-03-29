package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public final class CommandCreate extends GlobalCommand {

    public CommandCreate() {
        super("create", "reformcloud.command.create", "The create command for reformcloud things", "");
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessages((
                "create new pg <name> <version>     | Creates a new process group\n" +
                        " --start-port=[port]               | Sets the start port of the new process group\n" +
                        " --max-memory=[memory]             | Sets the max-memory of the process group (default: 512)\n" +
                        " --min-process-count=[min]         | Sets the min process count for the group (default: 1)\n" +
                        " --max-process-count=[max]         | Sets the max process count for the group (default: -1)\n" +
                        " --always-prepared=[prepared]      | Sets the amount of processes which should always be preared (default: 1)\n" +
                        " --max-players=[max]               | Sets the max player count for the processes (default: proxies: 512, servers: 20)\n" +
                        " --start-priority=[priority]       | Sets the startup priority for the group to start (default: 0)\n" +
                        " --static=[static]                 | Marks the process as a static process (default: false)\n" +
                        " --lobby=[lobby]                   | Marks the process as a lobby (default: false)\n" +
                        " --maintenance=[maintenance]       | Enables the maintenance mode for the group (default: enabled on proxies)\n" +
                        " --main-groups=[Group1;Group2]     | Sets the default main groups the group should be in\n" +
                        " --startup-pickers=[Client1;Node2] | Sets the clients on which the processes should start only"
        ).split("\n"));
        source.sendMessage(" ");
        source.sendMessages((
                "create new mg <name>               | Creates a new main group\n" +
                        " --sub-groups=[Group1;Group2]      | Sets the default sub groups which should get added to the group"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        if (strings.length <= 2 || !strings[0].equalsIgnoreCase("new")) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        if (strings[1].equalsIgnoreCase("pg")) {
            this.handleProcessGroupRequest(commandSource, strings);
            return true;
        }

        if (strings[1].equalsIgnoreCase("mg")) {
            this.handleMainGroupRequest(commandSource, strings);
            return true;
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private void handleMainGroupRequest(CommandSource source, String[] strings) {
        if (strings.length != 3 && strings.length != 4) {
            this.describeCommandToSender(source);
            return;
        }

        String name = strings[2];
        List<String> subGroups = new ArrayList<>();
        if (ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(name) != null) {
            source.sendMessage(LanguageManager.get("command-create-main-group-already-exists", name));
            return;
        }

        Properties properties = StringUtil.calcProperties(strings, 3);
        if (properties.containsKey("sub-groups")) {
            String[] subGroupsStrings = properties.getProperty("sub-groups").contains(";")
                    ? properties.getProperty("sub-groups").split(";")
                    : new String[]{properties.getProperty("sub-groups")};

            for (String subGroup : subGroupsStrings) {
                if (ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(subGroup) == null) {
                    source.sendMessage(LanguageManager.get("command-create-sub-group-does-not-exists", subGroup));
                    return;
                }

                if (subGroups.contains(subGroup)) {
                    continue;
                }

                subGroups.add(subGroup);
            }
        }

        ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().createMainGroup(name, subGroups);
        source.sendMessage(LanguageManager.get("command-create-mg", name));
    }

    private void handleProcessGroupRequest(CommandSource source, String[] strings) {
        if (strings.length <= 3) {
            this.describeCommandToSender(source);
            return;
        }

        String name = strings[2];
        Version version = CommonHelper.findEnumField(Version.class, strings[3].toUpperCase()).orNothing();
        if (version == null) {
            source.sendMessage(LanguageManager.get("command-create-version-not-found", strings[3]));
            return;
        }

        if (ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(name) != null) {
            source.sendMessage(LanguageManager.get("command-create-sub-group-already-exists", name));
            return;
        }

        Properties properties = StringUtil.calcProperties(strings, 4);

        int port = version.getDefaultPort();
        int memory = 512;
        int min = 1;
        int max = -1;
        int prepared = 1;
        int maxPlayers = version.isServer() ? 20 : 512;
        int priority = 0;
        boolean staticProcess = false;
        boolean lobby = false;
        boolean maintenance = !version.isServer();
        List<String> clients = new ArrayList<>();

        if (properties.containsKey("start-port")) {
            Integer startPort = CommonHelper.fromString(properties.getProperty("start-port"));
            if (startPort == null || startPort <= 0) {
                source.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("start-port")));
                return;
            }

            port = startPort;
        }

        if (properties.containsKey("max-players")) {
            Integer maxPlayerCount = CommonHelper.fromString(properties.getProperty("max-players"));
            if (maxPlayerCount == null || maxPlayerCount <= 0) {
                source.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("max-players")));
                return;
            }

            maxPlayers = maxPlayerCount;
        }

        if (properties.containsKey("start-priority")) {
            Integer startPriority = CommonHelper.fromString(properties.getProperty("start-priority"));
            if (startPriority == null) {
                source.sendMessage(LanguageManager.get("command-integer-failed-no-limit", properties.getProperty("start-priority")));
                return;
            }

            priority = startPriority;
        }

        if (properties.containsKey("max-memory")) {
            Integer maxMemory = CommonHelper.fromString(properties.getProperty("max-memory"));
            if (maxMemory == null || maxMemory <= 50) {
                source.sendMessage(LanguageManager.get("command-integer-failed", 50, properties.getProperty("max-memory")));
                return;
            }

            memory = maxMemory;
        }

        if (properties.containsKey("min-process-count")) {
            Integer minProcessCount = CommonHelper.fromString(properties.getProperty("min-process-count"));
            if (minProcessCount == null || minProcessCount <= -1) {
                source.sendMessage(LanguageManager.get("command-integer-failed", -1, properties.getProperty("min-process-count")));
                return;
            }

            min = minProcessCount;
        }

        if (properties.containsKey("max-process-count")) {
            Integer maxProcessCount = CommonHelper.fromString(properties.getProperty("max-process-count"));
            if (maxProcessCount == null || maxProcessCount <= -2) {
                source.sendMessage(LanguageManager.get("command-integer-failed", -2, properties.getProperty("max-process-count")));
                return;
            }

            max = maxProcessCount;
        }

        if (properties.containsKey("always-prepared")) {
            Integer alwaysPrepared = CommonHelper.fromString(properties.getProperty("always-prepared"));
            if (alwaysPrepared == null || alwaysPrepared <= -1) {
                source.sendMessage(LanguageManager.get("command-integer-failed", -1, properties.getProperty("always-prepared")));
                return;
            }

            prepared = alwaysPrepared;
        }

        if (properties.containsKey("static")) {
            Boolean isStatic = CommonHelper.booleanFromString(properties.getProperty("static"));
            if (isStatic == null) {
                source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("static")));
                return;
            }

            staticProcess = isStatic;
        }

        if (properties.containsKey("lobby")) {
            Boolean isLobby = CommonHelper.booleanFromString(properties.getProperty("lobby"));
            if (isLobby == null) {
                source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("lobby")));
                return;
            }

            lobby = isLobby;
        }

        if (properties.containsKey("maintenance")) {
            Boolean isMaintenance = CommonHelper.booleanFromString(properties.getProperty("maintenance"));
            if (isMaintenance == null) {
                source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("maintenance")));
                return;
            }

            maintenance = isMaintenance;
        }

        if (properties.containsKey("main-groups")) {
            String[] mainGroups = properties.getProperty("main-groups").contains(";")
                    ? properties.getProperty("main-groups").split(";")
                    : new String[]{properties.getProperty("main-groups")};

            Collection<MainGroup> basedOn = new ArrayList<>();
            for (String mainGroup : mainGroups) {
                MainGroup group = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(mainGroup);
                if (group == null) {
                    source.sendMessage(LanguageManager.get("command-create-main-group-does-not-exists", mainGroup));
                    return;
                }

                if (basedOn.contains(group) || group.getSubGroups().contains(name)) {
                    continue;
                }

                basedOn.add(group);
            }

            basedOn.forEach(e -> {
                e.getSubGroups().add(name);
                ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateMainGroup(e);
            });
        }

        if (properties.containsKey("startup-pickers")) {
            String[] startPickers = properties.getProperty("startup-pickers").contains(";")
                    ? properties.getProperty("startup-pickers").split(";")
                    : new String[]{properties.getProperty("startup-pickers")};

            for (String picker : startPickers) {
                if (clients.contains(picker)) {
                    continue;
                }

                clients.add(picker);
            }
        }

        ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().createProcessGroup(new DefaultProcessGroup(
                name,
                port,
                version,
                memory,
                maintenance,
                min,
                max,
                prepared,
                priority,
                staticProcess,
                lobby,
                clients,
                maxPlayers
        ));
        source.sendMessage(LanguageManager.get("command-create-pg", name, version.getName()));
    }
}
