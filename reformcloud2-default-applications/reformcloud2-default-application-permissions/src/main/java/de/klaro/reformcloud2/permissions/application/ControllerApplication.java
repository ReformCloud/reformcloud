package de.klaro.reformcloud2.permissions.application;

import de.klaro.reformcloud2.executor.api.common.application.api.Application;

public class ControllerApplication extends Application {

    private static ControllerApplication instance;

    @Override
    public void onInstallable() {
        instance = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onUninstall() {
        super.onUninstall();
    }
}
