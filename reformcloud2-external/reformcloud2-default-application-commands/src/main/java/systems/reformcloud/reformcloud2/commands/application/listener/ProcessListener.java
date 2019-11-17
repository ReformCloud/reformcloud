package systems.reformcloud.reformcloud2.commands.application.listener;

import systems.reformcloud.reformcloud2.commands.application.packet.out.PacketOutRegisterCommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class ProcessListener {

    private final Collection<UUID> connectedProcesses = new LinkedList<>();

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getNetworkInfo().isConnected()
                && !connectedProcesses.contains(event.getProcessInformation().getProcessUniqueID())) {
            connectedProcesses.add(event.getProcessInformation().getProcessUniqueID());
            waitFor(event.getProcessInformation().getName());
        }
    }

    @Listener
    public void handle(final ProcessStoppedEvent event) {
        connectedProcesses.remove(event.getProcessInformation().getProcessUniqueID());
    }

    private void waitFor(String name) {
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get(name).isPresent()) {
                AbsoluteThread.sleep(20);
            }

            AbsoluteThread.sleep(1000); // Wait for complete ready
            DefaultChannelManager.INSTANCE.get(name).ifPresent(
                    e -> e.sendPacket(new PacketOutRegisterCommandsConfig())
            );
        });
    }
}
