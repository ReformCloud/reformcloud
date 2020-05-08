/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.controller.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.groups.setup.GroupSetupHelper;
import systems.reformcloud.reformcloud2.executor.api.common.groups.setup.GroupSetupVersion;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.Setup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetupQuestion;
import systems.reformcloud.reformcloud2.executor.api.common.registry.Registry;
import systems.reformcloud.reformcloud2.executor.api.common.registry.basic.RegistryBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams.newCollection;
import static systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper.createDirectory;

public final class ControllerExecutorConfig {

    private static final Collection<Path> PATHS = newCollection(
            s -> Paths.get(s),
            "reformcloud/groups/main",
            "reformcloud/groups/sub",
            "reformcloud/configs",
            "reformcloud/applications"
    );

    private final Setup setup = new DefaultSetup();

    private final ControllerConfig controllerConfig;

    private final List<MainGroup> mainGroups = new ArrayList<>();

    private final List<ProcessGroup> processGroups = new ArrayList<>();

    private final Registry subGroupRegistry;

    private final Registry mainGroupRegistry;

    private final String connectionKey;

    private final IngameMessages ingameMessages;

    private final AtomicBoolean firstStartup = new AtomicBoolean(false);

    public ControllerExecutorConfig() {
        createDirectories();
        this.subGroupRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/sub"));
        this.mainGroupRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/main"));

        if (!Files.exists(ControllerConfig.PATH)) {
            this.firstStartup.set(true);
            firstSetup();
        }

        loadGroups();
        this.controllerConfig = load();
        this.connectionKey = connectionKey();
        this.ingameMessages = JsonConfiguration.read("reformcloud/configs/messages.json").get("messages", IngameMessages.TYPE);
    }

    private ControllerConfig load() {
        return JsonConfiguration.read(ControllerConfig.PATH).get("config", ControllerConfig.TYPE);
    }

    private String connectionKey() {
        return JsonConfiguration.read("reformcloud/.bin/connection.json").getString("key");
    }

    private void loadGroups() {
        processGroups.addAll(this.subGroupRegistry.readKeys(e -> e.get("key", ProcessGroup.TYPE)));
        mainGroups.addAll(this.mainGroupRegistry.readKeys(e -> e.get("key", MainGroup.TYPE)));
    }

    private void createDirectories() {
        PATHS.forEach(path -> {
            if (!Files.exists(path)) {
                createDirectory(path);
            }
        });
    }

    private void firstSetup() {
        new JsonConfiguration().add("key", StringUtil.generateString(1)).write(Paths.get(
                "reformcloud/.bin/connection.json"
        ));
        new JsonConfiguration().add("messages", new IngameMessages()).write(Paths.get("reformcloud/configs/messages.json"));

        setup.addQuestion(new DefaultSetupQuestion(
                LanguageManager.get("controller-setup-question-controller-address"),
                LanguageManager.get("controller-setup-question-controller-address-wrong"),
                s -> CommonHelper.getIpAddress(s.trim()) != null,
                s -> {
                    String ip = CommonHelper.getIpAddress(s.trim());

                    new JsonConfiguration().add("config", new ControllerConfig(
                            -1,
                            Collections.singletonList(Collections.singletonMap(ip, 2008)),
                            Collections.singletonList(Collections.singletonMap(ip, 1809))
                    )).write(ControllerConfig.PATH);
                }
        )).startSetup(ControllerExecutor.getInstance().getLoggerBase());

        System.out.println(LanguageManager.get("general-setup-choose-default-installation"));
        GroupSetupHelper.printAvailable();

        String result = ControllerExecutor.getInstance().getLoggerBase().readLineNoPrompt();
        while (!result.trim().isEmpty()) {
            GroupSetupVersion version = GroupSetupHelper.findByName(result);
            if (version == null) {
                System.out.println(LanguageManager.get("general-setup-choose-default-installation-wrong"));
                result = ControllerExecutor.getInstance().getLoggerBase().readLineNoPrompt();
                continue;
            }

            version.install(e -> subGroupRegistry.createKey(e.getName(), e), e -> mainGroupRegistry.createKey(e.getName(), e));
            System.out.println(LanguageManager.get("general-setup-default-installation-done", version.getName()));
            break;
        }
    }

    @NotNull
    public MainGroup createMainGroup(MainGroup mainGroup) {
        MainGroup mainGroup1 = mainGroups.stream().filter(group -> mainGroup.getName().equals(group.getName())).findFirst().orElse(null);
        if (mainGroup1 == null) {
            this.mainGroups.add(mainGroup);
            return this.mainGroupRegistry.createKey(mainGroup.getName(), mainGroup);
        }

        return mainGroup;
    }

    @NotNull
    public ProcessGroup createProcessGroup(ProcessGroup processGroup) {
        ProcessGroup processGroup1 = processGroups.stream().filter(group -> processGroup.getName().equals(group.getName())).findFirst().orElse(null);
        if (processGroup1 == null) {
            this.processGroups.add(processGroup);
            ControllerExecutor.getInstance().getAutoStartupHandler().update();
            return this.subGroupRegistry.createKey(processGroup.getName(), processGroup);
        }

        return processGroup;
    }

    public void deleteMainGroup(MainGroup mainGroup) {
        mainGroups.remove(mainGroup);
        this.mainGroupRegistry.deleteKey(mainGroup.getName());
    }

    public void deleteProcessGroup(ProcessGroup processGroup) {
        this.subGroupRegistry.deleteKey(processGroup.getName());
        processGroups.remove(processGroup);
        ControllerExecutor.getInstance().getAutoStartupHandler().update();

        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(processInformation -> {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(processInformation.getProcessDetail().getProcessUniqueID());
            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 10);
        });
    }

    public void updateProcessGroup(ProcessGroup processGroup) {
        Streams.filterToReference(processGroups, group -> processGroup.getName().equals(group.getName())).ifPresent(group -> {
            processGroups.remove(group);
            processGroups.add(processGroup);
            this.subGroupRegistry.updateKey(processGroup.getName(), processGroup);
            ControllerExecutor.getInstance().getAutoStartupHandler().update();
        });

        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(processInformation -> {
            processInformation.setProcessGroup(processGroup);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);
        });
    }

    public void updateMainGroup(MainGroup mainGroup) {
        Streams.filterToReference(mainGroups, group -> group.getName().equals(mainGroup.getName())).ifPresent(group -> {
            mainGroups.remove(group);
            mainGroups.add(mainGroup);
            this.mainGroupRegistry.updateKey(mainGroup.getName(), mainGroup);
        });
    }

    public ControllerConfig getControllerConfig() {
        return controllerConfig;
    }

    public List<MainGroup> getMainGroups() {
        return new ArrayList<>(mainGroups);
    }

    public List<ProcessGroup> getProcessGroups() {
        return new ArrayList<>(processGroups);
    }

    public String getConnectionKey() {
        return connectionKey;
    }

    public IngameMessages getIngameMessages() {
        return ingameMessages;
    }

    public final boolean isFirstStartup() {
        return firstStartup.get();
    }
}
