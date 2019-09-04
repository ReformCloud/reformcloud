package de.klaro.reformcloud2.executor.controller.packet.in.query;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;

import java.util.function.Consumer;

public final class ControllerQueryGetInstalledPlugin implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 29;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String name = packet.content().getString("name");
        String process = packet.content().getString("process");
        responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", convert(ExecutorAPI.getInstance().getInstalledPlugin(process, name)))));
    }

    private static DefaultPlugin convert(Plugin plugin) {
        return new DefaultPlugin(plugin.version(), plugin.author(), plugin.main(), plugin.depends(), plugin.softpends(), plugin.enabled(), plugin.getName());
    }
}
