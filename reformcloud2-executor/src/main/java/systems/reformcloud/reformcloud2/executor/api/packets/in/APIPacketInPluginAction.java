package systems.reformcloud.reformcloud2.executor.api.packets.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.executor.PluginExecutor;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class APIPacketInPluginAction extends DefaultJsonNetworkHandler {

    private final PluginExecutor pluginExecutor;

    public APIPacketInPluginAction(PluginExecutor pluginExecutor) {
        this.pluginExecutor = pluginExecutor;
    }

    @Override
    public int getHandlingPacketID() {
        return 47;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        if (packet.content().has("installable")) {
            DefaultInstallablePlugin plugin = packet.content().get("installable", InstallablePlugin.INSTALLABLE_TYPE);
            pluginExecutor.installPlugin(plugin);
        } else {
            DefaultPlugin plugin = packet.content().get("plugin", Plugin.TYPE);
            pluginExecutor.uninstallPlugin(plugin);
        }
    }
}
