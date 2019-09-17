package de.klaro.reformcloud2.executor.client.process.basic;

import de.klaro.reformcloud2.executor.api.client.process.ProcessManager;
import de.klaro.reformcloud2.executor.api.client.process.RunningProcess;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.client.ClientExecutor;
import de.klaro.reformcloud2.executor.client.packet.out.ClientPacketOutProcessRegistered;
import de.klaro.reformcloud2.executor.client.packet.out.ClientPacketOutProcessStopped;
import de.klaro.reformcloud2.executor.client.screen.ProcessScreen;

import java.util.*;

public final class DefaultProcessManager implements ProcessManager {

    private final List<RunningProcess> list = new ArrayList<>();

    @Override
    public void registerProcess(RunningProcess runningProcess) {
        list.add(runningProcess);
        ClientExecutor.getInstance().getScreenManager().getPerProcessScreenLines().put(
                runningProcess.getProcessInformation().getProcessUniqueID(),
                new ProcessScreen(runningProcess.getProcessInformation().getProcessUniqueID())
        );

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketOutProcessRegistered(
                runningProcess.getProcessInformation().getProcessUniqueID(),
                runningProcess.getProcessInformation().getName()
        )));
    }

    @Override
    public void unregisterProcess(String name) {
        Links.filterToOptional(list, runningProcess -> runningProcess.getProcessInformation().getName().equals(name)).ifPresent(runningProcess -> {
            list.remove(runningProcess);

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketOutProcessStopped(
                    runningProcess.getProcessInformation().getProcessUniqueID(),
                    runningProcess.getProcessInformation().getName()
            )));
        });
    }

    @Override
    public Optional<RunningProcess> getProcess(UUID uniqueID) {
        return Links.filterToOptional(list, runningProcess -> runningProcess.getProcessInformation().getProcessUniqueID().equals(uniqueID));
    }

    @Override
    public Optional<RunningProcess> getProcess(String name) {
        return Links.filterToOptional(list, runningProcess -> runningProcess.getProcessInformation().getName().equals(name));
    }

    @Override
    public Collection<RunningProcess> getAll() {
        return Collections.unmodifiableCollection(list);
    }

    @Override
    public void onProcessDisconnect(UUID uuid) {
        Links.filterToOptional(list, runningProcess -> runningProcess.getProcessInformation().getProcessUniqueID().equals(uuid)).ifPresent(list::remove);
    }

    @Override
    public void stopAll() {
        Links.newList(list).forEach(RunningProcess::shutdown);
        list.clear();
    }
}
