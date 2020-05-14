package systems.reformcloud.reformcloud2.executor.node.process.basic;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.Null;
import systems.reformcloud.reformcloud2.executor.api.common.utility.PortUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.common.utility.process.JavaProcessHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutProcessPrepared;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreen;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreenHandler;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class BasicLocalNodeProcess implements LocalNodeProcess {

    private static final String LIB_PATH = new File(".").getAbsolutePath();
    private final ProcessInformation processInformation;
    private final AtomicLong startupTime = new AtomicLong(-1);
    private Path path;

    private boolean prepared = false;

    private Process process;

    public BasicLocalNodeProcess(ProcessInformation processInformation) {
        this.processInformation = processInformation;

        if (processInformation.getProcessGroup().isStaticProcess()) {
            this.path = Paths.get("reformcloud/static/" + processInformation.getName());
            SystemHelper.createDirectory(Paths.get(path + "/plugins"));
        } else {
            this.path = Paths.get("reformcloud/temp/" + processInformation.getName() + "-" + processInformation.getProcessUniqueID());
            SystemHelper.recreateDirectory(path);
        }
    }

    // ========================= //
    //Static
    private static void rewriteFile(File file, Function<String, String> function) {
        try {
            List<String> list = Files.readAllLines(file.toPath());
            List<String> newLine = new ArrayList<>();

            list.forEach(s -> newLine.add(function.apply(s)));

            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8), true)) {
                newLine.forEach(s -> {
                    printWriter.write(s + "\n");
                    printWriter.flush();
                });
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void updateCommandLine(List<String> command, Version version) {
        if (version.getId() == 1) {
            command.add("nogui");
        } else if (version.getId() == 3) {
            command.add("disable-ansi");
        }
    }

    @Override
    public long getStartupTime() {
        return startupTime.get();
    }

    @Override
    public void prepare() {
        processInformation.setProcessState(ProcessState.PREPARED);

        int port = PortUtil.checkPort(processInformation.getNetworkInfo().getPort());
        processInformation.getNetworkInfo().setPort(port);
        chooseLogicallyStartup();

        if (!Files.exists(Paths.get("reformcloud/files/runner.jar"))) {
            DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");
        }

        SystemHelper.doCopy("reformcloud/files/runner.jar", path + "/runner.jar");
        SystemHelper.doCopy("reformcloud/.bin/executor.jar", path + "/plugins/executor.jar");

        Double<String, Integer> connection = NodeExecutor.getInstance().getConnectHost();
        new JsonConfiguration()
                .add("controller-host", connection.getFirst())
                .add("controller-port", connection.getSecond())
                .add("startInfo", processInformation)
                .write(path + "/reformcloud/.connection/connection.json");

        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);
        prepared = true;

        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new NodePacketOutProcessPrepared(
                processInformation.getName(),
                processInformation.getProcessUniqueID(),
                processInformation.getTemplate().getName()
        ));
        LocalProcessManager.registerLocalProcess(this);
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().registerLocalProcess(this);
    }

    @Override
    public boolean bootstrap() {
        if (!prepared) {
            return false;
        }

        List<String> command = new ArrayList<>(Arrays.asList(
                processInformation.getProcessGroup().getStartupConfiguration().getStartupEnvironment().getCommand(),
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
                        this.processInformation.getTemplate().getVersion()
                )).getAbsolutePath(),

                "-Xmx" + processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory() + "M"
        ));

        command.addAll(this.processInformation.getTemplate().getRuntimeConfiguration().getJvmOptions());
        this.processInformation.getTemplate().getRuntimeConfiguration().getSystemProperties().forEach((s, s2) -> command.add("-D" + s + "=" + s2));

        command.addAll(this.processInformation.getTemplate().getRuntimeConfiguration().getProcessParameters());
        command.addAll(Arrays.asList(
                "-cp", Null.devNull(),
                "-javaagent:runner.jar",
                "systems.reformcloud.reformcloud2.runner.Runner"
        ));
        updateCommandLine(command, processInformation.getTemplate().getVersion());

        try {
            this.process = new ProcessBuilder(command)
                    .directory(path.toFile())
                    .redirectErrorStream(true)
                    .start();
        } catch (final IOException ex) {
            ex.printStackTrace();
            return false;
        }

        processInformation.setProcessState(ProcessState.STARTED);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(processInformation);

        NodeExecutor.getInstance().getClusterSyncManager().syncProcessStartup(processInformation);
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleLocalProcessStart(processInformation);
        startupTime.set(System.currentTimeMillis());

        NodeProcessScreenHandler.registerScreen(new NodeProcessScreen(this.processInformation.getProcessUniqueID()));

        System.out.println(LanguageManager.get("client-process-start-done", processInformation.getName()));
        return true;
    }

    @Override
    public void shutdown() {
        startupTime.set(-1);
        JavaProcessHelper.shutdown(process, true, true, TimeUnit.SECONDS.toMillis(10), getShutdownCommands());

        NodeProcessScreenHandler.unregisterScreen(this.processInformation.getProcessUniqueID());
        NodeExecutor.getInstance().getClusterSyncManager().syncProcessStop(processInformation);
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().unregisterLocalProcess(processInformation.getProcessUniqueID());
        NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleLocalProcessStop(processInformation);
        LocalProcessManager.unregisterProcesses(processInformation.getProcessUniqueID());

        if (!processInformation.getProcessGroup().isStaticProcess()) {
            SystemHelper.deleteDirectory(path);
        }
    }

    @Override
    public void sendCommand(@Nonnull String line) {
        if (running()) {
            try {
                process.getOutputStream().write((line + "\n").getBytes());
                process.getOutputStream().flush();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean running() {
        try {
            return process != null && process.getInputStream().available() != -1 && process.isAlive();
        } catch (final IOException ex) {
            return false;
        }
    }

    @Override
    public void copy() {
        sendCommand("save-all");
        AbsoluteThread.sleep(TimeUnit.SECONDS, 1);
        TemplateBackendManager.getOrDefault(this.processInformation.getTemplate().getBackend()).deployTemplate(
                this.processInformation.getProcessGroup().getName(),
                this.processInformation.getTemplate().getName(),
                this.path
        );
    }

    @Override
    public CompletableFuture<Void> initTemplate() {
        if (!processInformation.getProcessGroup().isStaticProcess()) {
            try {
                TemplateBackendManager.getOrDefault(this.processInformation.getTemplate().getBackend()).loadGlobalTemplates(
                        this.processInformation.getProcessGroup(),
                        this.path
                ).get(15, TimeUnit.SECONDS);
            } catch (final TimeoutException | InterruptedException | ExecutionException ex) {
                System.err.println("Load of global templates took too long");
            }

            processInformation.getTemplate().getTemplateInclusionsOfType(Inclusion.InclusionLoadType.PRE).forEach(e -> {
                String[] splitTemplate = e.getFirst().split("/");
                if (splitTemplate.length != 2) {
                    return;
                }

                TemplateBackendManager.getOrDefault(e.getSecond()).loadTemplate(
                        splitTemplate[0],
                        splitTemplate[1],
                        this.path
                ).getUninterruptedly(TimeUnit.SECONDS, 10);
            });

            processInformation.getTemplate().getPathInclusionsOfType(Inclusion.InclusionLoadType.PRE).forEach(e -> TemplateBackendManager.getOrDefault(e.getSecond()).loadPath(
                    e.getFirst(),
                    this.path
            ).getUninterruptedly(TimeUnit.SECONDS, 10));

            TemplateBackendManager.getOrDefault(this.processInformation.getTemplate().getBackend()).loadTemplate(
                    this.processInformation.getProcessGroup().getName(),
                    this.processInformation.getTemplate().getName(),
                    this.path
            ).getUninterruptedly();

            processInformation.getTemplate().getTemplateInclusionsOfType(Inclusion.InclusionLoadType.PAST).forEach(e -> {
                String[] splitTemplate = e.getFirst().split("/");
                if (splitTemplate.length != 2) {
                    return;
                }

                TemplateBackendManager.getOrDefault(e.getSecond()).loadTemplate(
                        splitTemplate[0],
                        splitTemplate[1],
                        this.path
                ).getUninterruptedly(TimeUnit.SECONDS, 10);
            });

            processInformation.getTemplate().getPathInclusionsOfType(Inclusion.InclusionLoadType.PAST).forEach(e -> TemplateBackendManager.getOrDefault(e.getSecond()).loadPath(
                    e.getFirst(),
                    this.path
            ).getUninterruptedly(TimeUnit.SECONDS, 10));
        }

        return CompletableFuture.completedFuture(null);
    }

    /* ================================= */

    @Nonnull
    @Override
    public ReferencedOptional<Process> getProcess() {
        return ReferencedOptional.build(process);
    }

    @Nonnull
    @Override
    public ProcessInformation getProcessInformation() {
        return processInformation;
    }

    private void createEula() {
        try (InputStream inputStream = BasicLocalNodeProcess.class.getClassLoader().getResourceAsStream("files/java/bukkit/eula.txt")) {
            Files.copy(Objects.requireNonNull(inputStream), Paths.get(path + "/eula.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    //Sponge
    private boolean isLogicallySpongeVanilla() {
        Version version = processInformation.getTemplate().getVersion();
        return version.equals(Version.SPONGEVANILLA_1_11_2)
                || version.equals(Version.SPONGEVANILLA_1_12_2);
    }

    private boolean isLogicallySpongeForge() {
        Version version = processInformation.getTemplate().getVersion();
        return version.equals(Version.SPONGEFORGE_1_10_2)
                || version.equals(Version.SPONGEFORGE_1_11_2)
                || version.equals(Version.SPONGEFORGE_1_12_2);
    }

    private void rewriteSpongeConfig() {
        File config = Paths.get(path + "/config/sponge/global.conf").toFile();
        rewriteFile(config, (UnaryOperator<String>) s -> {
            if (s.startsWith("ip-forwarding=")) {
                s = "ip-forwarding=true";
            } else if (s.startsWith("bungeecord=")) {
                s = "bungeecord=true";
            }

            return s;
        });
    }

    // ========================= //
    //Bungee
    private boolean isLogicallyBungee() {
        Version version = processInformation.getTemplate().getVersion();
        return version.equals(Version.WATERFALL)
                || version.equals(Version.TRAVERTINE)
                || version.equals(Version.HEXACORD)
                || version.equals(Version.BUNGEECORD);
    }

    private void rewriteBungeeConfig() {
        File file = Paths.get(path + "/config.yml").toFile();
        rewriteFile(file, (UnaryOperator<String>) s -> {
            if (s.startsWith("  host:")) {
                s = "  host: " + NodeExecutor.getInstance().getNodeConfig().getStartHost() + ":" + processInformation.getNetworkInfo().getPort();
            } else if (s.startsWith("ip_forward:")) {
                s = "ip_forward: true";
            }

            return s;
        });
    }

    // ========================= //
    //Waterdog
    private void rewriteWaterDogConfig() {
        File file = Paths.get(path + "/config.yml").toFile();
        rewriteFile(file, (UnaryOperator<String>) s -> {
            if (s.startsWith("  host:")) {
                s = "  host: " + NodeExecutor.getInstance().getNodeConfig().getStartHost() + ":" + processInformation.getNetworkInfo().getPort();
            } else if (s.startsWith("ip_forward:")) {
                s = "ip_forward: true";
            } else if (s.startsWith("use_xuid_for_uuid:")) {
                s = "use_xuid_for_uuid: true";
            } else if (s.startsWith("  raknet:")) {
                s = "  raknet: " + processInformation.getTemplate().getVersion().equals(Version.WATERDOG_PE);
            }

            return s;
        });
    }

    private boolean isLogicallyWaterDog() {
        return processInformation.getTemplate().getVersion().equals(Version.WATERDOG)
                || processInformation.getTemplate().getVersion().equals(Version.WATERDOG_PE);
    }

    // ========================= //
    //Velocity
    private void rewriteVelocityConfig() {
        File file = Paths.get(path + "/velocity.toml").toFile();
        rewriteFile(file, (UnaryOperator<String>) s -> {
            if (s.startsWith("bind")) {
                s = "bind = \"" + NodeExecutor.getInstance().getNodeConfig().getStartHost() + ":" + processInformation.getNetworkInfo().getPort() + "\"";
            } else if (s.startsWith("show-max-players") && processInformation.getProcessGroup().getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
                s = "show-max-players = " + processInformation.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
            } else if (s.startsWith("player-info-forwarding-mode")) {
                s = "player-info-forwarding-mode = \"LEGACY\"";
            }

            return s;
        });
    }

    // ========================= //
    //Glowstone
    private boolean isLogicallyGlowstone() {
        Version version = processInformation.getTemplate().getVersion();
        return version.equals(Version.GLOWSTONE_1_10_2)
                || version.equals(Version.GLOWSTONE_1_12_2);
    }

    // ========================= //
    //Startup

    private void rewriteGlowstoneConfig() {
        rewriteFile(new File(path + "/config/glowstone.yml"), (UnaryOperator<String>) s -> {
            if (s.trim().startsWith("ip:")) {
                s = "  ip: '" + this.processInformation.getNetworkInfo().getHost() + "'";
            }

            if (s.trim().startsWith("port:")) {
                s = "  port: " + this.processInformation.getNetworkInfo().getPort();
            }

            if (s.trim().startsWith("online-mode:")) {
                s = "  online-mode: false";
            }

            if (s.trim().startsWith("advanced.proxy-support:")) {
                s = "  online-mode: false";
            }

            return s;
        });
    }

    // ========================= //
    //Spigot
    private void rewriteSpigotConfig() {
        rewriteFile(new File(path + "/spigot.yml"), (UnaryOperator<String>) s -> {
            if (s.trim().startsWith("bungeecord:")) {
                s = "  bungeecord: true";
            }

            return s;
        });
    }

    private void chooseLogicallyStartup() {
        if (processInformation.getTemplate().isServer()) {
            serverStartup();
        } else {
            proxyStartup();
        }
    }

    private void serverStartup() {
        createEula();
        createTemplateAndFiles();

        if (isLogicallySpongeForge()) {
            Version version = processInformation.getTemplate().getVersion();
            String fileName = "reformcloud/files/" + version.getName().toLowerCase().replace(" ", "-") + ".zip";
            String destPath = "reformcloud/files/" + version.getName().toLowerCase().replace(" ", "-");

            if (!Files.exists(Paths.get(destPath))) {
                DownloadHelper.downloadAndDisconnect(version.getUrl(), fileName);

                SystemHelper.unZip(Paths.get(fileName).toFile(), destPath);
                SystemHelper.rename(Paths.get(destPath + "/sponge.jar").toFile(), destPath + "/process.jar");
                SystemHelper.deleteFile(new File(fileName));
            }

            SystemHelper.copyDirectory(Paths.get(destPath + "/mods"), Paths.get(path + "/mods"));
        }

        if (isLogicallyGlowstone()) {
            SystemHelper.createDirectory(Paths.get(path + "/config"));
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/glowstone/glowstone.yml", path + "/config/glowstone.yml");
            rewriteGlowstoneConfig();
        } else if (isLogicallySpongeVanilla()) {
            SystemHelper.createDirectory(Paths.get(path + "/config/sponge"));
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/sponge/vanilla/global.conf", path + "/config/sponge/global.conf");
            rewriteSpongeConfig();
        } else if (isLogicallySpongeForge()) {
            SystemHelper.createDirectory(Paths.get(path + "/config/sponge"));
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/sponge/forge/global.conf", path + "/config/sponge/global.conf");
            rewriteSpongeConfig();
        } else if (processInformation.getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/mcpe/nukkit/server.properties", path + "/server.properties");
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/mcpe/nukkit/nukkit.yml", path + "/nukkit.yml");
            Properties properties = new Properties();
            try (InputStream inputStream = Files.newInputStream(Paths.get(path + "/server.properties"))) {
                properties.load(inputStream);
                properties.setProperty("server-ip", NodeExecutor.getInstance().getNodeConfig().getStartHost());
                properties.setProperty("server-port", Integer.toString(processInformation.getNetworkInfo().getPort()));
                properties.setProperty("xbox-auth", Boolean.toString(false));

                try (OutputStream outputStream = Files.newOutputStream(Paths.get(path + "/server.properties"))) {
                    properties.store(outputStream, "ReformCloud2 node edit");
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        } else {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/bukkit/spigot.yml", path + "/spigot.yml");
            rewriteSpigotConfig();
        }

        if (!processInformation.getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            Properties properties = new Properties();
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/bukkit/server.properties", path + "/server.properties");
            try (InputStream inputStream = Files.newInputStream(Paths.get(path + "/server.properties"))) {
                properties.load(inputStream);
                properties.setProperty("server-ip", NodeExecutor.getInstance().getNodeConfig().getStartHost());
                properties.setProperty("server-port", Integer.toString(processInformation.getNetworkInfo().getPort()));
                properties.setProperty("online-mode", Boolean.toString(false));

                try (OutputStream outputStream = Files.newOutputStream(Paths.get(path + "/server.properties"))) {
                    properties.store(outputStream, "ReformCloud2 node edit");
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        if (!isLogicallySpongeForge() && !Files.exists(Paths.get(path + "/process.jar"))) {
            Version version = processInformation.getTemplate().getVersion();
            if (!Files.exists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }
        }
    }

    private void proxyStartup() {
        createTemplateAndFiles();
        if (!Files.exists(Paths.get(path + "/server-icon.png"))) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/server-icon.png", path + "/server-icon.png");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(Paths.get(path + "/server-icon.png").toFile());
            if (bufferedImage.getHeight() != 64 || bufferedImage.getWidth() != 64) {
                System.err.println("The server icon of the process " + processInformation.getName() + " is not correctly sized");
                SystemHelper.rename(Paths.get(path + "/server-icon.png").toFile(), path + "/server-icon-old.png");
                SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/server-icon.png", path + "/server-icon.png");
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        if (isLogicallyBungee()) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/bungee/internal-bungeecord-config.yml", path + "/config.yml");
            rewriteBungeeConfig();
        } else if (isLogicallyWaterDog()) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/mcpe/waterdog/internal-waterdog-config.yml", path + "/config.yml");
            rewriteWaterDogConfig();
        } else if (processInformation.getTemplate().getVersion().equals(Version.VELOCITY)) {
            SystemHelper.doInternalCopy(getClass().getClassLoader(), "files/java/velocity/velocity.toml", path + "/velocity.toml");
            rewriteVelocityConfig();
        }

        if (!Files.exists(Paths.get(path + "/process.jar"))) {
            Version version = processInformation.getTemplate().getVersion();
            if (!Files.exists(Paths.get("reformcloud/files/" + Version.format(version)))) {
                Version.downloadVersion(version);
            }
        }
    }

    private void createTemplateAndFiles() {
        SystemHelper.createDirectory(Paths.get(path + "/plugins"));
        SystemHelper.createDirectory(Paths.get(path + "/reformcloud/.connection"));
        new JsonConfiguration().add("key", NodeExecutor.getInstance().getNodeExecutorConfig().getCurrentNodeConnectionKey())
                .write(path + "/reformcloud/.connection/key.json");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LocalNodeProcess)) {
            return false;
        }

        LocalNodeProcess process = (LocalNodeProcess) obj;
        return process.getProcessInformation().getProcessUniqueID().equals(processInformation.getProcessUniqueID());
    }
}
