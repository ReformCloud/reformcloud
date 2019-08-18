package de.klaro.reformcloud2.executor.api.common.application;

public interface ApplicationHandler {

    void onDetectApplications();

    void onInstallApplications();

    void onLoadApplications();

    void onEnableApplications();

    void onDisableApplications();
}
