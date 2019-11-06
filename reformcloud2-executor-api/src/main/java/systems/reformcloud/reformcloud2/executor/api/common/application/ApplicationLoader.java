package systems.reformcloud.reformcloud2.executor.api.common.application;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nullable
    LoadedApplication getApplication(@Nonnull String name);

    @Nonnull
    String getApplicationName(@Nonnull LoadedApplication loadedApplication);

    @Nonnull
    List<LoadedApplication> getApplications();

    void addApplicationHandler(ApplicationHandler applicationHandler);
}
