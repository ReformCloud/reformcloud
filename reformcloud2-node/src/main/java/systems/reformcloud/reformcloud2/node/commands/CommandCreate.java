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
package systems.reformcloud.reformcloud2.node.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;
import systems.reformcloud.reformcloud2.executor.api.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.basic.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.basic.FileTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.AutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.utility.StringUtil;

import java.util.*;

public final class CommandCreate implements Command {

    public void describeCommandToSender(@NotNull CommandSender source) {
        source.sendMessages((
            "create new pg <name> <version>     | Creates a new process group\n" +
                " --start-port=[port]               | Sets the start port of the new process group\n" +
                " --max-memory=[memory]             | Sets the max-memory of the process group (default: 512)\n" +
                " --min-process-count=[min]         | Sets the min process count for the group (default: 1)\n" +
                " --max-process-count=[max]         | Sets the max process count for the group (default: -1)\n" +
                " --always-prepared=[prepared]      | Sets the amount of processes which should always be preared (default: 1)\n" +
                " --max-players=[max]               | Sets the max player count for the processes (default: proxies: 512, servers: 20)\n" +
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
    public void process(@NotNull CommandSender sender, String[] strings, @NotNull String commandLine) {
        if (strings.length <= 2 || !strings[0].equalsIgnoreCase("new")) {
            this.describeCommandToSender(sender);
            return;
        }

        if (strings[1].equalsIgnoreCase("pg")) {
            this.handleProcessGroupRequest(sender, strings);
            return;
        }

        if (strings[1].equalsIgnoreCase("mg")) {
            this.handleMainGroupRequest(sender, strings);
            return;
        }

        this.describeCommandToSender(sender);
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender commandSender, String[] strings, int bufferIndex, @NotNull String commandLine) {
        List<String> result = new ArrayList<>();
        if (bufferIndex == 0) {
            result.add("new");
        } else if (bufferIndex == 1) {
            result.addAll(Arrays.asList("pg", "mg"));
        } else if (bufferIndex >= 3) {
            if (bufferIndex == 3 && strings[1].equalsIgnoreCase("mg")) {
                result.add("--sub-groups=");
            } else if (bufferIndex == 3 && strings[1].equalsIgnoreCase("pg")) {
                for (Version value : EnumUtil.getEnumEntries(Version.class)) {
                    result.add(value.name());
                }
            } else if (bufferIndex > 3 && strings[1].equalsIgnoreCase("pg")) {
                result.addAll(Arrays.asList("--start-port=25565", "--max-memory=512", "--min-process-count=1", "--max-process-count=-1",
                    "--always-prepared=1", "--max-players=20", "--static=false", "--lobby=false", "--maintenance=false", "--main-groups=", "--startup-pickers="));
            }
        }

        return result;
    }

    private void handleMainGroupRequest(CommandSender source, String[] strings) {
        if (strings.length != 3 && strings.length != 4) {
            this.describeCommandToSender(source);
            return;
        }

        String name = strings[2];
        List<String> subGroups = new ArrayList<>();
        if (ExecutorAPI.getInstance().getMainGroupProvider().getMainGroup(name).isPresent()) {
            source.sendMessage(LanguageManager.get("command-create-main-group-already-exists", name));
            return;
        }

        Properties properties = StringUtil.calcProperties(strings, 3);
        if (properties.containsKey("sub-groups")) {
            String[] subGroupsStrings = properties.getProperty("sub-groups").contains(";")
                ? properties.getProperty("sub-groups").split(";")
                : new String[]{properties.getProperty("sub-groups")};

            for (String subGroup : subGroupsStrings) {
                if (!ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(subGroup).isPresent()) {
                    source.sendMessage(LanguageManager.get("command-create-sub-group-does-not-exists", subGroup));
                    return;
                }

                if (subGroups.contains(subGroup)) {
                    continue;
                }

                subGroups.add(subGroup);
            }
        }

        ExecutorAPI.getInstance().getMainGroupProvider().createMainGroup(name).subGroups(subGroups).create();
        source.sendMessage(LanguageManager.get("command-create-mg", name));
    }

    private void handleProcessGroupRequest(CommandSender source, String[] strings) {
        if (strings.length <= 3) {
            this.describeCommandToSender(source);
            return;
        }

        String name = strings[2];
        Version version = EnumUtil.findEnumFieldByName(Version.class, strings[3].toUpperCase()).orElse(null);
        if (version == null) {
            source.sendMessage(LanguageManager.get("command-create-version-not-found", strings[3]));
            return;
        }

        if (ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(name).isPresent()) {
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
                Optional<MainGroup> group = ExecutorAPI.getInstance().getMainGroupProvider().getMainGroup(mainGroup);
                if (!group.isPresent()) {
                    source.sendMessage(LanguageManager.get("command-create-main-group-does-not-exists", mainGroup));
                    return;
                }

                if (basedOn.contains(group.get()) || group.get().getSubGroups().contains(name)) {
                    continue;
                }

                basedOn.add(group.get());
            }

            basedOn.forEach(e -> {
                e.getSubGroups().add(name);
                ExecutorAPI.getInstance().getMainGroupProvider().updateMainGroup(e);
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

        ExecutorAPI.getInstance().getProcessGroupProvider().createProcessGroup(name)
            .templates(new Template(
                0,
                "default",
                false,
                FileTemplateBackend.NAME,
                "-",
                new RuntimeConfiguration(
                    memory,
                    new ArrayList<>(),
                    new HashMap<>()
                ), version,
                new ArrayList<>(),
                Collections.singletonList(version.isServer() ? DefaultProcessGroup.SERVER_INCLUSION : DefaultProcessGroup.PROXY_INCLUSION)
            ))
            .startupConfiguration(new StartupConfiguration(
                max,
                min,
                prepared,
                port,
                "java",
                new AutomaticStartupConfiguration(false, 90, 30),
                clients.isEmpty(),
                clients
            ))
            .playerAccessConfig(new PlayerAccessConfiguration(
                "reformcloud.join.full",
                maintenance,
                "reformcloud.join.maintenance",
                false,
                "reformcloud.join",
                true,
                maxPlayers
            ))
            .staticGroup(staticProcess)
            .lobby(lobby)
            .createPermanently();
        source.sendMessage(LanguageManager.get("command-create-pg", name, version.getName()));
    }
}
