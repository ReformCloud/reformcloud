package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.util.*;
import java.util.stream.Collectors;

import static systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultProcessGroup.PROXY_INCLUSION;
import static systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultProcessGroup.SERVER_INCLUSION;

public final class CommandGroup extends GlobalCommand {

    public CommandGroup() {
        super("group", "reformcloud.command.group", "Manage all sub and main groups", "g", "groups");
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessages((
                "group <list>                                   | Shows all registered main and process groups\n" +
                        "group <sub | main> <name> [info]               | Shows information about a specific group\n" +
                        "group <sub | main> <name> [delete]             | Deletes the specified process group\n" +
                        "group <sub | main> <name> [stop]               | Stops either all processes of the group or all sub groups of the main group\n" +
                        " \n" +
                        "group <sub> <name> [edit]                      | Edits the specified group\n" +
                        " --maintenance=[maintenance]                   | Enables or disables the maintenance mode\n" +
                        " --static=[static]                             | Enables or disables the deleting of the process after the stop\n" +
                        " --lobby=[lobby]                               | Sets if the group can be used as lobby\n" +
                        " --max-players=[max]                           | Sets the max player count for the process\n" +
                        " --min-process-count=[min]                     | Sets the min process count for the process\n" +
                        " --max-process-count=[max]                     | Sets the max process count for the process\n" +
                        " --always-prepared-process-count=[count]       | Sets the count of processes which should always be prepared\n" +
                        " --start-port=[port]                           | Sets the start port of the group\n" +
                        " --max-memory=[default/memory]                 | Sets the max memory of the template (format: <template-name>/<max-memory>)\n" +
                        " --start-priority=[priority]                   | Sets the start priority for the group\n" +
                        " --startup-pickers=[Client1;Node2]             | Sets the startup pickers for the group\n" +
                        " --add-startup-pickers=[Client1;Node2]         | Adds the specified startup pickers to the group\n" +
                        " --remove-startup-pickers=[Client1;Node2]      | Removes the specified startup pickers from the group\n" +
                        " --clear-startup-pickers=true                  | Clears the startup pickers\n" +
                        " --templates=[default/FILE/WATERFALL;...]      | Sets the templates of the group (format: <name>/<backend>/<version>)\n" +
                        " --add-templates=[default/FILE/WATERFALL;...]  | Adds the specified templates to the group (format: <name>/<backend>/<version>)\n" +
                        " --remove-templates=[default;global]           | Removes the specified templates from the group\n" +
                        " --clear-templates=true                        | Clears the templates of the group\n" +
                        " \n" +
                        "group <main> <name> [edit]                     | Edits the specified main group\n" +
                        " --sub-groups=[Group1;Group2]                  | Sets the sub groups of the main group\n" +
                        " --add-sub-groups=[Group1;Group2]              | Adds the sub groups to the main group\n" +
                        " --remove-sub-groups[Group1;Group2]            | Removes the sub groups from the main group\n" +
                        " --clear-sub-groups=true                       | Clears the sub groups of the main group"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
            this.listGroupsToSender(commandSource);
            return true;
        }

        if (strings.length <= 2) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        Properties properties = StringUtil.calcProperties(strings, 2);
        if (strings[0].equalsIgnoreCase("sub")) {
            this.handleSubGroupRequest(commandSource, strings, properties);
            return true;
        }

        if (strings[0].equalsIgnoreCase("main")) {
            this.handleMainGroupRequest(commandSource, strings, properties);
            return true;
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private void handleSubGroupRequest(CommandSource source, String[] strings, Properties properties) {
        ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[1]);
        if (processGroup == null) {
            source.sendMessage(LanguageManager.get("command-group-sub-group-not-exists", strings[1]));
            return;
        }

        if (strings.length == 3 && strings[2].equalsIgnoreCase("stop")) {
            List<ProcessInformation> processes = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName());
            source.sendMessage(LanguageManager.get("command-group-stopping-all", processGroup.getName()));
            processes.forEach(e -> ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(e.getProcessDetail().getProcessUniqueID()).onComplete(f -> {
            }));
            return;
        }

        if (strings.length == 3 && strings[2].equalsIgnoreCase("info")) {
            this.describeProcessGroupToSender(source, processGroup);
            return;
        }

        if (strings.length == 3 && strings[2].equalsIgnoreCase("delete")) {
            ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().deleteProcessGroup(processGroup.getName());
            source.sendMessage(LanguageManager.get("command-group-sub-delete", processGroup.getName()));
            return;
        }

        if (strings.length >= 4 && strings[2].equalsIgnoreCase("edit")) {
            if (properties.containsKey("maintenance")) {
                Boolean maintenance = CommonHelper.booleanFromString(properties.getProperty("maintenance"));
                if (maintenance == null) {
                    source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("maintenance")));
                    return;
                }

                processGroup.getPlayerAccessConfiguration().setMaintenance(maintenance);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "maintenance",
                        processGroup.getPlayerAccessConfiguration().isMaintenance()
                ));
            }

            if (properties.containsKey("static")) {
                Boolean isStatic = CommonHelper.booleanFromString(properties.getProperty("static"));
                if (isStatic == null) {
                    source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("static")));
                    return;
                }

                processGroup.setStaticProcess(isStatic);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "static",
                        processGroup.isStaticProcess()
                ));
            }

            if (properties.containsKey("lobby")) {
                Boolean isLobby = CommonHelper.booleanFromString(properties.getProperty("lobby"));
                if (isLobby == null) {
                    source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("lobby")));
                    return;
                }

                processGroup.setCanBeUsedAsLobby(isLobby);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "lobby",
                        isLobby
                ));
            }

            if (properties.containsKey("max-players")) {
                Integer maxPlayers = CommonHelper.fromString(properties.getProperty("max-players"));
                if (maxPlayers == null || maxPlayers <= 0) {
                    source.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("max-players")));
                    return;
                }

                processGroup.getPlayerAccessConfiguration().setMaxPlayers(maxPlayers);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "max-players",
                        processGroup.getPlayerAccessConfiguration().getMaxPlayers()
                ));
            }

            if (properties.containsKey("max-memory")) {
                String[] split = properties.getProperty("max-memory").split("/");
                if (split.length == 2) {
                    Integer maxMemory = CommonHelper.fromString(split[1]);
                    if (maxMemory == null || maxMemory <= 50) {
                        source.sendMessage(LanguageManager.get("command-integer-failed", 50, split[1]));
                        return;
                    }

                    Template template = processGroup.getTemplate(split[0]);
                    if (template != null) {
                        template.getRuntimeConfiguration().setMaxMemory(maxMemory);
                        source.sendMessage(LanguageManager.get(
                                "command-group-edit",
                                "max-memory",
                                split[0] + "/" + maxMemory
                        ));
                    }
                }
            }

            if (properties.containsKey("min-process-count")) {
                Integer minProcessCount = CommonHelper.fromString(properties.getProperty("min-process-count"));
                if (minProcessCount == null || minProcessCount < 0) {
                    source.sendMessage(LanguageManager.get("command-integer-failed", -1, properties.getProperty("min-process-count")));
                    return;
                }

                processGroup.getStartupConfiguration().setMinOnlineProcesses(minProcessCount);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "min-process-count",
                        processGroup.getStartupConfiguration().getMinOnlineProcesses()
                ));
            }

            if (properties.containsKey("max-process-count")) {
                Integer maxProcessCount = CommonHelper.fromString(properties.getProperty("max-process-count"));
                if (maxProcessCount == null || maxProcessCount <= -2) {
                    source.sendMessage(LanguageManager.get("command-integer-failed", -2, properties.getProperty("max-process-count")));
                    return;
                }

                processGroup.getStartupConfiguration().setMaxOnlineProcesses(maxProcessCount);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "max-process-count",
                        processGroup.getStartupConfiguration().getMaxOnlineProcesses()
                ));
            }

            if (properties.containsKey("always-prepared-process-count")) {
                Integer alwaysPreparedCount = CommonHelper.fromString(properties.getProperty("always-prepared-process-count"));
                if (alwaysPreparedCount == null || alwaysPreparedCount < 0) {
                    source.sendMessage(LanguageManager.get("command-integer-failed", -1, properties.getProperty("always-prepared-process-count")));
                    return;
                }

                processGroup.getStartupConfiguration().setAlwaysPreparedProcesses(alwaysPreparedCount);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "always-prepared-process-count",
                        processGroup.getStartupConfiguration().getAlwaysPreparedProcesses()
                ));
            }

            if (properties.containsKey("start-port")) {
                Integer startPort = CommonHelper.fromString(properties.getProperty("start-port"));
                if (startPort == null || startPort <= 0) {
                    source.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("start-port")));
                    return;
                }

                processGroup.getStartupConfiguration().setStartPort(startPort);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "start-port",
                        processGroup.getStartupConfiguration().getStartPort()
                ));
            }

            if (properties.containsKey("start-priority")) {
                Integer startPriority = CommonHelper.fromString(properties.getProperty("start-priority"));
                if (startPriority == null || startPriority <= 0) {
                    source.sendMessage(LanguageManager.get("command-integer-failed-no-limit", properties.getProperty("start-priority")));
                    return;
                }

                processGroup.getStartupConfiguration().setStartupPriority(startPriority);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "start-priority",
                        startPriority
                ));
            }

            if (properties.containsKey("startup-pickers")) {
                List<String> startPickers = this.parseStrings(properties.getProperty("startup-pickers"));
                processGroup.getStartupConfiguration().setUseOnlyTheseClients(startPickers);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "startup-pickers",
                        String.join(", ", startPickers)
                ));
            }

            if (properties.containsKey("add-startup-pickers")) {
                List<String> startPickers = this.parseStrings(properties.getProperty("add-startup-pickers"));
                startPickers.addAll(processGroup.getStartupConfiguration().getUseOnlyTheseClients());
                processGroup.getStartupConfiguration().setUseOnlyTheseClients(startPickers);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "startup-pickers",
                        String.join(", ", startPickers)
                ));
            }

            if (properties.containsKey("remove-startup-pickers")) {
                List<String> startPickers = processGroup.getStartupConfiguration().getUseOnlyTheseClients();
                startPickers.removeAll(this.parseStrings(properties.getProperty("remove-startup-pickers")));
                processGroup.getStartupConfiguration().setUseOnlyTheseClients(startPickers);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "startup-pickers",
                        String.join(", ", startPickers)
                ));
            }

            if (properties.containsKey("clear-startup-pickers")) {
                Boolean clear = CommonHelper.booleanFromString(properties.getProperty("clear-startup-pickers"));
                if (clear == null) {
                    source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("clear-startup-pickers")));
                    return;
                }

                if (clear) {
                    processGroup.getStartupConfiguration().setUseOnlyTheseClients(new ArrayList<>());
                    source.sendMessage(LanguageManager.get(
                            "command-group-edit",
                            "use-specific-start-picker",
                            "false"
                    ));
                }
            }

            if (properties.containsKey("templates")) {
                List<Template> newTemplates = this.parseTemplates(this.parseStrings(properties.getProperty("templates")), source, processGroup);
                if (!newTemplates.isEmpty()) {
                    processGroup.setTemplates(newTemplates);
                    source.sendMessage(LanguageManager.get(
                            "command-group-edit",
                            "templates",
                            newTemplates.stream().map(Template::getName).collect(Collectors.joining(", "))
                    ));
                }
            }

            if (properties.containsKey("add-templates")) {
                List<Template> newTemplates = this.parseTemplates(this.parseStrings(properties.getProperty("add-templates")), source, processGroup);
                if (!newTemplates.isEmpty()) {
                    newTemplates.addAll(processGroup.getTemplates());
                    processGroup.setTemplates(newTemplates);
                    source.sendMessage(LanguageManager.get(
                            "command-group-edit",
                            "add-templates",
                            newTemplates.stream().map(Template::getName).collect(Collectors.joining(", "))
                    ));
                }
            }

            if (properties.containsKey("remove-templates")) {
                Collection<String> templatesToRemove = this.parseStrings(properties.getProperty("remove-templates"));
                Collection<Template> toRemove = Streams.allOf(processGroup.getTemplates(), e -> templatesToRemove.contains(e.getName()));
                processGroup.getTemplates().removeAll(toRemove);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "remove-templates",
                        toRemove.stream().map(Template::getName).collect(Collectors.joining(", "))
                ));
            }

            if (properties.containsKey("clear-templates")) {
                Boolean clear = CommonHelper.booleanFromString(properties.getProperty("clear-templates"));
                if (clear == null) {
                    source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("clear-templates")));
                    return;
                }

                if (clear) {
                    processGroup.getTemplates().clear();
                    source.sendMessage(LanguageManager.get(
                            "command-group-edit",
                            "templates",
                            "clear"
                    ));
                }
            }

            ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateProcessGroup(processGroup);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(e -> {
                e.setProcessGroup(processGroup);
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(e);
                System.out.println(LanguageManager.get("command-group-edited-running-process", e.getProcessDetail().getName()));
            });
            return;
        }

        this.describeCommandToSender(source);
    }

    private void handleMainGroupRequest(CommandSource source, String[] strings, Properties properties) {
        MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[1]);
        if (mainGroup == null) {
            source.sendMessage(LanguageManager.get("command-group-main-group-not-exists", strings[1]));
            return;
        }

        if (strings.length == 3 && strings[2].equalsIgnoreCase("info")) {
            this.describeMainGroupToSender(source, mainGroup);
            return;
        }

        if (strings.length == 3 && strings[2].equalsIgnoreCase("delete")) {
            ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().deleteMainGroup(mainGroup.getName());
            source.sendMessage(LanguageManager.get("command-group-main-delete", mainGroup.getName()));
            return;
        }

        if (strings.length == 3 && strings[2].equalsIgnoreCase("stop")) {
            for (String subGroup : mainGroup.getSubGroups()) {
                Collection<ProcessInformation> running = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(subGroup);
                source.sendMessage(LanguageManager.get("command-group-stopping-all", subGroup));
                running.forEach(e -> ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(e.getProcessDetail().getProcessUniqueID()).onComplete(f -> {
                }));
                AbsoluteThread.sleep(50);
            }

            return;
        }

        if (strings.length >= 4 && strings[2].equalsIgnoreCase("edit")) {
            if (properties.containsKey("sub-groups")) {
                List<String> groups = this.parseStrings(properties.getProperty("sub-groups"));
                mainGroup.setSubGroups(groups);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "sub-groups",
                        String.join(", ", groups)
                ));
            }

            if (properties.containsKey("add-sub-groups")) {
                List<String> groups = this.parseStrings(properties.getProperty("add-sub-groups"));
                Streams.allOf(mainGroup.getSubGroups(), e -> groups.contains(e)).forEach(groups::remove);
                mainGroup.getSubGroups().addAll(groups);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "sub-groups",
                        String.join(", ", mainGroup.getSubGroups())
                ));
            }

            if (properties.containsKey("remove-sub-groups")) {
                List<String> groups = this.parseStrings(properties.getProperty("remove-sub-groups"));
                mainGroup.getSubGroups().removeAll(groups);
                source.sendMessage(LanguageManager.get(
                        "command-group-edit",
                        "sub-groups-remove",
                        String.join(", ", groups)
                ));
            }

            if (properties.containsKey("clear-sub-groups")) {
                Boolean clear = CommonHelper.booleanFromString(properties.getProperty("clear-sub-groups"));
                if (clear == null) {
                    source.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("clear-sub-groups")));
                    return;
                }

                if (clear) {
                    mainGroup.getSubGroups().clear();
                    source.sendMessage(LanguageManager.get(
                            "command-group-edit",
                            "sub-groups",
                            "clear"
                    ));
                }
            }

            ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateMainGroup(mainGroup);
            return;
        }

        this.describeCommandToSender(source);
    }

    private void describeProcessGroupToSender(CommandSource source, ProcessGroup group) {
        StringBuilder builder = new StringBuilder();

        builder.append(" > Name        - ").append(group.getName()).append("\n");
        builder.append(" > Lobby       - ").append(group.isCanBeUsedAsLobby() ? "&ayes&r" : "&cno&r").append("\n");
        builder.append(" > Max-Players - ").append(group.getPlayerAccessConfiguration().getMaxPlayers()).append("\n");
        builder.append(" > Maintenance - ").append(group.getPlayerAccessConfiguration().isMaintenance() ? "&ayes&r" : "&cno&r").append("\n");
        builder.append(" > Min-Online  - ").append(group.getStartupConfiguration().getMinOnlineProcesses()).append("\n");
        builder.append(" > Max-Online  - ").append(group.getStartupConfiguration().getMaxOnlineProcesses()).append("\n");
        builder.append(" > Start-Port  - ").append(group.getStartupConfiguration().getStartPort()).append("\n");

        builder.append(" ").append("\n");
        builder.append(" > Templates (").append(group.getTemplates().size()).append(")");

        for (Template template : group.getTemplates()) {
            builder.append("\n");
            builder.append("  > Name       - ").append(template.getName()).append("\n");
            builder.append("  > Version    - ").append(template.getVersion().getName()).append("\n");
            builder.append("  > Backend    - ").append(template.getBackend()).append("\n");
            builder.append("  > Priority   - ").append(template.getPriority()).append("\n");
            builder.append("  > Max-Memory - ").append(template.getRuntimeConfiguration().getMaxMemory()).append("MB\n");
            builder.append("  > Global     - ").append(template.isGlobal() ? "&ayes&r" : "&cno&r").append("\n");
            builder.append(" ");
        }

        source.sendMessages(builder.toString().split("\n"));
    }

    private void describeMainGroupToSender(CommandSource source, MainGroup mainGroup) {
        String prefix = " > Sub-Groups (" + mainGroup.getSubGroups().size() + ")";
        StringBuilder append = new StringBuilder();
        for (int i = 7; i <= prefix.length(); i++) {
            append.append(" ");
        }

        append.append("- ");

        String s = " > Name" + append.toString() + mainGroup.getName()
                + "\n" + prefix + " - " + String.join(", ", mainGroup.getSubGroups()) + "\n";
        source.sendMessages(s.split("\n"));
    }

    private void listGroupsToSender(CommandSource source) {
        StringBuilder builder = new StringBuilder();

        List<MainGroup> mainGroups = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroups();
        List<ProcessGroup> processGroups = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups();

        builder.append(" Main-Groups (").append(mainGroups.size()).append(")");
        for (MainGroup mainGroup : mainGroups) {
            builder.append("\n");
            builder.append("  > Name       - ").append(mainGroup.getName()).append("\n");
            builder.append("  > Sub-Groups - ").append(String.join(", ", mainGroup.getSubGroups())).append("\n");
            builder.append(" ");
        }

        builder.append(mainGroups.isEmpty() ? "\n" : "").append(" \n");

        builder.append(" Process-Groups (").append(processGroups.size()).append(")");
        for (ProcessGroup processGroup : processGroups) {
            builder.append("\n");
            builder.append(" > Name            - ").append(processGroup.getName()).append("\n");
            builder.append(" > Min-Processes   - ").append(processGroup.getStartupConfiguration().getMinOnlineProcesses()).append("\n");
            builder.append(" > Max-Processes   - ").append(processGroup.getStartupConfiguration().getMaxOnlineProcesses()).append("\n");
            builder.append(" > Start-Port      - ").append(processGroup.getStartupConfiguration().getStartPort()).append("\n");
            builder.append(" > Startup-Pickers - ").append(processGroup.getStartupConfiguration().getUseOnlyTheseClients().isEmpty()
                    ? "undefined" : String.join(", ", processGroup.getStartupConfiguration().getUseOnlyTheseClients())
            ).append("\n");
            builder.append(" ");
        }

        source.sendMessages(builder.toString().split("\n"));
    }

    private List<String> parseStrings(String s) {
        List<String> out = new ArrayList<>();
        if (s.contains(";")) {
            String[] split = s.split(";");
            for (String s1 : split) {
                if (out.contains(s1)) {
                    continue;
                }

                out.add(s1);
            }
        } else {
            out.add(s);
        }

        return out;
    }

    private List<Template> parseTemplates(Collection<String> collection, CommandSource source, ProcessGroup processGroup) {
        List<Template> newTemplates = new ArrayList<>();
        for (String template : collection) {
            String[] templateConfig = template.split("/");
            if (templateConfig.length != 3) {
                source.sendMessage(LanguageManager.get("command-group-template-format-error", template));
                continue;
            }

            if (processGroup.getTemplate(templateConfig[0]) != null) {
                source.sendMessage(LanguageManager.get("command-group-template-already-exists", templateConfig[0]));
                continue;
            }

            TemplateBackend backend = TemplateBackendManager.get(templateConfig[1]);
            if (backend == null) {
                source.sendMessage(LanguageManager.get("command-group-template-backend-invalid", templateConfig[1]));
                continue;
            }

            Version version = CommonHelper.findEnumField(Version.class, templateConfig[2].toUpperCase()).orNothing();
            if (version == null) {
                source.sendMessage(LanguageManager.get("command-group-template-version-not-found", templateConfig[2]));
                continue;
            }

            newTemplates.add(new Template(0, templateConfig[0], false, backend.getName(), "#", new RuntimeConfiguration(
                    version.isServer() ? 512 : 256, new ArrayList<>(), new HashMap<>()
            ), version, new ArrayList<>(), new ArrayList<>(Collections.singletonList(version.isServer() ? SERVER_INCLUSION : PROXY_INCLUSION))));
        }
        return newTemplates;
    }
}
