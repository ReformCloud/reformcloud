package de.klaro.reformcloud2.executor.api.packets.in;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.Plugin;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import de.klaro.reformcloud2.executor.api.executor.PluginExecutor;

import java.util.function.Consumer;

public final class APIPacketInPluginAction implements NetworkHandler {

    public APIPacketInPluginAction(PluginExecutor pluginExecutor) {
        this.pluginExecutor = pluginExecutor;
    }

    private final PluginExecutor pluginExecutor;

    @Override
    public int getHandlingPacketID() {
        return 47;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        if (packet.content().has("installable")) {
            DefaultInstallablePlugin plugin = packet.content().get("installable", InstallablePlugin.INSTALLABLE_TYPE);
            pluginExecutor.installPlugin(plugin);
        } else {
            DefaultPlugin plugin = packet.content().get("plugin", Plugin.TYPE);
            pluginExecutor.uninstallPlugin(plugin);
        }
    }
}
