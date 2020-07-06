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
package systems.reformcloud.reformcloud2.node.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.network.packet.query.QueryManager;
import systems.reformcloud.reformcloud2.executor.api.process.Player;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.utility.process.JavaProcessHelper;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.node.event.process.LocalProcessPrePrepareEvent;
import systems.reformcloud.reformcloud2.node.process.screen.ProcessScreen;
import systems.reformcloud.reformcloud2.node.process.screen.ProcessScreenController;
import systems.reformcloud.reformcloud2.protocol.api.NodeToApiRequestProcessInformationUpdate;
import systems.reformcloud.reformcloud2.protocol.api.NodeToApiRequestProcessInformationUpdateResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class DefaultNodeLocalProcessWrapper extends DefaultNodeRemoteProcessWrapper {

    private static final String LIB_PATH = Paths.get("").toAbsolutePath().toString();
    private static final String[] DEFAULT_SHUTDOWN_COMMANDS = new String[]{"end", "stop"};

    public DefaultNodeLocalProcessWrapper(ProcessInformation processInformation) {
        super(processInformation);

        this.path = processInformation.getProcessGroup().isStaticProcess()
                ? Paths.get("reformcloud/static", processInformation.getProcessDetail().getName())
                : Paths.get("reformcloud/temp", processInformation.getProcessDetail().getName() + "-" + processInformation.getProcessDetail().getProcessUniqueID());
        this.firstStart = Files.notExists(this.path);
        this.processScreen = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProcessScreenController.class).createScreen(this);

        IOUtils.createDirectory(this.path);

        processInformation.getNetworkInfo().setHost(NodeExecutor.getInstance().getNodeConfig().getStartHost());
        this.prepare();
        this.setRuntimeState(ProcessState.PREPARED);
    }

    private final Lock lock = new ReentrantLock();
    private final String connectionKey = StringUtil.generateString(16);
    private final ProcessScreen processScreen;
    private final Path path;
    private final boolean firstStart;

    private ProcessState runtimeState;
    private Process process;

    @NotNull
    @Override
    public Optional<ProcessInformation> requestProcessInformationUpdate() {
        NetworkChannel channel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
                .getChannel(this.processInformation.getProcessDetail().getName())
                .orElse(null);
        if (channel == null) {
            return Optional.empty();
        }

        Packet result = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(QueryManager.class)
                .sendPacketQuery(channel, new NodeToApiRequestProcessInformationUpdate())
                .getUninterruptedly(TimeUnit.SECONDS, 5);
        if (!(result instanceof NodeToApiRequestProcessInformationUpdateResult)) {
            return Optional.empty();
        }

        return Optional.ofNullable(((NodeToApiRequestProcessInformationUpdateResult) result).getProcessInformation());
    }

    @NotNull
    @Override
    public Optional<String> uploadLog() {
        return ProcessUtil.uploadLog(this.getLastLogLines());
    }

    @NotNull
    @Override
    public @UnmodifiableView Queue<String> getLastLogLines() {
        return this.processScreen.getCachedLogLines();
    }

    @Override
    public void sendCommand(@NotNull String commandLine) {
        if (this.isAlive()) {
            try {
                this.process.getOutputStream().write((commandLine + "\n").getBytes());
                this.process.getOutputStream().flush();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void setRuntimeState(@NotNull ProcessState state) {
        if (state.isRuntimeState() && this.runtimeState != state) {
            if (this.callRuntimeStateUpdate(state)) {
                this.runtimeState = state;
            } else {
                return;
            }
        }

        this.processInformation.getProcessDetail().setProcessState(state);
        ExecutorAPI.getInstance().getProcessProvider().updateProcessInformation(this.processInformation);
    }

    @Override
    public void copy(@NotNull String templateGroup, @NotNull String templateName, @NotNull String templateBackend) {
        TemplateBackendManager.get(templateBackend).ifPresent(
                backend -> backend.deployTemplate(templateGroup, templateName, this.path)
        );
    }

    private boolean callRuntimeStateUpdate(@NotNull ProcessState runtimeState) {
        try {
            this.lock.lock();

            switch (runtimeState) {
                case STARTED:
                    return this.start();
                case RESTARTING:
                    this.restart();
                    break;
                case PAUSED:
                    this.stop(false);
                    break;
                case STOPPED:
                    this.stop(true);
                    return false;
            }
        } finally {
            this.lock.unlock();
        }

        return true;
    }

    private void prepare() {
        try {
            this.lock.lock();

            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new LocalProcessPrePrepareEvent(this.processInformation));
            EnvironmentBuilder.constructEnvFor(this, this.firstStart, this.connectionKey);
        } finally {
            this.lock.unlock();
        }
    }

    private boolean start() {
        if (!NodeExecutor.getInstance().canStartProcesses(this.processInformation.getProcessDetail().getMaxMemory())) {
            NodeExecutor.getInstance().getTaskScheduler().queue(() -> this.setRuntimeState(ProcessState.STARTED), 20 * 5);
            return false;
        }

        NodeExecutor.getInstance().getCurrentNodeInformation().addUsedMemory(this.processInformation.getProcessDetail().getMaxMemory());
        List<String> command = new ArrayList<>(Arrays.asList(
                this.processInformation.getProcessGroup().getStartupConfiguration().getJvmCommand(),

                "-DIReallyKnowWhatIAmDoingISwear=true",
                "-Djline.terminal=jline.UnsupportedTerminal",
                "-Dreformcloud.runner.version=" + System.getProperty("reformcloud.runner.version"),
                "-Dreformcloud.executor.type=3",
                "-Dreformcloud.lib.path=" + LIB_PATH,
                "-Dreformcloud.process.path=" + new File("reformcloud/files/" + Version.format(
                        this.processInformation.getProcessDetail().getTemplate().getVersion()
                )).getAbsolutePath()
        ));
        this.processInformation.getProcessDetail().getTemplate().getRuntimeConfiguration().getSystemProperties().forEach(
                (key, value) -> command.add(String.format("-D%s=%s", key, value))
        );

        command.addAll(this.processInformation.getProcessDetail().getTemplate().getRuntimeConfiguration().getJvmOptions());
        command.addAll(Arrays.asList(
                "-Xmx" + this.processInformation.getProcessDetail().getMaxMemory() + "M",
                "-cp", StringUtil.NULL_PATH,
                "-javaagent:runner.jar",
                "systems.reformcloud.reformcloud2.runner.RunnerExecutor"
        ));
        command.addAll(this.processInformation.getProcessDetail().getTemplate().getRuntimeConfiguration().getProcessParameters());

        if (this.processInformation.getProcessDetail().getTemplate().getVersion().getId() == 1) {
            command.add("nogui"); // Spigot server
        } else if (this.processInformation.getProcessDetail().getTemplate().getVersion().getId() == 3) {
            command.add("disable-ansi"); // Nukkit server
        }

        try {
            this.process = new ProcessBuilder()
                    .command(command)
                    .directory(this.path.toFile())
                    .redirectErrorStream(true)
                    .start();
        } catch (Throwable throwable) {
            if (throwable instanceof IOException) { // low level - but the best way :(
                NodeExecutor.getInstance().getTaskScheduler().queue(() -> this.setRuntimeState(ProcessState.STARTED), 20 * 5);
                return false;
            }

            throwable.printStackTrace();
            return false;
        }

        return true;
    }

    private void restart() {
        this.stop(false);
        this.start();
    }

    private void stop(boolean finalStop) {
        if (this.isStarted()) {
            JavaProcessHelper.shutdown(this.process, true, true, TimeUnit.SECONDS.toMillis(15), this.getShutdownCommands());
        }

        this.process = null;

        if (finalStop) {
            if (this.processInformation.getProcessDetail().getTemplate().isAutoReleaseOnClose()) {
                TemplateBackendManager.getOrDefault(this.processInformation.getProcessDetail().getTemplate().getBackend()).deployTemplate(
                        this.processInformation.getProcessGroup().getName(),
                        this.processInformation.getProcessDetail().getTemplate().getName(),
                        this.path,
                        this.processInformation.getPreInclusions().stream().map(ProcessInclusion::getName).collect(Collectors.toList())
                );
            }

            if (!this.processInformation.getProcessGroup().isStaticProcess()) {
                IOUtils.deleteDirectory(this.path);
            }

            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessUnregister(this.processInformation);
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).handleProcessUnregister(
                    this.processInformation.getProcessDetail().getName()
            );
        } else {
            this.processInformation.getNetworkInfo().setConnected(false);
            for (Player onlinePlayer : this.processInformation.getProcessPlayerManager().getOnlinePlayers()) {
                this.processInformation.getProcessPlayerManager().onLogout(onlinePlayer.getUniqueID());
            }

            this.processInformation.getProcessDetail().setProcessRuntimeInformation(ProcessRuntimeInformation.empty());
            ExecutorAPI.getInstance().getProcessProvider().updateProcessInformation(this.processInformation);
        }

        NodeExecutor.getInstance().getCurrentNodeInformation().removeUsedMemory(this.processInformation.getProcessDetail().getMaxMemory());
    }

    private @NotNull String[] getShutdownCommands() {
        String[] shutdownCommands = this.processInformation.getProcessDetail().getTemplate().getRuntimeConfiguration().getShutdownCommands().toArray(new String[0]);
        return Streams.concat(shutdownCommands, DEFAULT_SHUTDOWN_COMMANDS);
    }

    Path getPath() {
        return this.path;
    }

    public String getConnectionKey() {
        return this.connectionKey;
    }

    public boolean isAlive() {
        return this.process != null && this.process.isAlive();
    }

    public boolean isStarted() {
        return this.process != null;
    }

    public @NotNull Optional<Process> getProcess() {
        return Optional.ofNullable(this.process);
    }
}
