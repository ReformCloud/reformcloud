package systems.reformcloud.reformcloud2.executor.api.common.application;

import java.util.List;

public interface ApplicationLoader {

    void detectApplications();

    void installApplications();

    void loadApplications();

    void enableApplications();

    void disableApplications();

    boolean doSpecificApplicationInstall(InstallableApplication application);

    boolean doSpecificApplicationUninstall(LoadedApplication loadedApplication);

    boolean doSpecificApplicationUninstall(String application);

    LoadedApplication getApplication(String name);

    String getApplicationName(LoadedApplication loadedApplication);

    List<LoadedApplication> getApplications();

    void addApplicationHandler(ApplicationHandler applicationHandler);
}
