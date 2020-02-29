package systems.reformcloud.reformcloud2.executor.api.common.utility;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StringUtil {

    public static final String RUNNER_DOWNLOAD_URL = "https://internal.reformcloud.systems/runner.jar";

    public static final String NULL_PATH = new File("reformcloud/.bin/dev/null").getAbsolutePath();

    private static final String[] RC_COMMAND_BASIC_HELP = new String[] {
            "rc applications",
            "rc applications update",
            "rc applications update <name>",
            "rc maintenance <group>",
            "rc copy <uuid | name>",
            "rc screen <uuid | name> toggle",
            "rc list",
            "rc list <group>",
            "rc listgroups <main | sub>",
            "rc versions",
            "rc start <group>",
            "rc start <group> <amount>",
            "rc start <group> <amount> <template>",
            "rc stop <name>",
            "rc stop <uuid>",
            "rc stopall <subGroup>",
            "rc ofAll <mainGroup> <list | stop>",
            "rc execute <name | uuid> <command>",
            "rc create main <name>",
            "rc create sub <name>",
            "rc create sub <name> <version>",
            "rc create sub <name> <version> <parent>",
            "rc create sub <name> <version> <parent> <static>",
            "rc create sub <name> <version> <parent> <static> <lobby>",
            "rc create sub <name> <version> <parent> <static> <minonline> <maxonline>",
            "rc create sub <name> <version> <parent> <static> <lobby> <minonline> <maxonline>",
            "rc delete <sub | main> <name>"
    };

    private static String[] commandHelp;

    public static String[] getCommandHelp() {
        if (commandHelp == null) {
            commandHelp = loadHelpMessage();
        }

        return commandHelp;
    }

    private static String[] loadHelpMessage() {
        String[] strings;

        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            strings = new String[] {
                    "rc create node <ip/domain-name> <port>"
            };
        } else {
            strings = new String[] {
                    "rc clients",
                    "rc start internalclient",
                    "rc stop internalclient",
                    "rc create internalclient <start-host>",
                    "rc delete internalclient"
            };
        }

        return Streams.concat(RC_COMMAND_BASIC_HELP, strings);
    }

    public static List<String> completeReformCommand(@Nonnull String[] currentArg, boolean isController) {
        List<String> completions = new ArrayList<>();
        if (currentArg.length == 0) {
            if (isController) {
                completions.add("clients");
            }

            completions.addAll(Arrays.asList("applications", "list", "maintenance", "copy",
                    "screen", "listgroups", "versions", "start", "stop", "stopall", "ofall", "execute", "create", "delete"));
            return completions;
        }

        if (currentArg.length == 1) {
            if (currentArg[0].equalsIgnoreCase("applications")) {
                completions.add("update");
            } else if (currentArg[0].equalsIgnoreCase("maintenance") || currentArg[0].equalsIgnoreCase("list")) {
                completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups().stream().map(ProcessGroup::getName).collect(Collectors.toList()));
            } else if (currentArg[0].equalsIgnoreCase("copy")
                    || currentArg[0].equalsIgnoreCase("screen")
                    || currentArg[0].equalsIgnoreCase("execute")
            ) {
                completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().stream().map(ProcessInformation::getName).collect(Collectors.toList()));
            } else if (currentArg[0].equalsIgnoreCase("listgroups")) {
                completions.addAll(Arrays.asList("main", "sub"));
            } else if (currentArg[0].equalsIgnoreCase("start")) {
                if (isController) {
                    completions.add("internalclient");
                }

                completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups().stream().map(ProcessGroup::getName).collect(Collectors.toList()));
            } else if (currentArg[0].equalsIgnoreCase("stop")) {
                if (isController) {
                    completions.add("internalclient");
                }

                completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().stream().map(ProcessInformation::getName).collect(Collectors.toList()));
            } else if (currentArg[0].equalsIgnoreCase("stopall")) {
                completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups().stream().map(ProcessGroup::getName).collect(Collectors.toList()));
            } else if (currentArg[0].equalsIgnoreCase("ofall")) {
                completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroups().stream().map(MainGroup::getName).collect(Collectors.toList()));
            } else if (currentArg[0].equalsIgnoreCase("create")) {
                if (isController) {
                    completions.add("internalclient");
                }

                completions.addAll(Arrays.asList("main", "sub"));
            } else if (currentArg[0].equalsIgnoreCase("delete")) {
                if (isController) {
                    completions.add("internalclient");
                }

                completions.addAll(Arrays.asList("sub", "main"));
            }

            return completions;
        }

        if (currentArg.length == 2) {
            if (currentArg[0].equalsIgnoreCase("applications") && currentArg[1].equalsIgnoreCase("update")) {
                if (ExecutorAPI.getInstance().getSyncAPI().getApplicationSyncAPI().getApplications() == null) {
                    return completions;
                }

                completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getApplicationSyncAPI().getApplications().stream()
                        .map(LoadedApplication::getName).collect(Collectors.toList())
                );
            } else if (currentArg[0].equalsIgnoreCase("screen")) {
                completions.add("toggle");
            } else if (currentArg[0].equalsIgnoreCase("start")) {
                for (int i = 0; i <= 10; ++i) {
                    completions.add(Integer.toString(i));
                }
            } else if (currentArg[0].equalsIgnoreCase("ofall")) {
                completions.addAll(Arrays.asList("list", "stop"));
            } else if (currentArg[0].equalsIgnoreCase("create")) {
                if (currentArg[1].equalsIgnoreCase("internalclient") && isController) {
                    completions.add("127.0.0.1");
                } else if (currentArg[1].equalsIgnoreCase("sub") || currentArg[1].equalsIgnoreCase("main")) {
                    completions.addAll(Arrays.asList("Lobby", "Proxy"));
                }
            } else if (currentArg[0].equalsIgnoreCase("delete")) {
                if (currentArg[1].equalsIgnoreCase("sub")) {
                    completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups().stream().map(ProcessGroup::getName).collect(Collectors.toList()));
                } else if (currentArg[1].equalsIgnoreCase("main")) {
                    completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroups().stream().map(MainGroup::getName).collect(Collectors.toList()));
                }
            }

            return completions;
        }

        if (currentArg.length == 3) {
            if (currentArg[0].equalsIgnoreCase("create") && currentArg[1].equalsIgnoreCase("sub")) {
                completions.addAll(Version.getJavaProxyProviders().values().stream().map(Version::name).collect(Collectors.toList()));
                completions.addAll(Version.getJavaServerProviders().values().stream().map(Version::name).collect(Collectors.toList()));
                completions.addAll(Version.getPocketProxyProviders().values().stream().map(Version::name).collect(Collectors.toList()));
                completions.addAll(Version.getPocketServerProviders().values().stream().map(Version::name).collect(Collectors.toList()));
            } else if (currentArg[0].equalsIgnoreCase("start")) {
                ProcessGroup group = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(currentArg[1]);
                if (group == null) {
                    return completions;
                }

                completions.addAll(group.getTemplates().stream().map(Template::getName).collect(Collectors.toList()));
            }

            return completions;
        }

        if (currentArg.length == 4) {
            if (currentArg[0].equalsIgnoreCase("create") && currentArg[1].equalsIgnoreCase("sub")) {
                completions.add("null");
                completions.addAll(ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroups().stream().map(MainGroup::getName).collect(Collectors.toList()));
            }

            return completions;
        }

        if (currentArg.length == 5) {
            if (currentArg[0].equalsIgnoreCase("create") && currentArg[1].equalsIgnoreCase("sub")) {
                completions.addAll(Arrays.asList("true", "false"));
            }

            return completions;
        }

        if (currentArg.length == 6) {
            if (currentArg[0].equalsIgnoreCase("create") && currentArg[1].equalsIgnoreCase("sub")) {
                completions.addAll(Arrays.asList("true", "false"));

                for (int i = 0; i <= 5; ++i) {
                    completions.add(Integer.toString(i));
                }
            }

            return completions;
        }

        if (currentArg.length == 7 || currentArg.length == 8) {
            if (currentArg[0].equalsIgnoreCase("create") && currentArg[1].equalsIgnoreCase("sub")) {
                for (int i = 0; i <= 5; ++i) {
                    completions.add(Integer.toString(i));
                }
            }

            return completions;
        }

        return completions;
    }

    public static void sendHeader() {
        if (Boolean.getBoolean("reformcloud.disable.header.show")) {
            System.out.println();
            return;
        }

        System.out.println(
                        "\n" +
                        "    __       __                        ___ _                 _ ____  \n" +
                        "   /__\\ ___ / _| ___  _ __ _ __ ___   / __\\ | ___  _   _  __| |___ \\ \n" +
                        "  / \\/// _ \\ |_ / _ \\| '__| '_ ` _ \\ / /  | |/ _ \\| | | |/ _` | __) |\n" +
                        " / _  \\  __/  _| (_) | |  | | | | | / /___| | (_) | |_| | (_| |/ __/\n" +
                        " \\/ \\_/\\___|_|  \\___/|_|  |_| |_| |_\\____/|_|\\___/ \\__,_|\\__,_|_____| git:"
                                + StringUtil.class.getPackage().getSpecificationVersion() + "\n" +
                        " \n" +
                        "                   Not just a cloud system, but an experience.\n"
        );
    }

    public static String generateString(int times) {
        Conditions.isTrue(times > 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            stringBuilder.append(UUID.randomUUID().toString().replace("-", ""));
        }

        return stringBuilder.toString();
    }

    public static String getConsolePrompt() {
        return LanguageManager.get("logger.console.prompt")
                .replace("%version%", System.getProperty("reformcloud.runner.version", "c-build"))
                .replace("%user_name%", System.getProperty("user.name", "unknown")) + " ";
    }
}
