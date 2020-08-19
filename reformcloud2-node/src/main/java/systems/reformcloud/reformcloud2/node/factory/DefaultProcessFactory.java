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
package systems.reformcloud.reformcloud2.node.factory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.process.NetworkInfo;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.detail.ProcessDetail;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.wrappers.NodeProcessWrapper;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeProcessProvider;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class DefaultProcessFactory implements ProcessFactory {

    public DefaultProcessFactory(DefaultNodeProcessProvider defaultNodeProcessProvider) {
        this.defaultNodeProcessProvider = defaultNodeProcessProvider;
    }

    private final DefaultNodeProcessProvider defaultNodeProcessProvider;

    @Override
    public @NotNull Task<ProcessInformation> buildProcessInformation(@NotNull ProcessFactoryConfiguration configuration) {
        return NodeExecutor.getInstance().getTaskScheduler().queue(() -> {
            Template template = configuration.getTemplate() == null ? this.nextTemplate(configuration.getProcessGroup()) : configuration.getTemplate();
            if (template == null) {
                System.err.println(LanguageManager.get("process-unable-to-find-template", configuration.getProcessGroup().getName()));
                return null;
            }

            NodeInformation nodeInformation = this.getNode(configuration.getNode()).orElseGet(() -> this.getBestNode(configuration.getProcessGroup()));
            if (nodeInformation == null) {
                System.err.println(LanguageManager.get("process-unable-to-find-node", configuration.getProcessGroup().getName()));
                return null;
            }

            int memory = MemoryCalculator.calcMemory(configuration.getProcessGroup().getName(), template);
            int id = this.nextId(configuration.getProcessGroup().getName(), configuration.getId() <= 0 ? 1 : configuration.getId());
            UUID processUniqueId = this.preventCollision(configuration.getProcessUniqueId());

            ProcessInformation processInformation = new ProcessInformation(new ProcessDetail(
                    processUniqueId,
                    nodeInformation.getNodeUniqueID(),
                    nodeInformation.getName(),
                    configuration.getProcessGroup().getName() + template.getServerNameSplitter() + id,
                    configuration.getDisplayName() != null ? configuration.getDisplayName() : configuration.getProcessGroup().getName()
                            + (configuration.getProcessGroup().isShowIdInName() ? template.getServerNameSplitter() + id : ""),
                    id,
                    template,
                    configuration.getMemory() == -1 ? memory : configuration.getMemory(),
                    configuration.getInitialState()
            ), new NetworkInfo(
                    this.nextPort(configuration.getProcessGroup().getStartupConfiguration().getStartPort())
            ), configuration.getProcessGroup(), configuration.getExtra(), configuration.getInclusions());

            if (configuration.getMaxPlayers() >= 0) {
                processInformation.updateMaxPlayers(configuration.getMaxPlayers());
            } else if (configuration.getProcessGroup().getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
                processInformation.updateMaxPlayers(configuration.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers());
            }

            this.defaultNodeProcessProvider.registerProcess(processInformation);
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessRegister(processInformation);

            return processInformation;
        });
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @NotNull
    @Override
    public String getName() {
        return DefaultProcessFactory.class.getName();
    }

    private @NotNull Optional<NodeInformation> getNode(@Nullable String nodeName) {
        if (nodeName == null) {
            return Optional.empty();
        }

        return ExecutorAPI.getInstance().getNodeInformationProvider().getNodeInformation(nodeName).map(NodeProcessWrapper::getNodeInformation);
    }

    private @Nullable NodeInformation getBestNode(@NotNull ProcessGroup processGroup) {
        NodeInformation best = null;

        for (NodeInformation node : ExecutorAPI.getInstance().getNodeInformationProvider().getNodes()) {
            if (!processGroup.getStartupConfiguration().isSearchBestClientAlone()
                    && !processGroup.getStartupConfiguration().getUseOnlyTheseClients().contains(node.getName())) {
                continue;
            }

            if (best == null) {
                best = node;
                continue;
            }

            if (node.getUsedMemory() < best.getUsedMemory()) {
                if (node.getProcessRuntimeInformation().getCpuUsageSystem() == -1 || best.getProcessRuntimeInformation().getCpuUsageSystem() == -1) {
                    best = node;
                    continue;
                }

                if (node.getProcessRuntimeInformation().getCpuUsageSystem() < best.getProcessRuntimeInformation().getCpuUsageSystem()) {
                    best = node;
                }
            }
        }

        return best;
    }

    private int nextId(@NotNull String groupName, int beginId) {
        Collection<Integer> ids = Streams.map(
                ExecutorAPI.getInstance().getProcessProvider().getProcessesByProcessGroup(groupName),
                processInformation -> processInformation.getProcessDetail().getId()
        );

        while (ids.contains(beginId)) {
            beginId++;
        }

        return beginId;
    }

    private int nextPort(int start) {
        Collection<Integer> ports = Streams.map(
                ExecutorAPI.getInstance().getProcessProvider().getProcesses(),
                processInformation -> processInformation.getNetworkInfo().getPort()
        );

        int port = start;
        while (ports.contains(port)) {
            port++;
        }

        return port;
    }

    private @Nullable Template nextTemplate(@NotNull ProcessGroup processGroup) {
        if (processGroup.getTemplates().isEmpty()) {
            return null;
        }

        Template result = null;
        for (Template template : processGroup.getTemplates()) {
            if (template.isGlobal()) {
                continue;
            }

            if (result == null) {
                result = template;
            } else if (result.getPriority() < template.getPriority()) {
                result = template;
            }
        }

        return result;
    }

    private @NotNull UUID preventCollision(@NotNull UUID current) {
        while (ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(current).isPresent()) {
            current = UUID.randomUUID();
        }

        return current;
    }
}
