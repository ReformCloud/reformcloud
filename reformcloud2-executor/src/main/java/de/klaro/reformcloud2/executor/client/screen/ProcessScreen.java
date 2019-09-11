package de.klaro.reformcloud2.executor.client.screen;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.client.packet.out.ClientPacketOutAddScreenLine;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class ProcessScreen {

    ProcessScreen(UUID uuid) {
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
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(new Consumer<PacketSender>() {
                @Override
                public void accept(PacketSender packetSender) {
                    packetSender.sendPacket(new ClientPacketOutAddScreenLine(uuid, line));
                }
            });
        }
    }

    public void toggleScreen() {
        this.enabled = !this.enabled;
    }
}
