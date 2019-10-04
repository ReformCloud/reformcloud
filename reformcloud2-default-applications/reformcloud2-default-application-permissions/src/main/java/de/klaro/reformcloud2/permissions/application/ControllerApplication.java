package de.klaro.reformcloud2.permissions.application;

import de.klaro.reformcloud2.executor.api.common.application.api.Application;
import de.klaro.reformcloud2.permissions.packets.PacketHelper;

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
