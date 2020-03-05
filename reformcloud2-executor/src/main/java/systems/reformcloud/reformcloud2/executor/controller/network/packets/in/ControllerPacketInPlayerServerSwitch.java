package systems.reformcloud.reformcloud2.executor.controller.network.packets.in;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerServerSwitchEvent;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventPlayerServerSwitch;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

public class ControllerPacketInPlayerServerSwitch extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 12;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uuid", UUID.class);
        String target = packet.content().getString("target");

        ControllerExecutor.getInstance().getEventManager().callEvent(new PlayerServerSwitchEvent(uuid, target));
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().forEach(process -> DefaultChannelManager.INSTANCE.get(process.getName()).ifPresent(channel -> channel.sendPacket(
                new ControllerEventPlayerServerSwitch(uuid, target)
        )));
    }
}
