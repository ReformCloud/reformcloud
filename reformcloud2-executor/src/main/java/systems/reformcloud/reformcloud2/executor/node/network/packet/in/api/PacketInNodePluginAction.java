package systems.reformcloud.reformcloud2.executor.node.network.packet.in.api;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PacketInNodePluginAction extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return 47;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        String process = packet.content().getString("process");

        if (packet.content().has("installable")) {
            DefaultInstallablePlugin plugin = packet.content().get("installable", InstallablePlugin.INSTALLABLE_TYPE);
            ExecutorAPI.getInstance().getSyncAPI().getPluginSyncAPI().installPlugin(process, plugin);
        } else {
            DefaultPlugin plugin = packet.content().get("plugin", Plugin.TYPE);
            ExecutorAPI.getInstance().getSyncAPI().getPluginSyncAPI().unloadPlugin(process, plugin);
        }
    }
}
