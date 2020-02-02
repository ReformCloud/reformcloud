package systems.reformcloud.reformcloud2.signs.sponge.adapter;

import com.google.gson.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.signs.listener.CloudListener;
import systems.reformcloud.reformcloud2.signs.packets.api.in.APIPacketInCreateSign;
import systems.reformcloud.reformcloud2.signs.packets.api.in.APIPacketInDeleteSign;
import systems.reformcloud.reformcloud2.signs.packets.api.in.APIPacketInReloadConfig;
import systems.reformcloud.reformcloud2.signs.packets.api.out.APIPacketOutCreateSign;
import systems.reformcloud.reformcloud2.signs.packets.api.out.APIPacketOutDeleteSign;
import systems.reformcloud.reformcloud2.signs.sponge.command.SpongeCommandSigns;
import systems.reformcloud.reformcloud2.signs.sponge.listener.SpongeListener;
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
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SpongeSignSystemAdapter implements SignSystemAdapter<Sign> {

    private static SpongeSignSystemAdapter instance;

    private static final Map<String, BlockType> BLOCK_TYPES = new HashMap<>();

    static {
        Arrays.stream(BlockTypes.class.getDeclaredFields()).filter(
                e -> Modifier.isFinal(e.getModifiers()) && Modifier.isStatic(e.getModifiers())
        ).forEach(e -> {
            try {
                BLOCK_TYPES.put(e.getName(), (BlockType) e.get(BlockTypes.class));
            } catch (final IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });
    }

    public SpongeSignSystemAdapter(Object plugin, SignConfig config) {
        SignSystemAdapter.instance.set(instance = this);

        this.plugin = plugin;
        this.config = config;

        ExecutorAPI.getInstance().getEventManager().registerListener(new CloudListener());
        Sponge.getEventManager().registerListeners(plugin, new SpongeListener());

        ExecutorAPI.getInstance().getPacketHandler().registerNetworkHandlers(
                new APIPacketInCreateSign(),
                new APIPacketInDeleteSign(),
                new APIPacketInReloadConfig()
        );

        CommandSpec signs = CommandSpec.builder()
                .description(Text.of("The default signs command of the cloud system"))
                .permission("reformcloud.command.signs")
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("Execute type"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("Target group")))
                )
                .executor(new SpongeCommandSigns())
                .build();
        Sponge.getCommandManager().register(plugin, signs, "signs");

        start();
    }

    private final List<CloudSign> cachedSigns = new LinkedList<>();

    private final Object plugin;

    private SignConfig config;

    private UUID taskID;

    private Map<UUID, ProcessInformation> notAssigned = new ConcurrentHashMap<>();

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
    public CloudSign createSign(@Nonnull Sign sign, @Nonnull String group) {
        CloudSign cloudSign = getSignConverter().to(sign, group);
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
    public SignConverter<Sign> getSignConverter() {
        return SpongeSignConverter.INSTANCE;
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
            clearLines(getSignConverter().from(e));
            updateAllSigns();
        });
    }

    @Override
    public void handleSignConfigUpdate(@Nonnull SignConfig config) {
        this.config = config;
        restart();
    }

    private UUID repeat(Runnable runnable, long interval) {
        return Sponge.getScheduler().createTaskBuilder()
                .execute(runnable)
                .delayTicks(0)
                .intervalTicks(interval)
                .submit(plugin)
                .getUniqueId();
    }

    private boolean isCurrent(ProcessInformation processInformation) {
        ProcessInformation info = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        return info != null && info.getProcessUniqueID().equals(processInformation.getProcessUniqueID());
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

    private void clearLines(Sign sign) {
        if (sign == null) {
            return;
        }

        SignData signData = sign.getSignData();
        signData.setElement(0, Text.of());
        signData.setElement(1, Text.of());
        signData.setElement(2, Text.of());
        signData.setElement(3, Text.of());

        sign.offer(signData);
    }

    private void updateAllSigns() {
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
        Sign sponge = getSignConverter().from(sign);
        if (sponge == null || layout.getLines() == null || layout.getLines().length != 4) {
            return;
        }

        SignData signData = sponge.getSignData();
        signData.setElement(0, replaceAll(layout.getLines()[0], sign.getGroup(), processInformation));
        signData.setElement(1, replaceAll(layout.getLines()[1], sign.getGroup(), processInformation));
        signData.setElement(2, replaceAll(layout.getLines()[2], sign.getGroup(), processInformation));
        signData.setElement(3, replaceAll(layout.getLines()[3], sign.getGroup(), processInformation));
        sponge.offer(signData);

        this.changeBlockBehind(sponge, layout);
    }

    private void changeBlockBehind(Sign sign, SignSubLayout layout) {
        Optional<DirectionalData> directionalData = sign.getLocation().get(DirectionalData.class);
        if (!directionalData.isPresent()) {
            return;
        }

        DirectionalData data = directionalData.get();
        data.get(Keys.DIRECTION).ifPresent(e -> {
            Location<World> blockRelative = sign.getLocation().getBlockRelative(e.getOpposite());
            BlockType blockType = BLOCK_TYPES.get(layout.getBlock());
            if (blockType == null) {
                return;
            }

            blockRelative.setBlockType(blockType);
        });
    }

    private Text replaceAll(String line, String group, ProcessInformation processInformation) {
        if (processInformation == null) {
            line = line.replace("%group%", group);
            return TextSerializers.FORMATTING_CODE.deserialize(line);
        }

        return PlaceHolderUtil.format(line, group, processInformation, TextSerializers.FORMATTING_CODE::deserialize);
    }

    private void start() {
        systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task.EXECUTOR.execute(() -> {
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

    private void runTasks() {
        this.taskID = repeat(this::updateAllSigns, this.config.getUpdateInterval() * 20);
    }

    private void restart() {
        if (this.taskID != null) {
            Sponge.getScheduler().getTaskById(taskID).ifPresent(Task::cancel);
        }

        runTasks();
    }

    private SignLayout getSelfLayout() {
        return LayoutUtil.getLayoutFor(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation().getProcessGroup().getName(), config).orElseThrow(
                () -> new RuntimeException("No sign config present for context global or current group"));
    }

    public static SpongeSignSystemAdapter getInstance() {
        return instance;
    }
}
