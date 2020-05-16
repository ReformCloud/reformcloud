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
package systems.reformcloud.reformcloud2.signs;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketCreateSign;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketDeleteBulkSigns;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketDeleteSign;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketRequestSignLayoutsResult;
import systems.reformcloud.reformcloud2.signs.listener.CloudListener;
import systems.reformcloud.reformcloud2.signs.packets.PacketReloadSignConfig;
import systems.reformcloud.reformcloud2.signs.util.LayoutUtil;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.Utils;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignLayout;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class SharedSignSystemAdapter<T> implements SignSystemAdapter<T> {

    private static final String[] EMPTY_SIGN = new String[]{"", "", "", ""};
    protected final UUID ownUniqueID = API.getInstance().getCurrentProcessInformation().getProcessDetail().getProcessUniqueID();
    protected final Collection<CloudSign> signs = Collections.synchronizedCollection(new ArrayList<CloudSign>() {
        @NotNull
        @Override
        public Iterator<CloudSign> iterator() {
            synchronized (this) {
                return super.iterator();
            }
        }
    });
    protected final Set<ProcessInformation> allProcesses = Collections.synchronizedSet(new HashSet<>());
    protected final AtomicInteger[] counter = new AtomicInteger[]{
            new AtomicInteger(-1), // start
            new AtomicInteger(-1), // connecting
            new AtomicInteger(-1), // empty
            new AtomicInteger(-1), // online
            new AtomicInteger(-1), // full
            new AtomicInteger(-1)  // maintenance
    };
    protected SignConfig signConfig;

    public SharedSignSystemAdapter(@NotNull SignConfig signConfig) {
        this.signConfig = signConfig;

        SignSystemAdapter.instance.set(this);

        ExecutorAPI.getInstance().getEventManager().registerListener(new CloudListener());
        ExecutorAPI.getInstance().getPacketHandler().registerNetworkHandlers(
                PacketCreateSign.class,
                PacketDeleteSign.class,
                PacketRequestSignLayoutsResult.class,
                PacketReloadSignConfig.class
        );

        this.start();
    }

    @Override
    public void handleProcessStart(@NotNull ProcessInformation processInformation) {
        this.allProcesses.add(processInformation);

        if (!processInformation.getProcessDetail().getTemplate().isServer()
                || processInformation.getProcessDetail().getProcessUniqueID().equals(this.ownUniqueID)) {
            return;
        }

        if (!Utils.canConnectPerState(processInformation)) {
            return;
        }

        this.tryAssign(processInformation);
    }

    @Override
    public void handleProcessUpdate(@NotNull ProcessInformation processInformation) {
        if (!processInformation.getProcessDetail().getTemplate().isServer()
                || processInformation.getProcessDetail().getProcessUniqueID().equals(this.ownUniqueID)) {
            return;
        }

        CloudSign sign = this.getSignOf(processInformation);
        if (sign == null && Utils.canConnectPerState(processInformation)) {
            this.tryAssign(processInformation);
            return;
        }

        if (sign != null && !Utils.canConnectPerState(processInformation)) {
            sign.setCurrentTarget(null);
            this.updateSigns();
            this.tryAssignUnassigned();
            return;
        }

        if (sign == null) {
            return;
        }

        sign.setCurrentTarget(processInformation);
        this.updateSigns();
    }

    @Override
    public void handleProcessStop(@NotNull ProcessInformation processInformation) {
        this.allProcesses.remove(processInformation);

        CloudSign sign = this.getSignOf(processInformation);
        if (sign != null) {
            sign.setCurrentTarget(null);
            this.updateSigns();
            this.tryAssignUnassigned();
        }
    }

    @Override
    public @NotNull CloudSign createSign(@NotNull T t, @NotNull String group) {
        CloudSign cloudSign = this.getSignAt(this.getSignConverter().to(t));
        if (cloudSign != null) {
            return cloudSign;
        }

        cloudSign = this.getSignConverter().to(t, group);
        this.sendPacketToController(new PacketCreateSign(cloudSign));

        return cloudSign;
    }

    @Override
    public void deleteSign(@NotNull CloudLocation location) {
        CloudSign sign = this.getSignAt(location);
        if (sign != null) {
            this.sendPacketToController(new PacketDeleteSign(sign));
        }
    }

    @Override
    public void deleteAll() {
        this.bulkDelete(this.signs);
    }

    @Override
    public void cleanSigns() {
        this.bulkDelete(this.signs.stream()
                .filter(e -> this.getSignConverter().from(e) == null)
                .collect(Collectors.toList()));
    }

    private void bulkDelete(@NotNull Collection<CloudSign> signs) {
        if (signs.isEmpty()) {
            return;
        }

        this.sendPacketToController(new PacketDeleteBulkSigns(signs));
    }

    @Override
    public @Nullable CloudSign getSignAt(@NotNull CloudLocation location) {
        for (CloudSign sign : this.signs) {
            if (sign.getLocation().equals(location)) {
                return sign;
            }
        }

        return null;
    }

    @Override
    public boolean canConnect(@NotNull CloudSign cloudSign) {
        SignLayout layout = this.getSignLayout();
        if (layout == null) {
            return false;
        }

        return cloudSign.getCurrentTarget() != null && Utils.canConnect(cloudSign.getCurrentTarget(), layout);
    }

    @Override
    public void handleInternalSignCreate(@NotNull CloudSign cloudSign) {
        this.signs.add(cloudSign);
        this.tryAssignUnassigned();
    }

    @Override
    public void handleInternalSignDelete(@NotNull CloudSign cloudSign) {
        CloudSign other = Streams.filter(this.signs, e -> e.equals(cloudSign));

        this.signs.remove(cloudSign);
        this.setSignLines(cloudSign, EMPTY_SIGN);
        if (other == null) {
            return;
        }

        this.removeAssignment(other);
        this.tryAssignUnassigned();
    }

    protected void removeAssignment(@NotNull CloudSign sign) {
        if (sign.getCurrentTarget() == null) {
            return;
        }

        sign.setCurrentTarget(null);
    }

    protected void tryAssignUnassigned() {
        for (ProcessInformation notAssignedProcess : this.allProcesses) {
            if (this.isProcessAssigned(notAssignedProcess) || !Utils.canConnectPerState(notAssignedProcess)) {
                continue;
            }

            CloudSign sign = this.getFreeSignForGroup(notAssignedProcess.getProcessGroup());
            if (sign == null) {
                continue;
            }

            sign.setCurrentTarget(notAssignedProcess);
            this.updateSigns();
        }
    }

    protected abstract void setSignLines(@NotNull CloudSign cloudSign, @NotNull String[] lines);

    protected void updateSigns() {
        SignLayout layout = this.getSignLayout();
        if (layout == null) {
            throw new RuntimeException("Unable to find sign layout of current group");
        }

        SignSubLayout searching = LayoutUtil.getNextAndCheckFor(layout.getSearchingLayouts(), counter[0])
                .orElseThrow(() -> new RuntimeException("Waiting layout for current group not present"));

        SignSubLayout maintenance = LayoutUtil.getNextAndCheckFor(layout.getMaintenanceLayout(), counter[5])
                .orElseThrow(() -> new RuntimeException("Maintenance layout for current group not present"));

        SignSubLayout connecting = LayoutUtil.getNextAndCheckFor(layout.getWaitingForConnectLayout(), counter[1])
                .orElseThrow(() -> new RuntimeException("Connecting layout for current group not present"));

        SignSubLayout empty = LayoutUtil.getNextAndCheckFor(layout.getEmptyLayout(), counter[2])
                .orElseThrow(() -> new RuntimeException("Empty layout for current group not present"));

        SignSubLayout full = LayoutUtil.getNextAndCheckFor(layout.getFullLayout(), counter[4])
                .orElseThrow(() -> new RuntimeException("Full layout for current group not present"));

        SignSubLayout online = LayoutUtil.getNextAndCheckFor(layout.getOnlineLayout(), counter[3])
                .orElseThrow(() -> new RuntimeException("Online layout for current group not present"));

        for (CloudSign sign : this.signs) {
            if (sign.getCurrentTarget() == null) {
                this.setLines(sign, searching);
                continue;
            }

            if (sign.getCurrentTarget().getProcessGroup().getPlayerAccessConfiguration().isMaintenance()) {
                if (layout.isShowMaintenanceProcessesOnSigns()) {
                    this.setLines(sign, maintenance);
                    continue;
                }

                this.setLines(sign, searching);
                continue;
            }

            if (!sign.getCurrentTarget().getNetworkInfo().isConnected()) {
                this.setLines(sign, connecting);
                continue;
            }

            if (!sign.getCurrentTarget().getProcessDetail().getProcessState().isReady()) {
                this.setLines(sign, searching);
                continue;
            }

            if (sign.getCurrentTarget().getProcessPlayerManager().getOnlineCount() == 0) {
                this.setLines(sign, empty);
                continue;
            }

            if (sign.getCurrentTarget().getProcessPlayerManager().getOnlineCount() >= sign.getCurrentTarget().getProcessDetail().getMaxPlayers()) {
                if (layout.isSearchingLayoutWhenFull()) {
                    this.setLines(sign, searching);
                    continue;
                }

                this.setLines(sign, full);
                continue;
            }

            this.setLines(sign, online);
        }
    }

    protected abstract void runTasks();

    @NotNull
    protected abstract String replaceAll(@NotNull String line, @NotNull String group, @Nullable ProcessInformation processInformation);

    public abstract void changeBlock(@NotNull CloudSign sign, @NotNull SignSubLayout layout);

    private void setLines(@NotNull CloudSign sign, @NotNull SignSubLayout layout) {
        String[] copy = layout.getLines().clone();
        if (copy.length != 4) {
            return;
        }

        for (int i = 0; i <= 3; i++) {
            copy[i] = this.replaceAll(copy[i], sign.getGroup(), sign.getCurrentTarget());
        }

        this.setSignLines(sign, copy);
        this.changeBlock(sign, layout);
    }

    @Nullable
    protected SignLayout getSignLayout() {
        return LayoutUtil.getLayoutFor(
                API.getInstance().getCurrentProcessInformation().getProcessGroup().getName(),
                this.signConfig
        ).orElse(null);
    }

    private void sendPacketToController(@NotNull Packet packet) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(packet));
    }

    private void start() {
        Task.EXECUTOR.execute(() -> {
            Collection<CloudSign> signs = ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(SignSystemAdapter.table, "signs", null, k -> k.get("signs", new TypeToken<Collection<CloudSign>>() {
            }));
            if (signs == null) {
                return;
            }

            this.signs.addAll(signs.stream().filter(e -> {
                String current = API.getInstance().getCurrentProcessInformation().getProcessGroup().getName();
                return current.equals(e.getLocation().getGroup());
            }).collect(Collectors.toList()));

            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().forEach(this::handleProcessStart);
            this.runTasks();
        });
    }

    @Nullable
    private CloudSign getSignOf(@NotNull ProcessInformation other) {
        for (CloudSign sign : this.signs) {
            if (sign.getCurrentTarget() != null
                    && sign.getCurrentTarget().getProcessDetail().getProcessUniqueID().equals(other.getProcessDetail().getProcessUniqueID())) {
                return sign;
            }
        }

        return null;
    }

    private @Nullable CloudSign getFreeSignForGroup(@NotNull ProcessGroup processGroup) {
        return Streams.filter(this.signs, e -> e.getCurrentTarget() == null && e.getGroup().equals(processGroup.getName()));
    }

    private boolean isProcessAssigned(@NotNull ProcessInformation process) {
        return this.getSignOf(process) != null;
    }

    private void tryAssign(@NotNull ProcessInformation processInformation) {
        synchronized (this) {
            for (CloudSign sign : this.signs) {
                if (sign.getCurrentTarget() == null && sign.getGroup().equals(processInformation.getProcessGroup().getName())) {
                    sign.setCurrentTarget(processInformation);
                    this.updateSigns();
                    break;
                }
            }
        }
    }
}
