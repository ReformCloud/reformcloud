package systems.reformcloud.reformcloud2.executor.api.common.api.applications;

import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.List;

public interface ApplicationAsyncAPI extends ApplicationSyncAPI {

    /**
     * Loads a specific controller application
     *
     * @param application The application which should be loaded
     * @return A task which will be completed if the handler finished the process
     */
    Task<Boolean> loadApplicationAsync(InstallableApplication application);

    /**
     * Unloads a specific application
     *
     * @param application The application instance which should be unloaded
     * @return A task which will be completed if the handler finished the process
     */
    Task<Boolean> unloadApplicationAsync(LoadedApplication application);

    /**
     * Unloads a specific application
     *
     * @param application The name of the application
     * @return A task which will be completed if the handler finished the process
     */
    Task<Boolean> unloadApplicationAsync(String application);

    /**
     * Gets a specific application
     *
     * @param name The name of the application
     * @return A task which will be completed with the loaded application or {@code null} if the app is unloaded
     */
    Task<LoadedApplication> getApplicationAsync(String name);

    /**
     * Gets all currently loaded applications
     *
     * @return A task which will be completed with a list of all known and loaded application
     */
    Task<List<LoadedApplication>> getApplicationsAsync();
}
