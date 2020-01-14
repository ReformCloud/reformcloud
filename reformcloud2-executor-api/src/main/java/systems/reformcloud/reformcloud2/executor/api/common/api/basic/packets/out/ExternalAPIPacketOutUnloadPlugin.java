package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;

public final class ExternalAPIPacketOutUnloadPlugin extends JsonPacket {

    public ExternalAPIPacketOutUnloadPlugin(Plugin plugin, String process) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 28, new JsonConfiguration()
                .add("process", process)
                .add("plugin", convert(plugin))
        );
    }

    private static DefaultPlugin convert(Plugin plugin) {
        return new DefaultPlugin(plugin.version(), plugin.author(), plugin.main(), plugin.depends(), plugin.softpends(), plugin.enabled(), plugin.getName());
    }
}
