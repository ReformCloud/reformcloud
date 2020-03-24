package systems.reformcloud.reformcloud2.executor.api.common.process.running;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPrepareEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessPreparedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.RunningProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.inclusions.InclusionLoader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.PortUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.common.utility.process.JavaProcessHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class SharedRunningProcess implements RunningProcess {

    /**
     * The lib path which points to the root directory of the cloud
     */
    private static final String LIB_PATH = Paths.get("").toAbsolutePath().toString();

    /**
     * Creates a new shared process
     *
     * @param processInformation The process information for the startup process
     */
    public SharedRunningProcess(@Nonnull ProcessInformation processInformation) {
        this.startupInformation = processInformation;

        if (processInformation.getProcessGroup().isStaticProcess()) {
            this.path = Paths.get("reformcloud/static/" + processInformation.getName());
            SystemHelper.createDirectory(Paths.get(path + "/plugins"));
        } else {
            this.path = Paths.get("reformcloud/temp/" + processInformation.getName() + "-" + processInformation.getProcessUniqueID());
            SystemHelper.recreateDirectory(path);
        }
    }

    /**
     * The process information of the current process before it started (will never get updated again)
     */
    protected final ProcessInformation startupInformation;

    /**
     * The current path of the process in which the process is or will run
     */
    protected final Path path;

    /**
     * The runtime process in which the current process runs
     */
    protected Process process;

    /**
     * The startup time of the process ({@code -1} if it's not started yet)
     */
    protected long startupTime = -1;

    @Nonnull
    @Override
    public Task<Void> prepare() {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessPrepareEvent(this));

            this.startupInformation.getNetworkInfo().setPort(PortUtil.checkPort(
                    this.startupInformation.getNetworkInfo().getPort()
            ));

            this.chooseStartupEnvironmentAndPrepare();

            if (!Files.exists(Paths.get("reformcloud/files/runner.jar"))) {
                DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
            }

            InclusionLoader.loadInclusions(this.path, this.startupInformation.getPreInclusions());

            SystemHelper.createDirectory(Paths.get(path + "/plugins"));
            SystemHelper.createDirectory(Paths.get(path + "/reformcloud/.connection"));
            new JsonConfiguration().add("key", this.getConnectionKey()).write(path + "/reformcloud/.connection/key.json");

            SystemHelper.doCopy("reformcloud/files/runner.jar", path + "/runner.jar");
            SystemHelper.doCopy("reformcloud/.bin/executor.jar", path + "/plugins/executor.jar");

            Duo<String, Integer> connectHost = this.getAvailableConnectionHost();
            new JsonConfiguration()
                    .add("controller-host", connectHost.getFirst())
                    .add("controller-port", connectHost.getSecond())
                    .add("startInfo", this.startupInformation)
                    .write(path + "/reformcloud/.connection/connection.json");

            this.loadTemplateInclusions(Inclusion.InclusionLoadType.PRE);
            this.loadPathInclusions(Inclusion.InclusionLoadType.PRE);

            this.initGlobalTemplateAndCurrentTemplate();

            this.loadTemplateInclusions(Inclusion.InclusionLoadType.PAST);
            this.loadPathInclusions(Inclusion.InclusionLoadType.PAST);

            this.startupInformation.setProcessState(ProcessState.PREPARED);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.startupInformation);

            ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessPreparedEvent(this));
            task.complete(null);
        });
        return task;
    }

    @Override
    public void handleEnqueue() {
        this.startupInformation.setProcessState(ProcessState.READY_TO_START);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.startupInformation);
    }

    @Override
    public boolean bootstrap() {
        Conditions.isTrue(
                this.startupInformation.getProcessState().equals(ProcessState.READY_TO_START),
                "Trying to start a process which is not prepared and ready to start"
        );

        Collection<String> command = new ArrayList<>(Arrays.asList(
                this.startupInformation.getProcessGroup().getStartupConfiguration().getStartupEnvironment().getCommand(),
                "-XX:+UseG1GC",
                "-XX:MaxGCPauseMillis=50",
                "-XX:-UseAdaptiveSizePolicy",
                "-XX:CompileThreshold=100",
                "-Dcom.mojang.eula.agree=true",
                "-DIReallyKnowWhatIAmDoingISwear=true",
                "-Djline.terminal=jline.UnsupportedTerminal",

                "-Dreformcloud.executor.type=3",
                "-Dreformcloud.lib.path=" + LIB_PATH,
                "-Dreformcloud.process.path=" + new File("reformcloud/files/" + Version.format(
                        this.startupInformation.getTemplate().getVersion()
                )).getAbsolutePath(),

                "-Xmx" + this.startupInformation.getMaxMemory() + "M"
        ));

        command.addAll(this.startupInformation.getTemplate().getRuntimeConfiguration().getJvmOptions());
        this.startupInformation.getTemplate().getRuntimeConfiguration().getSystemProperties().forEach(
                (key, value) -> command.add(String.format("-D%s=%s", key, value))
        );
        command.addAll(this.startupInformation.getTemplate().getRuntimeConfiguration().getProcessParameters());

        command.addAll(Arrays.asList(
                "-cp", StringUtil.NULL_PATH,
                "-javaagent:runner.jar",
                "systems.reformcloud.reformcloud2.runner.Runner"
        ));

        if (this.startupInformation.getTemplate().getVersion().getId() == 1) {
            command.add("nogui"); // Spigot server
        } else if (this.startupInformation.getTemplate().getVersion().getId() == 3) {
            command.add("disable-ansi"); // Nukkit server
        }

        try {
            this.process = new ProcessBuilder(command.toArray(new String[0]))
                    .directory(path.toFile())
                    .redirectErrorStream(true)
                    .start();
            this.startupTime = System.currentTimeMillis();
        } catch (final IOException ex) {
            ex.printStackTrace();
            return false;
        }

        ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessStartedEvent(this));

        this.startupInformation.setProcessState(ProcessState.STARTED);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.startupInformation);

        return true;
    }

    @Override
    public void shutdown() {
        this.startupTime = -1;
        ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessStoppedEvent(this));

        JavaProcessHelper.shutdown(
                this.process,
                true,
                true,
                TimeUnit.SECONDS.toMillis(5),
                this.getShutdownCommands()
        );

        if (this.startupInformation.getTemplate().isAutoReleaseOnClose()) {
            TemplateBackendManager.getOrDefault(this.startupInformation.getTemplate().getBackend()).deployTemplate(
                    this.startupInformation.getProcessGroup().getName(),
                    this.startupInformation.getTemplate().getName(),
                    this.path
            );
        }

        if (!this.startupInformation.getProcessGroup().isStaticProcess()) {
            SystemHelper.deleteDirectory(this.path);
        }
    }

    @Override
    public void copy() {
        this.sendCommand("save-all");
        AbsoluteThread.sleep(TimeUnit.SECONDS, 1);
        TemplateBackendManager.getOrDefault(this.startupInformation.getTemplate().getBackend()).deployTemplate(
                this.startupInformation.getProcessGroup().getName(),
                this.startupInformation.getTemplate().getName(),
                this.path
        );
    }

    @Nonnull
    @Override
    public ReferencedOptional<Process> getProcess() {
        return ReferencedOptional.build(this.process);
    }

    @Nonnull
    @Override
    public ProcessInformation getProcessInformation() {
        return this.startupInformation;
    }

    @Override
    public void sendCommand(@Nonnull String line) {
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
            return process != null && process.getInputStream().available() != -1 && process.isAlive();
        } catch (final IOException ex) {
            return false;
        }
    }

    @Override
    public long getStartupTime() {
        return this.startupTime;
    }

    @Nonnull
    @Override
    public Path getPath() {
        return this.path;
    }

    /**
     * Loads all specified template inclusions of the provided type
     *
     * @param loadType The type of inclusion which should get loaded
     */
    protected void loadTemplateInclusions(@Nonnull Inclusion.InclusionLoadType loadType) {
        this.startupInformation.getTemplate().getTemplateInclusionsOfType(loadType).forEach(e -> {
            String[] splitTemplate = e.getFirst().split("/");
            if (splitTemplate.length != 2) {
                return;
            }

            TemplateBackendManager.getOrDefault(e.getSecond()).loadTemplate(
                    splitTemplate[0],
                    splitTemplate[1],
                    this.path
            ).awaitUninterruptedly();
        });
    }

    /**
     * Loads all specified path inclusions of the provided type
     *
     * @param loadType The type of inclusion which should get loaded
     */
    protected void loadPathInclusions(@Nonnull Inclusion.InclusionLoadType loadType) {
        this.startupInformation.getTemplate().getPathInclusionsOfType(loadType).forEach(e -> {
            String[] splitTemplate = e.getFirst().split("/");
            if (splitTemplate.length != 2) {
                return;
            }

            TemplateBackendManager.getOrDefault(e.getSecond()).loadPath(
                    e.getFirst(),
                    this.path
            ).awaitUninterruptedly();
        });
    }

    /**
     * Loads all global templates of the current group first and then the current startup template
     */
    private void initGlobalTemplateAndCurrentTemplate() {
        TemplateBackendManager.getOrDefault(this.startupInformation.getTemplate().getBackend()).loadGlobalTemplates(
                this.startupInformation.getProcessGroup(),
                this.path
        ).awaitUninterruptedly();

        TemplateBackendManager.getOrDefault(this.startupInformation.getTemplate().getBackend()).loadTemplate(
                this.startupInformation.getProcessGroup().getName(),
                this.startupInformation.getTemplate().getName(),
                this.path
        ).awaitUninterruptedly();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RunningProcess)) {
            return false;
        }

        RunningProcess that = (RunningProcess) o;
        return that.getProcessInformation().getProcessUniqueID().equals(this.startupInformation.getProcessUniqueID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(startupInformation, path, getProcess(), getStartupTime());
    }

    /**
     * Chooses the logical startup for the information and creates all files for the start of the
     * process. Also it will rewrite specific configuration files per template version.
     */
    public abstract void chooseStartupEnvironmentAndPrepare();

    /**
     * @return The host and port to which the process will connect
     */
    @Nonnull
    public abstract Duo<String, Integer> getAvailableConnectionHost();

    /**
     * @return The connection key for the current process
     */
    @Nonnull
    public abstract String getConnectionKey();
}
