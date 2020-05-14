package systems.reformcloud.reformcloud2.executor.client.process.basic;

import systems.reformcloud.reformcloud2.executor.api.client.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.api.client.process.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.packet.out.ClientPacketOutProcessRegistered;
import systems.reformcloud.reformcloud2.executor.client.screen.ProcessScreen;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class DefaultProcessManager implements ProcessManager {

    private final List<RunningProcess> list = new CopyOnWriteArrayList<>();

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
        Streams.filterToReference(list, runningProcess -> runningProcess.getProcessInformation().getName().equals(name)).ifPresent(runningProcess -> {
            list.remove(runningProcess);
            ClientExecutor.getInstance().getScreenManager().getPerProcessScreenLines().remove(runningProcess.getProcessInformation().getProcessUniqueID());
        });
    }

    @Override
    public ReferencedOptional<RunningProcess> getProcess(UUID uniqueID) {
        return Streams.filterToReference(list, runningProcess -> runningProcess.getProcessInformation().getProcessUniqueID().equals(uniqueID));
    }

    @Override
    public ReferencedOptional<RunningProcess> getProcess(String name) {
        return Streams.filterToReference(list, runningProcess -> runningProcess.getProcessInformation().getName().equals(name));
    }

    @Override
    public Collection<RunningProcess> getAll() {
        return Collections.unmodifiableCollection(list);
    }

    @Override
    public void onProcessDisconnect(UUID uuid) {
        Streams.filterToReference(list, runningProcess -> runningProcess.getProcessInformation().getProcessUniqueID().equals(uuid)).ifPresent(RunningProcess::shutdown);
    }

    @Override
    public void stopAll() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        this.list.forEach(e -> executorService.submit(() -> e.shutdown()));

        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
