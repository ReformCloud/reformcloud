package systems.reformcloud.reformcloud2.signs.nukkit.adapter;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.signs.listener.CloudListener;
import systems.reformcloud.reformcloud2.signs.nukkit.commands.NukkitCommandSigns;
import systems.reformcloud.reformcloud2.signs.nukkit.listener.NukkitListener;
import systems.reformcloud.reformcloud2.signs.packets.api.in.APIPacketInCreateSign;
import systems.reformcloud.reformcloud2.signs.packets.api.in.APIPacketInDeleteSign;
import systems.reformcloud.reformcloud2.signs.packets.api.in.APIPacketInReloadConfig;
import systems.reformcloud.reformcloud2.signs.packets.api.out.APIPacketOutCreateSign;
import systems.reformcloud.reformcloud2.signs.packets.api.out.APIPacketOutDeleteSign;
import systems.reformcloud.reformcloud2.signs.util.LayoutUtil;
import systems.reformcloud.reformcloud2.signs.util.PlaceHolderUtil;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignLayout;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NukkitSignSystemAdapter implements SignSystemAdapter<BlockEntitySign> {

    private static NukkitSignSystemAdapter instance;

    private static final Map<String, Block> BLOCKS = new ConcurrentHashMap<>();

    static {
        try {
            Field fullList = Block.class.getDeclaredField("fullList");
            fullList.setAccessible(true);
            Block[] blocks = (Block[]) fullList.get(null);

            Arrays.stream(blocks).forEach(e -> BLOCKS.put(e.getName(), e));
        } catch (final IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public NukkitSignSystemAdapter(PluginBase plugin, SignConfig signConfig) {
        SignSystemAdapter.instance.set(instance = this);

        this.plugin = plugin;
        this.config = signConfig;

        ExecutorAPI.getInstance().getEventManager().registerListener(new CloudListener());
        Server.getInstance().getPluginManager().registerEvents(new NukkitListener(), plugin);

        PluginCommand command = (PluginCommand) plugin.getCommand("signs");
        command.setExecutor(new NukkitCommandSigns());
        command.setPermission("reformcloud.command.signs");

        ExecutorAPI.getInstance().getPacketHandler().registerNetworkHandlers(
                new APIPacketInCreateSign(),
                new APIPacketInDeleteSign(),
                new APIPacketInReloadConfig()
        );

        this.start();
    }

    private SignConfig config;

    private final Plugin plugin;

    private final List<CloudSign> cachedSigns = new LinkedList<>();

    private int taskID = -1;

    private final Map<UUID, ProcessInformation> notAssigned = new ConcurrentHashMap<>();

    private final AtomicInteger[] counter = new AtomicInteger[] {
            new AtomicInteger(-1), // start
            new AtomicInteger(-1), // connecting
            new AtomicInteger(-1), // empty
            new AtomicInteger(-1), // online
            new AtomicInteger(-1), // full
            new AtomicInteger(-1)  // maintenance
    };

    @Override
    public void handleProcessStart(@Nonnull ProcessInformation processInformation) {
        if (!processInformation.getTemplate().isServer()) {
            return;
        }

        if (isCurrent(processInformation)) {
            return;
        }

        assign(processInformation);
        updateAllSigns();
    }

    @Override
    public void handleProcessUpdate(@Nonnull ProcessInformation processInformation) {
        if (!processInformation.getTemplate().isServer()) {
            return;
        }

        if (isCurrent(processInformation)) {
            return;
        }

        if (notAssigned.containsKey(processInformation.getProcessUniqueID())) {
            notAssigned.put(processInformation.getProcessUniqueID(), processInformation);
            return;
        }

        updateAssign(processInformation);
        updateAllSigns();
    }

    @Override
    public void handleProcessStop(@Nonnull ProcessInformation processInformation) {
        if (!processInformation.getTemplate().isServer()) {
            return;
        }

        if (isCurrent(processInformation)) {
            return;
        }

        deleteAssignment(processInformation);
        updateAllSigns();
    }

    @Nonnull
    @Override
    public CloudSign createSign(@Nonnull BlockEntitySign blockEntitySign, @Nonnull String group) {
        CloudSign cloudSign = getSignConverter().to(blockEntitySign, group);
        if (getSignAt(cloudSign.getLocation()) != null) {
            return cloudSign;
        }

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new APIPacketOutCreateSign(cloudSign)));
        return cloudSign;
    }

    @Override
    public void deleteSign(@Nonnull CloudLocation location) {
        Streams.filterToReference(cachedSigns, e -> e.getLocation().equals(location)).ifPresent(e ->
                DefaultChannelManager.INSTANCE.get("Controller").ifPresent(s -> s.sendPacket(new APIPacketOutDeleteSign(e)))
        );
    }

    @Nullable
    @Override
    public CloudSign getSignAt(@Nonnull CloudLocation location) {
        return Streams.filter(cachedSigns, e -> e.getLocation().equals(location));
    }

    @Nonnull
    @Override
    public SignConverter<BlockEntitySign> getSignConverter() {
        return NukkitSignConverter.INSTANCE;
    }

    @Override
    public boolean canConnect(@Nonnull CloudSign cloudSign) {
        if (cloudSign.getCurrentTarget() == null || !cloudSign.getCurrentTarget().getNetworkInfo().isConnected()) {
            return false;
        }

        if (cloudSign.getCurrentTarget().getProcessGroup().getPlayerAccessConfiguration().isMaintenance()) {
            return getSelfLayout().isShowMaintenanceProcessesOnSigns();
        }

        return true;
    }

    @Override
    public void handleInternalSignCreate(@Nonnull CloudSign cloudSign) {
        this.cachedSigns.add(cloudSign);
        tryAssign();
        updateAllSigns();
    }

    @Override
    public void handleInternalSignDelete(@Nonnull CloudSign cloudSign) {
        Streams.filterToReference(cachedSigns, e -> e.getLocation().equals(cloudSign.getLocation())).ifPresent(e -> {
            this.cachedSigns.remove(e);
            removeAssign(e);
            updateAllSigns();
            doSync(() -> clearSign(getSignConverter().from(e)));
        });
    }

    @Override
    public void handleSignConfigUpdate(@Nonnull SignConfig config) {
        this.config = config;
        restartTask();
    }

    private void clearSign(BlockEntitySign sign) {
        if (sign == null) {
            return;
        }

        sign.setText(); // Nukkit sets an empty line if the array does not contain a string for a line
    }

    private boolean isCurrent(ProcessInformation processInformation) {
        return API.getInstance().getCurrentProcessInformation().getProcessUniqueID().equals(processInformation.getProcessUniqueID());
    }

    private void doSync(Runnable runnable) {
        if (!plugin.isEnabled()) {
            return;
        }

        Server.getInstance().getScheduler().scheduleTask(plugin, runnable);
    }

    private void assign(ProcessInformation processInformation) {
        for (CloudSign sign : cachedSigns) {
            if (sign.getCurrentTarget() == null
                    && sign.getGroup().equals(processInformation.getProcessGroup().getName())) {
                sign.setCurrentTarget(processInformation);
                notAssigned.remove(processInformation.getProcessUniqueID());
                return;
            }
        }

        notAssigned.put(processInformation.getProcessUniqueID(), processInformation);
    }

    private void updateAssign(ProcessInformation newInfo) {
        for (CloudSign sign : cachedSigns) {
            if (sign.getCurrentTarget() != null
                    && sign.getCurrentTarget().getProcessUniqueID().equals(newInfo.getProcessUniqueID())) {
                sign.setCurrentTarget(newInfo);
                break;
            }
        }
    }

    private void deleteAssignment(ProcessInformation processInformation) {
        for (CloudSign sign : cachedSigns) {
            if (sign.getCurrentTarget() != null
                    && sign.getCurrentTarget().getProcessUniqueID().equals(processInformation.getProcessUniqueID())) {
                sign.setCurrentTarget(null);
                return;
            }
        }

        notAssigned.remove(processInformation.getProcessUniqueID());
    }

    private void tryAssign() {
        if (notAssigned.isEmpty()) {
            return;
        }

        notAssigned.values().forEach(this::assign);
    }

    private void removeAssign(CloudSign sign) {
        if (sign.getCurrentTarget() == null) {
            return;
        }

        notAssigned.put(sign.getCurrentTarget().getProcessUniqueID(), sign.getCurrentTarget());
        sign.setCurrentTarget(null);
        tryAssign();
    }

    private void updateAllSigns() {
        doSync(this::updateAllSigns0);
    }

    private void updateAllSigns0() {
        SignLayout layout = getSelfLayout();

        SignSubLayout searching = LayoutUtil.getNextAndCheckFor(layout.getSearchingLayouts(), counter[0])
                .orElseThrow(() -> new RuntimeException("Waiting layout for current group not present"));

        SignSubLayout maintenance = LayoutUtil.getNextAndCheckFor(layout.getMaintenanceLayout(), counter[5])
                .orElseThrow(() -> new RuntimeException("Waiting layout for current group not present"));

        SignSubLayout connecting = LayoutUtil.getNextAndCheckFor(layout.getWaitingForConnectLayout(), counter[1])
                .orElseThrow(() -> new RuntimeException("Connecting layout for current group not present"));

        SignSubLayout empty = LayoutUtil.getNextAndCheckFor(layout.getEmptyLayout(), counter[2])
                .orElseThrow(() -> new RuntimeException("Empty layout for current group not present"));

        SignSubLayout full = LayoutUtil.getNextAndCheckFor(layout.getFullLayout(), counter[4])
                .orElseThrow(() -> new RuntimeException("Empty layout for current group not present"));

        SignSubLayout online = LayoutUtil.getNextAndCheckFor(layout.getOnlineLayout(), counter[3])
                .orElseThrow(() -> new RuntimeException("Empty layout for current group not present"));

        this.cachedSigns.forEach(e -> {
            if (e.getCurrentTarget() == null) {
                updateSign(e, searching, null);
                return;
            }

            if (e.getCurrentTarget().getProcessState().equals(ProcessState.INVISIBLE)
                    || e.getCurrentTarget().getProcessState().equals(ProcessState.STOPPED)
                    || e.getCurrentTarget().getProcessState().equals(ProcessState.STARTED)
                    || e.getCurrentTarget().getProcessState().equals(ProcessState.PREPARED)) {
                updateSign(e, searching, null);
                return;
            }

            if (e.getCurrentTarget().getProcessGroup().getPlayerAccessConfiguration().isMaintenance()) {
                if (layout.isShowMaintenanceProcessesOnSigns()) {
                    updateSign(e, maintenance, e.getCurrentTarget());
                    return;
                }

                updateSign(e, searching, null);
                return;
            }

            if (!e.getCurrentTarget().getNetworkInfo().isConnected()) {
                updateSign(e, connecting, e.getCurrentTarget());
                return;
            }

            if (e.getCurrentTarget().getOnlineCount() == 0) {
                updateSign(e, empty, e.getCurrentTarget());
                return;
            }

            if (e.getCurrentTarget().getOnlineCount() >= e.getCurrentTarget().getMaxPlayers()) {
                if (layout.isSearchingLayoutWhenFull()) {
                    updateSign(e, searching, null);
                    return;
                }

                updateSign(e, full, e.getCurrentTarget());
                return;
            }

            updateSign(e, online, e.getCurrentTarget());
        });
    }

    private void updateSign(CloudSign sign, SignSubLayout layout, ProcessInformation processInformation) {
        BlockEntitySign nukkit = getSignConverter().from(sign);
        if (nukkit == null || layout.getLines() == null || layout.getLines().length != 4) {
            return;
        }

        String[] lines = Arrays.stream(layout.getLines()).map(e -> replaceAll(e, sign.getGroup(), processInformation)).toArray(String[]::new);
        nukkit.setText(lines);
        this.changeBlockBehind(nukkit, layout);
    }

    private String replaceAll(String line, String group, ProcessInformation processInformation) {
        if (processInformation == null) {
            line = line.replace("%group%", group);
            return TextFormat.colorize('&', line);
        }

        return PlaceHolderUtil.format(line, group, processInformation, s -> TextFormat.colorize('&', s));
    }

    private void changeBlockBehind(BlockEntitySign sign, SignSubLayout layout) {
        if (!(sign.getBlock() instanceof BlockSignPost)) {
            return;
        }

        BlockSignPost post = (BlockSignPost) sign.getBlock();
        Location location = post.getSide(post.getBlockFace().getOpposite()).getLocation();
        Block block = BLOCKS.get(layout.getBlock());
        if (block == null) {
            return;
        }

        location.getLevel().setBlock(location, block, true, true);
    }

    private SignLayout getSelfLayout() {
        return LayoutUtil
                .getLayoutFor(API.getInstance().getCurrentProcessInformation().getProcessGroup().getName(), config)
                .orElseThrow(() -> new RuntimeException("No sign config present for context global or current group"));
    }

    private void start() {
        Task.EXECUTOR.execute(() -> {
            Collection<CloudSign> signs = ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(SignSystemAdapter.table, "signs", null, k -> k.get("signs", new TypeToken<Collection<CloudSign>>() {
            }));
            if (signs == null) {
                return;
            }

            cachedSigns.addAll(signs);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().forEach(this::handleProcessStart);
            runTasks();
        });
    }

    private void restartTask() {
        if (taskID != -1) {
            Server.getInstance().getScheduler().cancelTask(taskID);
        }

        runTasks();
    }

    private void runTasks() {
        taskID = Server.getInstance().getScheduler().scheduleRepeatingTask(plugin, this::updateAllSigns, fromLong(20 * config.getUpdateInterval()), true).getTaskId();
    }

    private int fromLong(long l) {
        if (l > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return l < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) l;
    }

    public static NukkitSignSystemAdapter getInstance() {
        return instance;
    }
}
