package systems.reformcloud.reformcloud2.executor.controller.network.packets.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class ControllerQueryGetInstalledPlugin extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 29;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        String name = packet.content().getString("name");
        String process = packet.content().getString("process");
        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", convert(ExecutorAPI.getInstance().getSyncAPI().getPluginSyncAPI().getInstalledPlugin(process, name)))));
    }

    private static DefaultPlugin convert(Plugin plugin) {
        if (plugin == null) {
            return null;
        }

        return new DefaultPlugin(plugin.version(), plugin.author(), plugin.main(), plugin.depends(), plugin.softpends(), plugin.enabled(), plugin.getName());
    }
}
