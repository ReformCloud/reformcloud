package systems.reformcloud.reformcloud2.executor.client.screen;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.client.packet.out.ClientPacketOutAddScreenLine;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ProcessScreen {

    public ProcessScreen(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;

    private final Queue<String> queue = new ConcurrentLinkedQueue<>();

    private boolean enabled = false;

    void addScreenLine(String line) {
        while (queue.size() >= 128) {
            queue.poll();
        }

        queue.add(line);
        if (enabled) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketOutAddScreenLine(uuid, line)));
        }
    }

    public void toggleScreen() {
        this.enabled = !this.enabled;
    }
}
