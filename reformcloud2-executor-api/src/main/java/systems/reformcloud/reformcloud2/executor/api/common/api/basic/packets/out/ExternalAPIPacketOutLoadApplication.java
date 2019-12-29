package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultInstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutLoadApplication extends JsonPacket {

    public ExternalAPIPacketOutLoadApplication(InstallableApplication application) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 1, new JsonConfiguration().add("app", convert(application)));
    }

    private static DefaultInstallableApplication convert(InstallableApplication application) {
        return new DefaultInstallableApplication(application.url(), application.loader(), application.getName());
    }
}
