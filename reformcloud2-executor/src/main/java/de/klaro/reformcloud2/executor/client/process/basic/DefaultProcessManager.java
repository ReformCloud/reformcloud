package de.klaro.reformcloud2.executor.client.process.basic;

import de.klaro.reformcloud2.executor.api.client.process.ProcessManager;
import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.client.packet.out.ClientPacketOutProcessRegistered;
import de.klaro.reformcloud2.executor.client.packet.out.ClientPacketOutProcessStopped;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class DefaultProcessManager implements ProcessManager {

    private final List<RunningProcess> list = new ArrayList<>();

    @Override
    public void registerProcess(RunningProcess runningProcess) {
        list.add(runningProcess);
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
            @Override
            public void accept(PacketSender packetSender) {
                packetSender.sendPacket(new ClientPacketOutProcessRegistered(
                        runningProcess.getProcessInformation().getProcessUniqueID(),
                        runningProcess.getProcessInformation().getName()
                ));
            }
        });
    }

    @Override
    public void unregisterProcess(String name) {
        Links.filterToOptional(list, new Predicate<RunningProcess>() {
            @Override
            public boolean test(RunningProcess runningProcess) {
                return runningProcess.getProcessInformation().getName().equals(name);
            }
        }).ifPresent(new Consumer<RunningProcess>() {
            @Override
            public void accept(RunningProcess runningProcess) {
                list.remove(runningProcess);
                DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
                    @Override
                    public void accept(PacketSender packetSender) {
                        packetSender.sendPacket(new ClientPacketOutProcessStopped(
                                runningProcess.getProcessInformation().getProcessUniqueID(),
                                runningProcess.getProcessInformation().getName()
                        ));
                    }
                });
            }
        });
    }

    @Override
    public Optional<RunningProcess> getProcess(UUID uniqueID) {
        return Links.filterToOptional(list, new Predicate<RunningProcess>() {
            @Override
            public boolean test(RunningProcess runningProcess) {
                return runningProcess.getProcessInformation().getProcessUniqueID().equals(uniqueID);
            }
        });
    }

    @Override
    public Collection<RunningProcess> getAll() {
        return Collections.unmodifiableCollection(list);
    }

    @Override
    public void onProcessDisconnect(UUID uuid) {
        Links.filterToOptional(list, new Predicate<RunningProcess>() {
            @Override
            public boolean test(RunningProcess runningProcess) {
                return runningProcess.getProcessInformation().getProcessUniqueID().equals(uuid);
            }
        }).ifPresent(new Consumer<RunningProcess>() {
            @Override
            public void accept(RunningProcess runningProcess) {
                runningProcess.shutdown();
                list.remove(runningProcess);
            }
        });
    }

    @Override
    public void stopAll() {
        Links.newList(list).forEach(new Consumer<RunningProcess>() {
            @Override
            public void accept(RunningProcess runningProcess) {
                runningProcess.shutdown();
            }
        });
        list.clear();
    }
}
