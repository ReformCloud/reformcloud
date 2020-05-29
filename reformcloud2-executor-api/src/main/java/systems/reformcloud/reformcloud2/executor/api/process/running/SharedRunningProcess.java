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
package systems.reformcloud.reformcloud2.executor.api.process.running;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.io.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.process.running.events.RunningProcessPrepareEvent;
import systems.reformcloud.reformcloud2.executor.api.process.running.events.RunningProcessPreparedEvent;
import systems.reformcloud.reformcloud2.executor.api.process.running.events.RunningProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.process.running.events.RunningProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.process.running.inclusions.InclusionLoader;
import systems.reformcloud.reformcloud2.executor.api.process.running.screen.DefaultRunningProcessScreen;
import systems.reformcloud.reformcloud2.executor.api.process.running.screen.RunningProcessScreen;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.utility.PortUtil;
import systems.reformcloud.reformcloud2.executor.api.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.utility.process.JavaProcessHelper;
import systems.reformcloud.reformcloud2.executor.api.utility.thread.AbsoluteThread;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class SharedRunningProcess implements RunningProcess {

    /**
     * The lib path which points to the root directory of the cloud
     */
    private static final String LIB_PATH = Paths.get("").toAbsolutePath().toString();

    private static final Pattern IP_PATTERN = Pattern.compile(
            "(?<!\\d|\\d\\.)" +
                    "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])" +
                    "(?!\\d|\\.\\d)"
    );
    /**
     * The process information of the current process before it started (will never get updated again)
     */
    protected final ProcessInformation startupInformation;
    /**
     * The current path of the process in which the process is or will run
     */
    protected final Path path;
    /**
     * If the process got started the first time
     */
    private final boolean firstStartup;
    /**
     * The runtime process in which the current process runs
     */
    protected Process process;

    /**
     * The startup time of the process ({@code -1} if it's not started yet)
     */
    protected long startupTime = -1;

    /**
     * The screen of the current running process
     */
    protected RunningProcessScreen runningProcessScreen;
    /**
     * Lock to prevent multiple actions at the same time on the current process
     */
    protected final Lock lock = new ReentrantLock();

    /**
     * Creates a new shared process
     *
     * @param processInformation The process information for the startup process
     */
    public SharedRunningProcess(@NotNull ProcessInformation processInformation) {
        this.startupInformation = processInformation;
        this.runningProcessScreen = new DefaultRunningProcessScreen(this);

        if (processInformation.getProcessGroup().isStaticProcess()) {
            this.path = Paths.get("reformcloud/static/" + processInformation.getProcessDetail().getName());
            this.firstStartup = Files.notExists(this.path);
            IOUtils.createDirectory(Paths.get(this.path + "/plugins"));
        } else {
            this.path = Paths.get("reformcloud/temp/" + processInformation.getProcessDetail().getName()
                    + "-" + processInformation.getProcessDetail().getProcessUniqueID());
            this.firstStartup = Files.notExists(this.path);
            IOUtils.recreateDirectory(this.path);
        }
    }

    @NotNull
    @Override
    public Task<Void> prepare() {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.lock.lock();
            try {
                this.prepare0();
            } finally {
                this.lock.unlock();
            }

            task.complete(null);
        });
        return task;
    }

    private void prepare0() {
        ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessPrepareEvent(this));

        this.startupInformation.getNetworkInfo().setPort(PortUtil.checkPort(
                this.startupInformation.getNetworkInfo().getPort()
        ));

        if (!this.startupInformation.getProcessGroup().isStaticProcess() || this.firstStartup) {
            this.loadTemplateInclusions(Inclusion.InclusionLoadType.PRE);
            this.loadPathInclusions(Inclusion.InclusionLoadType.PRE);

            this.initGlobalTemplateAndCurrentTemplate();
        }

        InclusionLoader.loadInclusions(this.path, this.startupInformation.getPreInclusions());

        if (!this.startupInformation.getProcessGroup().isStaticProcess() || this.firstStartup) {
            this.loadTemplateInclusions(Inclusion.InclusionLoadType.PAST);
            this.loadPathInclusions(Inclusion.InclusionLoadType.PAST);
        }

        this.chooseStartupEnvironmentAndPrepare();

        if (!Files.exists(Paths.get("reformcloud/files/runner.jar"))) {
            DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
        }

        IOUtils.createDirectory(Paths.get(this.path + "/plugins"));
        IOUtils.createDirectory(Paths.get(this.path + "/reformcloud/.connection"));
        new JsonConfiguration().add("key", this.getConnectionKey()).write(this.path + "/reformcloud/.connection/key.json");

        IOUtils.doCopy("reformcloud/files/runner.jar", this.path + "/runner.jar");
        IOUtils.doCopy("reformcloud/.bin/executor.jar", this.path + "/plugins/executor.jar");

        Duo<String, Integer> connectHost = this.getAvailableConnectionHost();
        new JsonConfiguration()
                .add("controller-host", connectHost.getFirst())
                .add("controller-port", connectHost.getSecond())
                .add("startInfo", this.startupInformation)
                .write(this.path + "/reformcloud/.connection/connection.json");

        this.startupInformation.getProcessDetail().setProcessState(ProcessState.PREPARED);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.startupInformation);

        ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessPreparedEvent(this));
    }

    @Override
    public void handleEnqueue() {
        this.lock.lock();

        try {
            this.startupInformation.getProcessDetail().setProcessState(ProcessState.READY_TO_START);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.startupInformation);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean bootstrap() {
        this.lock.lock();

        try {
            return this.bootstrap0();
        } finally {
            this.lock.unlock();
        }
    }

    private boolean bootstrap0() {
        Conditions.isTrue(
                this.startupInformation.getProcessDetail().getProcessState().equals(ProcessState.READY_TO_START),
                "Trying to start a process which is not prepared and ready to start"
        );

        List<String> command = new ArrayList<>(Arrays.asList(
                this.startupInformation.getProcessGroup().getStartupConfiguration().getJvmCommand(),

                "-DIReallyKnowWhatIAmDoingISwear=true",
                "-Djline.terminal=jline.UnsupportedTerminal",
                "-Dreformcloud.runner.version=" + System.getProperty("reformcloud.runner.version"),
                "-Dreformcloud.executor.type=2",
                "-Dreformcloud.lib.path=" + LIB_PATH,
                "-Dreformcloud.process.path=" + new File("reformcloud/files/" + Version.format(
                        this.startupInformation.getProcessDetail().getTemplate().getVersion()
                )).getAbsolutePath()
        ));
        this.startupInformation.getProcessDetail().getTemplate().getRuntimeConfiguration().getSystemProperties().forEach(
                (key, value) -> command.add(String.format("-D%s=%s", key, value))
        );

        command.addAll(this.startupInformation.getProcessDetail().getTemplate().getRuntimeConfiguration().getJvmOptions());
        command.addAll(Arrays.asList(
                "-Xmx" + this.startupInformation.getProcessDetail().getMaxMemory() + "M",
                "-cp", StringUtil.NULL_PATH,
                "-javaagent:runner.jar",
                "systems.reformcloud.reformcloud2.runner.RunnerExecutor"
        ));
        command.addAll(this.startupInformation.getProcessDetail().getTemplate().getRuntimeConfiguration().getProcessParameters());

        if (this.startupInformation.getProcessDetail().getTemplate().getVersion().getId() == 1) {
            command.add("nogui"); // Spigot server
        } else if (this.startupInformation.getProcessDetail().getTemplate().getVersion().getId() == 3) {
            command.add("disable-ansi"); // Nukkit server
        }

        try {
            this.process = new ProcessBuilder()
                    .command(command)
                    .directory(this.path.toFile())
                    .redirectErrorStream(true)
                    .start();
            this.startupTime = System.currentTimeMillis();
        } catch (final IOException ex) {
            ex.printStackTrace();
            return false;
        }

        ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessStartedEvent(this));

        this.startupInformation.getProcessDetail().setProcessState(ProcessState.STARTED);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.startupInformation);

        return true;
    }

    @Override
    public void shutdown() {
        this.lock.lock();

        try {
            this.shutdown0();
        } finally {
            this.lock.unlock();
        }
    }

    private void shutdown0() {
        this.startupTime = -1;

        JavaProcessHelper.shutdown(
                this.process,
                true,
                true,
                TimeUnit.SECONDS.toMillis(5),
                this.getShutdownCommands()
        );

        if (this.startupInformation.getProcessDetail().getTemplate().isAutoReleaseOnClose()) {
            TemplateBackendManager.getOrDefault(this.startupInformation.getProcessDetail().getTemplate().getBackend()).deployTemplate(
                    this.startupInformation.getProcessGroup().getName(),
                    this.startupInformation.getProcessDetail().getTemplate().getName(),
                    this.path,
                    this.startupInformation.getPreInclusions().stream().map(ProcessInclusion::getName).collect(Collectors.toList())
            );
        }

        if (!this.startupInformation.getProcessGroup().isStaticProcess()) {
            IOUtils.deleteDirectory(this.path);
        }

        ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessStoppedEvent(this));
    }

    @Override
    public void copy() {
        this.copy(
                this.startupInformation.getProcessDetail().getTemplate().getName(),
                this.startupInformation.getProcessDetail().getTemplate().getBackend(),
                this.startupInformation.getProcessGroup().getName()
        );
    }

    @Override
    public void copy(@NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        this.lock.lock();

        try {
            this.copy0(targetTemplate, targetTemplateStorage, targetTemplateGroup);
        } finally {
            this.lock.unlock();
        }
    }

    public void copy0(@NotNull String targetTemplate, @NotNull String targetTemplateStorage, @NotNull String targetTemplateGroup) {
        this.sendCommand("save-all");
        AbsoluteThread.sleep(TimeUnit.SECONDS, 1);

        TemplateBackend backend = TemplateBackendManager.getOrDefault(targetTemplateStorage);
        backend.createTemplate(targetTemplateGroup, targetTemplate);

        backend.deployTemplate(
                targetTemplateGroup,
                targetTemplate,
                this.path,
                this.startupInformation.getPreInclusions().stream().map(ProcessInclusion::getName).collect(Collectors.toList())
        );
    }

    @NotNull
    @Override
    public String uploadLog() {
        String lines = this.getLogLines();

        String result = this.uploadLog0("https://paste.reformcloud.systems", lines);
        if (result != null) {
            return result;
        }

        result = this.uploadLog0("https://just-paste.it", lines);
        return result == null ? "Unable to upload log" : result;
    }

    @NotNull
    @Override
    public ReferencedOptional<Process> getProcess() {
        return ReferencedOptional.build(this.process);
    }

    @NotNull
    @Override
    public ProcessInformation getProcessInformation() {
        return this.startupInformation;
    }

    @NotNull
    @Override
    public RunningProcessScreen getProcessScreen() {
        return this.runningProcessScreen;
    }

    @Override
    public void sendCommand(@NotNull String line) {
        if (this.isAlive()) {
            try {
                this.process.getOutputStream().write((line + "\n").getBytes());
                this.process.getOutputStream().flush();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean isAlive() {
        try {
            return this.process != null && this.process.getInputStream().available() != -1 && this.process.isAlive();
        } catch (final IOException ex) {
            return false;
        }
    }

    @Override
    public long getStartupTime() {
        return this.startupTime;
    }

    @NotNull
    @Override
    public Path getPath() {
        return this.path;
    }

    /**
     * Loads all specified template inclusions of the provided type
     *
     * @param loadType The type of inclusion which should get loaded
     */


    @Nullable
    private String uploadLog0(@NotNull String pasteServerUrl, @NotNull String text) {
        text = "Process: " +
                this.getProcessInformation().toString() +
                "\n" +
                "Running on: " +
                this.getProcessInformation().getProcessDetail().getParentName() +
                "/" +
                this.getProcessInformation().getProcessDetail().getParentUniqueID() +
                "\n" +
                "Process started: " + this.getProcess().isPresent() +
                "\n" +
                "Start time: " + (this.getProcess().isEmpty() ? "-1" : CommonHelper.DATE_FORMAT.format(this.getStartupTime())) +
                "\n\n" + text;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(pasteServerUrl + "/documents").openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();

            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(text.getBytes(StandardCharsets.UTF_8));
                stream.flush();
            }

            if (connection.getResponseCode() != 201) {
                return null;
            }

            try (BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String lines = stream.lines().collect(Collectors.joining("\n"));
                return pasteServerUrl + '/' + new JsonConfiguration(lines).getString("key");
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @NotNull
    private String getLogLines() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String cachedLogLine : this.getProcessScreen().getLastLogLines()) {
            Matcher matcher = IP_PATTERN.matcher(cachedLogLine);
            if (matcher.find()) {
                String ip = matcher.group();
                stringBuilder.append(cachedLogLine.replace(ip, "***.***.***.***"));
            } else {
                stringBuilder.append(cachedLogLine);
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RunningProcess)) {
            return false;
        }

        RunningProcess that = (RunningProcess) o;
        return that.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(this.startupInformation.getProcessDetail().getProcessUniqueID());
    }

    @Override
    public int hashCode() {
        return this.getProcessInformation().hashCode();
    }

    /**
     * Chooses the logical startup for the information and creates all files for the start of the
     * process. Also it will rewrite specific configuration files per template version.
     */
    public abstract void chooseStartupEnvironmentAndPrepare();

    /**
     * @return The host and port to which the process will connect
     */
    @NotNull
    public abstract Duo<String, Integer> getAvailableConnectionHost();

    /**
     * @return The connection key for the current process
     */
    @NotNull
    public abstract String getConnectionKey();
}
