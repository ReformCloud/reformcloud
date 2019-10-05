package systems.reformcloud.reformcloud2.executor.api.common.api.applications;

import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;

import java.util.List;

public interface ApplicationSyncAPI {

    /**
     * Loads a specific controller application
     *
     * @param application The application which should be loaded
     * @return {@code true} if the load was successful else {@code false}
     */
    boolean loadApplication(InstallableApplication application);

    /**
     * Unloads a specific application
     *
     * @param application The application instance which should be unloaded
     * @return {@code true} if the load was successful else {@code false}
     */
    boolean unloadApplication(LoadedApplication application);

    /**
     * Unloads a specific application
     *
     * @param application The name of the application
     * @return {@code true} if the load was successful else {@code false}
     */
    boolean unloadApplication(String application);

    /**
     * Gets a specific application
     *
     * @param name The name of the application
     * @return the loaded application or {@code null} if the application is unloaded
     */
    LoadedApplication getApplication(String name);

    /**
     * Gets all currently loaded applications
     *
     * @return a list of all loaded applications
     */
    List<LoadedApplication> getApplications();
}
