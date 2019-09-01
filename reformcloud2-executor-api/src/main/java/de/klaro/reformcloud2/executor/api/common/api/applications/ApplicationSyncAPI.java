package de.klaro.reformcloud2.executor.api.common.api.applications;

import de.klaro.reformcloud2.executor.api.common.application.InstallableApplication;
import de.klaro.reformcloud2.executor.api.common.application.LoadedApplication;

import java.util.List;

public interface ApplicationSyncAPI {

    boolean loadApplication(InstallableApplication application);

    boolean unloadApplication(LoadedApplication application);

    boolean unloadApplication(String application);

    LoadedApplication getApplication(String name);

    List<LoadedApplication> getApplications();
}
