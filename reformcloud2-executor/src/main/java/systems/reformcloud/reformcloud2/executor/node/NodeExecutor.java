package systems.reformcloud.reformcloud2.executor.node;

import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.Template;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.DefaultNetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.DefaultWebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.WebServer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.node.Node;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;
import systems.reformcloud.reformcloud2.executor.node.cluster.DefaultClusterManager;
import systems.reformcloud.reformcloud2.executor.node.cluster.DefaultNodeInternalCluster;
import systems.reformcloud.reformcloud2.executor.node.config.NodeExecutorConfig;
import systems.reformcloud.reformcloud2.executor.node.network.DefaultNodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.process.LocalNodeProcessManager;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class NodeExecutor extends Node {

    private static NodeExecutor instance;

    private static volatile boolean running = false;

    private LoggerBase loggerBase;

    private final CommandManager commandManager = new DefaultCommandManager();

    private final CommandSource console = new ConsoleCommandSource(commandManager);

    private final ApplicationLoader applicationLoader = new DefaultApplicationLoader();

    private final NetworkServer networkServer = new DefaultNetworkServer();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final WebServer webServer = new DefaultWebServer();

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NodeProcessManager nodeProcessManager = new LocalNodeProcessManager();

    private final NodeExecutorConfig nodeExecutorConfig = new NodeExecutorConfig();

    private NodeNetworkManager nodeNetworkManager;

    NodeExecutor() {
        Node.setInstance(this);
        instance = this;
    }

    @Override
    protected void bootstrap() {
        this.nodeExecutorConfig.init();
        this.nodeNetworkManager = new DefaultNodeNetworkManager(
                new LocalNodeProcessManager(),
                new DefaultNodeInternalCluster(new DefaultClusterManager(
                        nodeExecutorConfig.getClusterFirewall()), nodeExecutorConfig.getSelf(),packetHandler
                )
        );
    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public NetworkServer getNetworkServer() {
        return null;
    }

    @Override
    public CommandManager getCommandManager() {
        return null;
    }

    @Override
    public PacketHandler getPacketHandler() {
        return null;
    }

    @Override
    public Task<Boolean> loadApplicationAsync(InstallableApplication application) {
        return null;
    }

    @Override
    public Task<Boolean> unloadApplicationAsync(LoadedApplication application) {
        return null;
    }

    @Override
    public Task<Boolean> unloadApplicationAsync(String application) {
        return null;
    }

    @Override
    public Task<LoadedApplication> getApplicationAsync(String name) {
        return null;
    }

    @Override
    public Task<List<LoadedApplication>> getApplicationsAsync() {
        return null;
    }

    @Override
    public boolean loadApplication(InstallableApplication application) {
        return false;
    }

    @Override
    public boolean unloadApplication(LoadedApplication application) {
        return false;
    }

    @Override
    public boolean unloadApplication(String application) {
        return false;
    }

    @Override
    public LoadedApplication getApplication(String name) {
        return null;
    }

    @Override
    public List<LoadedApplication> getApplications() {
        return null;
    }

    @Override
    public Task<Boolean> isClientConnectedAsync(String name) {
        return null;
    }

    @Override
    public Task<String> getClientStartHostAsync(String name) {
        return null;
    }

    @Override
    public Task<Integer> getMaxMemoryAsync(String name) {
        return null;
    }

    @Override
    public Task<Integer> getMaxProcessesAsync(String name) {
        return null;
    }

    @Override
    public Task<ClientRuntimeInformation> getClientInformationAsync(String name) {
        return null;
    }

    @Override
    public boolean isClientConnected(String name) {
        return false;
    }

    @Override
    public String getClientStartHost(String name) {
        return null;
    }

    @Override
    public int getMaxMemory(String name) {
        return 0;
    }

    @Override
    public int getMaxProcesses(String name) {
        return 0;
    }

    @Override
    public ClientRuntimeInformation getClientInformation(String name) {
        return null;
    }

    @Override
    public Task<Void> sendColouredLineAsync(String line) throws IllegalAccessException {
        return null;
    }

    @Override
    public Task<Void> sendRawLineAsync(String line) {
        return null;
    }

    @Override
    public Task<String> dispatchCommandAndGetResultAsync(String commandLine) {
        return null;
    }

    @Override
    public Task<Command> getControllerCommandAsync(String name) {
        return null;
    }

    @Override
    public Task<Boolean> isControllerCommandRegisteredAsync(String name) {
        return null;
    }

    @Override
    public void sendColouredLine(String line) throws IllegalAccessException {

    }

    @Override
    public void sendRawLine(String line) {

    }

    @Override
    public String dispatchCommandAndGetResult(String commandLine) {
        return null;
    }

    @Override
    public Command getControllerCommand(String name) {
        return null;
    }

    @Override
    public boolean isControllerCommandRegistered(String name) {
        return false;
    }

    @Override
    public Task<JsonConfiguration> findAsync(String table, String key, String identifier) {
        return null;
    }

    @Override
    public <T> Task<T> findAsync(String table, String key, String identifier, Function<JsonConfiguration, T> function) {
        return null;
    }

    @Override
    public Task<Void> insertAsync(String table, String key, String identifier, JsonConfiguration data) {
        return null;
    }

    @Override
    public Task<Boolean> updateAsync(String table, String key, JsonConfiguration newData) {
        return null;
    }

    @Override
    public Task<Boolean> updateIfAbsentAsync(String table, String identifier, JsonConfiguration newData) {
        return null;
    }

    @Override
    public Task<Void> removeAsync(String table, String key) {
        return null;
    }

    @Override
    public Task<Void> removeIfAbsentAsync(String table, String identifier) {
        return null;
    }

    @Override
    public Task<Boolean> createDatabaseAsync(String name) {
        return null;
    }

    @Override
    public Task<Boolean> deleteDatabaseAsync(String name) {
        return null;
    }

    @Override
    public Task<Boolean> containsAsync(String table, String key) {
        return null;
    }

    @Override
    public Task<Integer> sizeAsync(String table) {
        return null;
    }

    @Override
    public JsonConfiguration find(String table, String key, String identifier) {
        return null;
    }

    @Override
    public <T> T find(String table, String key, String identifier, Function<JsonConfiguration, T> function) {
        return null;
    }

    @Override
    public void insert(String table, String key, String identifier, JsonConfiguration data) {

    }

    @Override
    public boolean update(String table, String key, JsonConfiguration newData) {
        return false;
    }

    @Override
    public boolean updateIfAbsent(String table, String identifier, JsonConfiguration newData) {
        return false;
    }

    @Override
    public void remove(String table, String key) {

    }

    @Override
    public void removeIfAbsent(String table, String identifier) {

    }

    @Override
    public boolean createDatabase(String name) {
        return false;
    }

    @Override
    public boolean deleteDatabase(String name) {
        return false;
    }

    @Override
    public boolean contains(String table, String key) {
        return false;
    }

    @Override
    public int size(String table) {
        return 0;
    }

    @Override
    public Task<MainGroup> createMainGroupAsync(String name) {
        return null;
    }

    @Override
    public Task<MainGroup> createMainGroupAsync(String name, List<String> subgroups) {
        return null;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name) {
        return null;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent) {
        return null;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates) {
        return null;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration) {
        return null;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration) {
        return null;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        return null;
    }

    @Override
    public Task<ProcessGroup> createProcessGroupAsync(ProcessGroup processGroup) {
        return null;
    }

    @Override
    public Task<MainGroup> updateMainGroupAsync(MainGroup mainGroup) {
        return null;
    }

    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(ProcessGroup processGroup) {
        return null;
    }

    @Override
    public Task<MainGroup> getMainGroupAsync(String name) {
        return null;
    }

    @Override
    public Task<ProcessGroup> getProcessGroupAsync(String name) {
        return null;
    }

    @Override
    public Task<Void> deleteMainGroupAsync(String name) {
        return null;
    }

    @Override
    public Task<Void> deleteProcessGroupAsync(String name) {
        return null;
    }

    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        return null;
    }

    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        return null;
    }

    @Override
    public MainGroup createMainGroup(String name) {
        return null;
    }

    @Override
    public MainGroup createMainGroup(String name, List<String> subgroups) {
        return null;
    }

    @Override
    public ProcessGroup createProcessGroup(String name) {
        return null;
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent) {
        return null;
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent, List<Template> templates) {
        return null;
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration) {
        return null;
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration) {
        return null;
    }

    @Override
    public ProcessGroup createProcessGroup(String name, String parent, List<Template> templates, StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        return null;
    }

    @Override
    public ProcessGroup createProcessGroup(ProcessGroup processGroup) {
        return null;
    }

    @Override
    public MainGroup updateMainGroup(MainGroup mainGroup) {
        return null;
    }

    @Override
    public ProcessGroup updateProcessGroup(ProcessGroup processGroup) {
        return null;
    }

    @Override
    public MainGroup getMainGroup(String name) {
        return null;
    }

    @Override
    public ProcessGroup getProcessGroup(String name) {
        return null;
    }

    @Override
    public void deleteMainGroup(String name) {

    }

    @Override
    public void deleteProcessGroup(String name) {

    }

    @Override
    public List<MainGroup> getMainGroups() {
        return null;
    }

    @Override
    public List<ProcessGroup> getProcessGroups() {
        return null;
    }

    @Override
    public Task<Void> sendMessageAsync(UUID player, String message) {
        return null;
    }

    @Override
    public Task<Void> kickPlayerAsync(UUID player, String message) {
        return null;
    }

    @Override
    public Task<Void> kickPlayerFromServerAsync(UUID player, String message) {
        return null;
    }

    @Override
    public Task<Void> playSoundAsync(UUID player, String sound, float f1, float f2) {
        return null;
    }

    @Override
    public Task<Void> sendTitleAsync(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        return null;
    }

    @Override
    public Task<Void> playEffectAsync(UUID player, String entityEffect) {
        return null;
    }

    @Override
    public <T> Task<Void> playEffectAsync(UUID player, String effect, T data) {
        return null;
    }

    @Override
    public Task<Void> respawnAsync(UUID player) {
        return null;
    }

    @Override
    public Task<Void> teleportAsync(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        return null;
    }

    @Override
    public Task<Void> connectAsync(UUID player, String server) {
        return null;
    }

    @Override
    public Task<Void> connectAsync(UUID player, ProcessInformation server) {
        return null;
    }

    @Override
    public Task<Void> connectAsync(UUID player, UUID target) {
        return null;
    }

    @Override
    public Task<Void> setResourcePackAsync(UUID player, String pack) {
        return null;
    }

    @Override
    public void sendMessage(UUID player, String message) {

    }

    @Override
    public void kickPlayer(UUID player, String message) {

    }

    @Override
    public void kickPlayerFromServer(UUID player, String message) {

    }

    @Override
    public void playSound(UUID player, String sound, float f1, float f2) {

    }

    @Override
    public void sendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {

    }

    @Override
    public void playEffect(UUID player, String entityEffect) {

    }

    @Override
    public <T> void playEffect(UUID player, String effect, T data) {

    }

    @Override
    public void respawn(UUID player) {

    }

    @Override
    public void teleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {

    }

    @Override
    public void connect(UUID player, String server) {

    }

    @Override
    public void connect(UUID player, ProcessInformation server) {

    }

    @Override
    public void connect(UUID player, UUID target) {

    }

    @Override
    public void setResourcePack(UUID player, String pack) {

    }

    @Override
    public Task<Void> installPluginAsync(String process, InstallablePlugin plugin) {
        return null;
    }

    @Override
    public Task<Void> installPluginAsync(ProcessInformation process, InstallablePlugin plugin) {
        return null;
    }

    @Override
    public Task<Void> unloadPluginAsync(String process, Plugin plugin) {
        return null;
    }

    @Override
    public Task<Void> unloadPluginAsync(ProcessInformation process, Plugin plugin) {
        return null;
    }

    @Override
    public Task<Plugin> getInstalledPluginAsync(String process, String name) {
        return null;
    }

    @Override
    public Task<Plugin> getInstalledPluginAsync(ProcessInformation process, String name) {
        return null;
    }

    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(String process, String author) {
        return null;
    }

    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(ProcessInformation process, String author) {
        return null;
    }

    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(String process) {
        return null;
    }

    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(ProcessInformation processInformation) {
        return null;
    }

    @Override
    public void installPlugin(String process, InstallablePlugin plugin) {

    }

    @Override
    public void installPlugin(ProcessInformation process, InstallablePlugin plugin) {

    }

    @Override
    public void unloadPlugin(String process, Plugin plugin) {

    }

    @Override
    public void unloadPlugin(ProcessInformation process, Plugin plugin) {

    }

    @Override
    public Plugin getInstalledPlugin(String process, String name) {
        return null;
    }

    @Override
    public Plugin getInstalledPlugin(ProcessInformation process, String name) {
        return null;
    }

    @Override
    public Collection<DefaultPlugin> getPlugins(String process, String author) {
        return null;
    }

    @Override
    public Collection<DefaultPlugin> getPlugins(ProcessInformation process, String author) {
        return null;
    }

    @Override
    public Collection<DefaultPlugin> getPlugins(String process) {
        return null;
    }

    @Override
    public Collection<DefaultPlugin> getPlugins(ProcessInformation processInformation) {
        return null;
    }

    @Override
    public Task<ProcessInformation> startProcessAsync(String groupName) {
        return null;
    }

    @Override
    public Task<ProcessInformation> startProcessAsync(String groupName, String template) {
        return null;
    }

    @Override
    public Task<ProcessInformation> startProcessAsync(String groupName, String template, JsonConfiguration configurable) {
        return null;
    }

    @Override
    public Task<ProcessInformation> stopProcessAsync(String name) {
        return null;
    }

    @Override
    public Task<ProcessInformation> stopProcessAsync(UUID uniqueID) {
        return null;
    }

    @Override
    public Task<ProcessInformation> getProcessAsync(String name) {
        return null;
    }

    @Override
    public Task<ProcessInformation> getProcessAsync(UUID uniqueID) {
        return null;
    }

    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        return null;
    }

    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(String group) {
        return null;
    }

    @Override
    public Task<Void> executeProcessCommandAsync(String name, String commandLine) {
        return null;
    }

    @Override
    public Task<Integer> getGlobalOnlineCountAsync(Collection<String> ignoredProxies) {
        return null;
    }

    @Override
    public Task<ProcessInformation> getThisProcessInformationAsync() {
        return null;
    }

    @Override
    public ProcessInformation startProcess(String groupName) {
        return null;
    }

    @Override
    public ProcessInformation startProcess(String groupName, String template) {
        return null;
    }

    @Override
    public ProcessInformation startProcess(String groupName, String template, JsonConfiguration configurable) {
        return null;
    }

    @Override
    public ProcessInformation stopProcess(String name) {
        return null;
    }

    @Override
    public ProcessInformation stopProcess(UUID uniqueID) {
        return null;
    }

    @Override
    public ProcessInformation getProcess(String name) {
        return null;
    }

    @Override
    public ProcessInformation getProcess(UUID uniqueID) {
        return null;
    }

    @Override
    public List<ProcessInformation> getAllProcesses() {
        return null;
    }

    @Override
    public List<ProcessInformation> getProcesses(String group) {
        return null;
    }

    @Override
    public void executeProcessCommand(String name, String commandLine) {

    }

    @Override
    public int getGlobalOnlineCount(Collection<String> ignoredProxies) {
        return 0;
    }

    @Override
    public ProcessInformation getThisProcessInformation() {
        return null;
    }

    @Override
    public void update(ProcessInformation processInformation) {

    }

    @Override
    public void reload() throws Exception {

    }
}
