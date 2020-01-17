package systems.reformcloud.reformcloud2.executor.node.commands;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupEnvironment;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.ControllerPacketOutCopyProcess;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.config.NodeConfig;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen.NodePacketOutToggleScreen;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreenHandler;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class CommandReformCloud extends GlobalCommand {

    public CommandReformCloud() {
        super("rc", "reformcloud.command.rc", "The main management command for reformcloud", Arrays.asList("reformcloud", "servers", "process", "proxies"));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("versions")) {
            {
                System.out.println(LanguageManager.get("command-rc-available-versions", "Java-Proxy"));
                StringBuilder stringBuilder = new StringBuilder();
                Version.getJavaProxyProviders().forEach((s, v) -> stringBuilder.append(v.name()).append(", "));
                System.out.println(stringBuilder.substring(0, stringBuilder.length() - 2));
            }

            {
                System.out.println(LanguageManager.get("command-rc-available-versions", "Pocket-Edition-Proxy"));
                StringBuilder stringBuilder = new StringBuilder();
                Version.getPocketProxyProviders().forEach((s, v) -> stringBuilder.append(v.name()).append(", "));
                System.out.println(stringBuilder.substring(0, stringBuilder.length() - 2));
            }

            {
                System.out.println(LanguageManager.get("command-rc-available-versions", "Java-Server"));
                StringBuilder stringBuilder = new StringBuilder();
                Version.getJavaServerProviders().forEach((s, v) -> stringBuilder.append(v.name()).append(", "));
                System.out.println(stringBuilder.substring(0, stringBuilder.length() - 2));
            }

            {
                System.out.println(LanguageManager.get("command-rc-available-versions", "Pocket-Edition-Server"));
                StringBuilder stringBuilder = new StringBuilder();
                Version.getPocketServerProviders().forEach((s, v) -> stringBuilder.append(v.name()).append(", "));
                System.out.println(stringBuilder.substring(0, stringBuilder.length() - 2));
            }
            return true;
        } else if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().forEach(processInformation -> System.out.println(
                    "  => "
                            + processInformation.getName()
                            + "/" + processInformation.getProcessUniqueID()
                            + " " + processInformation.getOnlineCount() + "/"
                            + processInformation.getMaxPlayers() + " "
                            + processInformation.getTemplate().getVersion()
            ));
            return true;
        } else if (strings.length == 1 && strings[0].equalsIgnoreCase("applications")) {
            System.out.println(LanguageManager.get("command-rc-loaded-applications"));
            ExecutorAPI.getInstance().getSyncAPI().getApplicationSyncAPI().getApplications().forEach(e ->
                    System.out.println("   => " + e.getName() + " / Version: " + e.applicationConfig().version())
            );
            return true;
        }

        if (strings.length <= 1) {
            sendHelp(commandSource);
            return true;
        }

        switch (strings[0].toLowerCase()) {
            case "applications": {
                if (strings.length == 2 && strings[1].equalsIgnoreCase("update")) {
                    System.out.println(LanguageManager.get("command-rc-fetching-updates"));
                    ControllerExecutor.getInstance().getApplicationLoader().fetchAllUpdates();
                    return true;
                }

                if (strings.length == 3 && strings[1].equalsIgnoreCase("update")) {
                    System.out.println(LanguageManager.get("command-rc-try-fetch", strings[2]));
                    ControllerExecutor.getInstance().getApplicationLoader().fetchUpdates(strings[2]);
                    return true;
                }

                break;
            }

            case "screen": {
                if (strings.length == 3) {
                    ProcessInformation processInformation;
                    UUID uuid = CommonHelper.tryParse(strings[1]);
                    if (uuid == null) {
                        processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(strings[1]);
                    } else {
                        processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uuid);
                    }

                    if (processInformation == null) {
                        System.out.println(LanguageManager.get("command-rc-process-unknown", strings[1]));
                        return true;
                    }

                    if (processInformation.getNodeUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
                        NodeProcessScreenHandler.getScreen(processInformation.getProcessUniqueID())
                                .ifPresent(e -> e.toggleFor(NodeExecutor.getInstance().getNodeConfig().getName()));
                    } else {
                        DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(packetSender -> packetSender.sendPacket(new NodePacketOutToggleScreen(processInformation.getProcessUniqueID())));
                    }

                    System.out.println(LanguageManager.get("command-rc-execute-success"));
                    return true;
                }
                break;
            }

            case "copy": {
                ProcessInformation processInformation;
                UUID uuid = CommonHelper.tryParse(strings[1]);
                if (uuid == null) {
                    processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(strings[1]);
                } else {
                    processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uuid);
                }

                if (processInformation == null) {
                    System.out.println(LanguageManager.get("command-rc-process-unknown", strings[1]));
                    return true;
                }

                if (NodeExecutor.getInstance().getNodeConfig().getUniqueID().equals(processInformation.getNodeUniqueID())) {
                    Links.filterToReference(LocalProcessManager.getNodeProcesses(),
                            e -> e.getProcessInformation().getProcessUniqueID().equals(processInformation.getProcessUniqueID())
                    ).ifPresent(LocalNodeProcess::copy);
                } else {
                    DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketOutCopyProcess(processInformation.getProcessUniqueID())));
                }

                System.out.println(LanguageManager.get("command-rc-execute-success"));
                return true;
            }

            case "maintenance": {
                ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[1]);
                if (processGroup == null) {
                    System.out.println(LanguageManager.get("command-rc-group-unknown", strings[1]));
                    return true;
                }

                processGroup.getPlayerAccessConfiguration().toggleMaintenance();
                ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().updateProcessGroupAsync(processGroup);
                System.out.println(LanguageManager.get("command-rc-execute-success"));
                return true;
            }

            case "start": {
                if (strings.length == 2) {
                    ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[1]);
                    if (processGroup == null) {
                        System.out.println(LanguageManager.get("command-rc-group-unknown", strings[1]));
                        return true;
                    }

                    ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().startProcessAsync(processGroup.getName());
                    System.out.println(LanguageManager.get("command-rc-execute-success"));
                    return true;
                }

                if (strings.length == 3) {
                    ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[1]);
                    if (processGroup == null) {
                        System.out.println(LanguageManager.get("command-rc-group-unknown", strings[1]));
                        return true;
                    }

                    Integer i = CommonHelper.fromString(strings[2]);
                    if (i == null || i < 1) {
                        System.out.println(LanguageManager.get("command-rc-integer-failed", strings[2]));
                        return true;
                    }

                    for (int started = 1; started <= i; started++) {
                        ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().startProcessAsync(processGroup.getName());
                        AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 20);
                    }

                    System.out.println(LanguageManager.get("command-rc-execute-success"));
                    return true;
                }

                if (strings.length == 4) {
                    ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[1]);
                    if (processGroup == null) {
                        System.out.println(LanguageManager.get("command-rc-group-unknown", strings[1]));
                        return true;
                    }

                    Integer i = CommonHelper.fromString(strings[2]);
                    if (i == null || i < 1) {
                        System.out.println(LanguageManager.get("command-rc-integer-failed", strings[2]));
                        return true;
                    }

                    for (int started = 0; started <= i; started++) {
                        ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().startProcessAsync(processGroup.getName(), strings[3]);
                        AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 20);
                    }

                    System.out.println(LanguageManager.get("command-rc-execute-success"));
                    return true;
                }
                break;
            }

            case "stop": {
                if (strings.length == 2) {
                    ProcessInformation processInformation;
                    UUID uuid = CommonHelper.tryParse(strings[1]);
                    if (uuid == null) {
                        processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(strings[1]);
                    } else {
                        processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uuid);
                    }

                    if (processInformation == null) {
                        System.out.println(LanguageManager.get("command-rc-process-unknown", strings[1]));
                        return true;
                    }

                    ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(processInformation.getProcessUniqueID());
                    System.out.println(LanguageManager.get("command-rc-execute-success"));
                    return true;
                }
                break;
            }

            case "stopall": {
                if (strings.length == 2) {
                    ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[1]);
                    if (processGroup == null) {
                        System.out.println(LanguageManager.get("command-rc-group-unknown", strings[1]));
                        return true;
                    }

                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(processInformation -> {
                        ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(processInformation.getProcessUniqueID());
                        AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 10);
                    });

                    System.out.println(LanguageManager.get("command-rc-execute-success"));
                    return true;
                }

                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().forEach(processInformation -> {
                    ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(processInformation.getProcessUniqueID());
                    AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 10);
                });
                System.out.println(LanguageManager.get("command-rc-execute-success"));
                break;
            }

            case "ofall": {
                if (strings.length == 3) {
                    if (strings[2].equalsIgnoreCase("list")) {
                        MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[1]);
                        if (mainGroup == null) {
                            System.out.println(LanguageManager.get("command-rc-main-group-unknown", strings[1]));
                            return true;
                        }

                        mainGroup.getSubGroups().forEach(s -> System.out.println(LanguageManager.get("command-rc-main-sub-group", s)));
                        return true;
                    } else if (strings[2].equalsIgnoreCase("stop")) {
                        MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[1]);
                        if (mainGroup == null) {
                            System.out.println(LanguageManager.get("command-rc-main-group-unknown", strings[1]));
                            return true;
                        }

                        mainGroup.getSubGroups().forEach(s -> {
                            ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(s);
                            if (processGroup == null) {
                                return;
                            }

                            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(processInformation -> {
                                ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(processInformation.getProcessUniqueID());
                                AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 10);
                            });
                        });

                        System.out.println(LanguageManager.get("command-rc-execute-success"));
                        return true;
                    }
                }
                break;
            }

            case "execute": {
                ProcessInformation processInformation;
                UUID uuid = CommonHelper.tryParse(strings[1]);
                if (uuid == null) {
                    processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(strings[1]);
                } else {
                    processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uuid);
                }

                if (processInformation == null) {
                    System.out.println(LanguageManager.get("command-rc-process-unknown", strings[1]));
                    return true;
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (String s : Arrays.copyOfRange(strings, 2, strings.length)) {
                    stringBuilder.append(s).append(" ");
                }

                ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().executeProcessCommandAsync(processInformation.getName(), stringBuilder.toString());
                System.out.println(LanguageManager.get("command-rc-execute-success"));
                return true;
            }

            case "create": {
                if (strings[1].equalsIgnoreCase("node") && strings.length == 4) {
                    if (strings[2].split("\\.").length != 4) {
                        System.out.println(LanguageManager.get("command-rc-node-ip-invalid", strings[2]));
                        return true;
                    }

                    Integer port = CommonHelper.fromString(strings[3]);
                    if (port == null || port < 0) {
                        System.out.println(LanguageManager.get("command-rc-node-port-invalid", strings[3]));
                        return true;
                    }

                    if (existsNode(strings[2], port)) {
                        System.out.println(LanguageManager.get("command-rc-node-already-exists", strings[2], strings[3]));
                        return true;
                    }

                    NodeExecutor.getInstance().getNodeConfig().getOtherNodes().add(
                            Collections.singletonMap(strings[2], port)
                    );
                    NodeExecutor.getInstance().getNodeConfig().save();
                    System.out.println(LanguageManager.get("command-rc-execute-success"));
                    return true;
                }

                if (strings[1].equalsIgnoreCase("main") && strings.length == 3) {
                    MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[2]);
                    if (mainGroup == null) {
                        ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().createMainGroupAsync(strings[2]);
                        System.out.println(LanguageManager.get("command-rc-execute-success"));
                    } else {
                        System.out.println(LanguageManager.get("command-rc-create-main-group-already-exists", strings[2]));
                    }
                    return true;
                }

                if (strings[1].equalsIgnoreCase("sub")) {
                    if (strings.length == 3) {
                        ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[2]);
                        if (processGroup == null) {
                            ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().createProcessGroupAsync(strings[2]);
                            System.out.println(LanguageManager.get("command-rc-execute-success"));
                        } else {
                            System.out.println(LanguageManager.get("command-rc-create-sub-group-already-exists", strings[2]));
                        }
                        return true;
                    }

                    if (strings.length == 4) {
                        ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[2]);
                        Version version = CommonHelper.findEnumField(Version.class, strings[3]).orNothing();
                        if (version == null) {
                            System.out.println(LanguageManager.get("command-rc-version-not-found", strings[3]));
                            return true;
                        }

                        if (processGroup == null) {
                            ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().createProcessGroupAsync(
                                    strings[2],
                                    Collections.singletonList(new Template(
                                            0,
                                            "default",
                                            false,
                                            FileBackend.NAME,
                                            "#",
                                            new RuntimeConfiguration(
                                                    512,
                                                    new ArrayList<>(),
                                                    new HashMap<>()
                                            ), version
                                    ))
                            );
                            System.out.println(LanguageManager.get("command-rc-execute-success"));
                        } else {
                            System.out.println(LanguageManager.get("command-rc-create-sub-group-already-exists", strings[2]));
                        }
                        return true;
                    }

                    if (strings.length == 5) {
                        ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[2]);
                        Version version = CommonHelper.findEnumField(Version.class, strings[3]).orNothing();
                        if (version == null) {
                            System.out.println(LanguageManager.get("command-rc-version-not-found", strings[3]));
                            return true;
                        }

                        if (processGroup == null) {
                            ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().createProcessGroupAsync(
                                    strings[2],
                                    Collections.singletonList(new Template(
                                            0,
                                            "default",
                                            false,
                                            FileBackend.NAME,
                                            "#",
                                            new RuntimeConfiguration(
                                                    512,
                                                    new ArrayList<>(),
                                                    new HashMap<>()
                                            ), version
                                    ))
                            ).onComplete(e -> {
                                MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[4]);
                                if (mainGroup != null) {
                                    mainGroup.getSubGroups().add(e.getName());
                                    ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateMainGroup(mainGroup);
                                }
                            });
                            System.out.println(LanguageManager.get("command-rc-execute-success"));
                        } else {
                            System.out.println(LanguageManager.get("command-rc-create-sub-group-already-exists", strings[2]));
                        }
                        return true;
                    }

                    if (strings.length == 6) {
                        ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[2]);
                        Version version = CommonHelper.findEnumField(Version.class, strings[3]).orNothing();
                        Boolean staticProcess = CommonHelper.booleanFromString(strings[5]);
                        if (version == null) {
                            System.out.println(LanguageManager.get("command-rc-version-not-found", strings[3]));
                            return true;
                        }

                        if (staticProcess == null) {
                            System.out.println(LanguageManager.get("command-rc-required-boolean", strings[5]));
                            return true;
                        }

                        if (processGroup == null) {
                            ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().createProcessGroupAsync(
                                    strings[2],
                                    Collections.singletonList(new Template(
                                            0,
                                            "default",
                                            false,
                                            FileBackend.NAME,
                                            "#",
                                            new RuntimeConfiguration(
                                                    512,
                                                    new ArrayList<>(),
                                                    new HashMap<>()
                                            ), version
                                    )), new StartupConfiguration(
                                            -1, 1, 1, 41000, StartupEnvironment.JAVA_RUNTIME, true, new ArrayList<>()
                                    ),  new PlayerAccessConfiguration(
                                            "reformcloud.join.full",
                                            true,
                                            "reformcloud.join.maintenance",
                                            false,
                                            null,
                                            true,
                                            true,
                                            true,
                                            50
                                    ), staticProcess
                            ).onComplete(e -> {
                                MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[4]);
                                if (mainGroup != null) {
                                    mainGroup.getSubGroups().add(e.getName());
                                    ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateMainGroup(mainGroup);
                                }
                            });
                            System.out.println(LanguageManager.get("command-rc-execute-success"));
                        } else {
                            System.out.println(LanguageManager.get("command-rc-create-sub-group-already-exists", strings[2]));
                        }
                        return true;
                    }

                    if (strings.length == 7) {
                        ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[2]);
                        Version version = CommonHelper.findEnumField(Version.class, strings[3]).orNothing();
                        Boolean staticProcess = CommonHelper.booleanFromString(strings[5]);
                        Boolean lobby = CommonHelper.booleanFromString(strings[6]);

                        if (version == null) {
                            System.out.println(LanguageManager.get("command-rc-version-not-found", strings[3]));
                            return true;
                        }

                        if (lobby == null) {
                            System.out.println(LanguageManager.get("command-rc-required-boolean", strings[6]));
                            return true;
                        }

                        if (staticProcess == null) {
                            System.out.println(LanguageManager.get("command-rc-required-boolean", strings[5]));
                            return true;
                        }

                        if (processGroup == null) {
                            ProcessGroup processGroup1 = new DefaultProcessGroup(
                                    strings[2],
                                    41000,
                                    version,
                                    512,
                                    false,
                                    50,
                                    staticProcess,
                                    lobby
                            );

                            MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[4]);
                            if (mainGroup != null) {
                                mainGroup.getSubGroups().add(processGroup1.getName());
                                ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateMainGroup(mainGroup);
                            }

                            ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().createProcessGroupAsync(processGroup1);
                            System.out.println(LanguageManager.get("command-rc-execute-success"));
                        } else {
                            System.out.println(LanguageManager.get("command-rc-create-sub-group-already-exists", strings[2]));
                        }
                        return true;
                    }

                    if (strings.length == 8) {
                        ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[2]);
                        Version version = CommonHelper.findEnumField(Version.class, strings[3]).orNothing();
                        Boolean staticProcess = CommonHelper.booleanFromString(strings[5]);
                        Integer min = CommonHelper.fromString(strings[6]);
                        Integer max = CommonHelper.fromString(strings[7]);

                        if (version == null) {
                            System.out.println(LanguageManager.get("command-rc-version-not-found", strings[3]));
                            return true;
                        }

                        if (max == null || max < -1) {
                            System.out.println(LanguageManager.get("command-rc-integer-failed", strings[7]));
                            return true;
                        }

                        if (min == null || min < 0) {
                            System.out.println(LanguageManager.get("command-rc-integer-failed", strings[6]));
                            return true;
                        }

                        if (staticProcess == null) {
                            System.out.println(LanguageManager.get("command-rc-required-boolean", strings[5]));
                            return true;
                        }

                        if (processGroup == null) {
                            ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().createProcessGroupAsync(
                                    strings[2],
                                    Collections.singletonList(new Template(
                                            0,
                                            "default",
                                            false,
                                            FileBackend.NAME,
                                            "#",
                                            new RuntimeConfiguration(
                                                    512,
                                                    new ArrayList<>(),
                                                    new HashMap<>()
                                            ), version
                                    )), new StartupConfiguration(
                                            max, min, 1, 41000, StartupEnvironment.JAVA_RUNTIME, true, new ArrayList<>()
                                    ),  new PlayerAccessConfiguration(
                                            "reformcloud.join.full",
                                            true,
                                            "reformcloud.join.maintenance",
                                            false,
                                            null,
                                            true,
                                            true,
                                            true,
                                            50
                                    ), staticProcess
                            ).onComplete(e -> {
                                MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[4]);
                                if (mainGroup != null) {
                                    mainGroup.getSubGroups().add(e.getName());
                                    ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateMainGroup(mainGroup);
                                }
                            });
                            System.out.println(LanguageManager.get("command-rc-execute-success"));
                        } else {
                            System.out.println(LanguageManager.get("command-rc-create-sub-group-already-exists", strings[2]));
                        }
                        return true;
                    }
                }

                if (strings.length == 9) {
                    ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[2]);
                    Version version = CommonHelper.findEnumField(Version.class, strings[3]).orNothing();
                    Boolean staticProcess = CommonHelper.booleanFromString(strings[5]);
                    Boolean lobby = CommonHelper.booleanFromString(strings[6]);
                    Integer min = CommonHelper.fromString(strings[7]);
                    Integer max = CommonHelper.fromString(strings[8]);

                    if (version == null) {
                        System.out.println(LanguageManager.get("command-rc-version-not-found", strings[3]));
                        return true;
                    }

                    if (max == null || max < -1) {
                        System.out.println(LanguageManager.get("command-rc-integer-failed", strings[8]));
                        return true;
                    }

                    if (min == null || min < 0) {
                        System.out.println(LanguageManager.get("command-rc-integer-failed", strings[7]));
                        return true;
                    }

                    if (staticProcess == null) {
                        System.out.println(LanguageManager.get("command-rc-required-boolean", strings[5]));
                        return true;
                    }

                    if (lobby == null) {
                        System.out.println(LanguageManager.get("command-rc-required-boolean", strings[6]));
                        return true;
                    }

                    if (processGroup == null) {
                        ProcessGroup processGroup1 = new DefaultProcessGroup(
                                strings[2],
                                41000,
                                version,
                                512,
                                false,
                                min,
                                max,
                                staticProcess,
                                lobby
                        );

                        ExecutorAPI.getInstance().getAsyncAPI().getGroupAsyncAPI().createProcessGroupAsync(processGroup1).onComplete(e -> {
                            MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[4]);
                            if (mainGroup != null) {
                                mainGroup.getSubGroups().add(e.getName());
                                ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateMainGroup(mainGroup);
                            }
                        });
                        System.out.println(LanguageManager.get("command-rc-execute-success"));
                    } else {
                        System.out.println(LanguageManager.get("command-rc-create-sub-group-already-exists", strings[2]));
                    }
                    return true;
                }

                break;
            }

            case "list": {
                if (strings.length == 2) {
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(strings[1]).forEach(processInformation -> System.out.println(
                            "  => "
                                    + processInformation.getName()
                                    + "/" + processInformation.getProcessUniqueID()
                                    + " " + processInformation.getOnlineCount() + "/"
                                    + processInformation.getMaxPlayers() + " "
                                    + processInformation.getTemplate().getVersion()
                    ));
                }

                System.out.println(LanguageManager.get("command-rc-execute-success"));
                return true;
            }

            case "listgroups": {
                if (strings.length == 2 && strings[1].equalsIgnoreCase("sub")) {
                    ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups().forEach(processGroup -> System.out.println("  => " +
                            processGroup.getName() +
                            " maintenance: " + processGroup.getPlayerAccessConfiguration().isMaintenance() +
                            " static: " + processGroup.isStaticProcess() +
                            " lobby: " + processGroup.isCanBeUsedAsLobby()
                    ));
                    return true;
                }

                if (strings.length == 2 && strings[1].equalsIgnoreCase("main")) {
                    ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroups().forEach(mainGroup -> System.out.println("  => " + mainGroup.getName() + "/" + mainGroup.getSubGroups()));
                    return true;
                }
                break;
            }

            case "delete": {
                if (strings.length == 3) {
                    if (strings[1].equalsIgnoreCase("sub")) {
                        ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[2]);
                        if (processGroup == null) {
                            System.out.println(LanguageManager.get("command-rc-group-unknown", strings[2]));
                            return true;
                        }

                        NodeExecutor.getInstance().getClusterSyncManager().syncProcessGroupDelete(processGroup.getName());
                        System.out.println(LanguageManager.get("command-rc-execute-success"));
                        return true;
                    }

                    if (strings[1].equalsIgnoreCase("main")) {
                        MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[2]);
                        if (mainGroup == null) {
                            System.out.println(LanguageManager.get("command-rc-main-group-unknown", strings[2]));
                            return true;
                        }

                        NodeExecutor.getInstance().getClusterSyncManager().syncMainGroupDelete(mainGroup.getName());
                        System.out.println(LanguageManager.get("command-rc-execute-success"));
                        return true;
                    }
                }

                break;
            }

            default: {
                sendHelp(commandSource);
                return true;
            }
        }

        sendHelp(commandSource);
        return true;
    }

    @Nonnull
    @Override
    public Collection<String> complete(@Nonnull CommandSource commandSource, @Nonnull String commandLine, @Nonnull String[] currentArg) {
        return StringUtil.completeReformCommand(currentArg, false);
    }

    private void sendHelp(CommandSource commandSource) {
        commandSource.sendMessages(StringUtil.RC_COMMAND_HELP);
    }

    private boolean existsNode(String host, int port) {
        NodeConfig config = NodeExecutor.getInstance().getNodeConfig();
        return Links.filterToReference(config.getOtherNodes(), e -> Links.deepFilterToReference(e,
                g -> g.getKey().equalsIgnoreCase(host) && g.getValue() == port).isPresent()).isPresent();
    }
}
