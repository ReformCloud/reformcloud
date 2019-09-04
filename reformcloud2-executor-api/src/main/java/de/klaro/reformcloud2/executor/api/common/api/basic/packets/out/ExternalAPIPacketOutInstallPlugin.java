package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;

public final class ExternalAPIPacketOutInstallPlugin extends DefaultPacket {

    public ExternalAPIPacketOutInstallPlugin(InstallablePlugin plugin, String process) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 27, new JsonConfiguration()
                .add("process", process)
                .add("plugin", convert(plugin))
        );
    }

    private static DefaultInstallablePlugin convert(InstallablePlugin plugin) {
        return new DefaultInstallablePlugin(plugin.getDownloadURL(), plugin.getName(), plugin.version(), plugin.author(), plugin.main());
    }
}
