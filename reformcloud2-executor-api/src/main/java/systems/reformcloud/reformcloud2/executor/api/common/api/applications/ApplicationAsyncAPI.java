package systems.reformcloud.reformcloud2.executor.api.common.api.applications;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.List;

public interface ApplicationAsyncAPI {

    /**
     * Loads a specific controller application
     *
     * @param application The application which should be loaded
     * @return A task which will be completed if the handler finished the process
     */
    @NotNull
    Task<Boolean> loadApplicationAsync(@NotNull InstallableApplication application);

    /**
     * Unloads a specific application
     *
     * @param application The application instance which should be unloaded
     * @return A task which will be completed if the handler finished the process
     */
    @NotNull
    Task<Boolean> unloadApplicationAsync(@NotNull LoadedApplication application);

    /**
     * Unloads a specific application
     *
     * @param application The name of the application
     * @return A task which will be completed if the handler finished the process
     */
    @NotNull
    Task<Boolean> unloadApplicationAsync(@NotNull String application);

    /**
     * Gets a specific application
     *
     * @param name The name of the application
     * @return A task which will be completed with the loaded application or {@code null} if the app is unloaded
     */
    @NotNull
    Task<LoadedApplication> getApplicationAsync(@NotNull String name);

    /**
     * Gets all currently loaded applications
     *
     * @return A task which will be completed with a list of all known and loaded application
     */
    @NotNull
    Task<List<LoadedApplication>> getApplicationsAsync();
}
