package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.application.InstallableApplication;
import de.klaro.reformcloud2.executor.api.common.application.basic.DefaultInstallableApplication;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutLoadApplication extends DefaultPacket {

    public ExternalAPIPacketOutLoadApplication(InstallableApplication application) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 1, new JsonConfiguration().add("app", convert(application)));
    }

    private static DefaultInstallableApplication convert(InstallableApplication application) {
        return new DefaultInstallableApplication(application.url(), application.loader(), application.getName());
    }
}
