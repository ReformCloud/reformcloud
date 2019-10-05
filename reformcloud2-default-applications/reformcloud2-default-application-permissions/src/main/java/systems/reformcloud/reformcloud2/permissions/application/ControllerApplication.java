package systems.reformcloud.reformcloud2.permissions.application;

import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

public class ControllerApplication extends Application {

    @Override
    public void onEnable() {
        PacketHelper.addControllerPackets();
    }

    @Override
    public void onUninstall() {
        PacketHelper.unregisterControllerPackets();
    }
}
