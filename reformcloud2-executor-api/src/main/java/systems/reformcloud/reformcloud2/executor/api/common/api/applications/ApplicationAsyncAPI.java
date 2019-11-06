package systems.reformcloud.reformcloud2.executor.api.common.api.applications;

import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.List;

public interface ApplicationAsyncAPI extends ApplicationSyncAPI {

    /**
     * Loads a specific controller application
     *
     * @param application The application which should be loaded
     * @return A task which will be completed if the handler finished the process
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> loadApplicationAsync(@Nonnull InstallableApplication application);

    /**
     * Unloads a specific application
     *
     * @param application The application instance which should be unloaded
     * @return A task which will be completed if the handler finished the process
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> unloadApplicationAsync(@Nonnull LoadedApplication application);

    /**
     * Unloads a specific application
     *
     * @param application The name of the application
     * @return A task which will be completed if the handler finished the process
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> unloadApplicationAsync(@Nonnull String application);

    /**
     * Gets a specific application
     *
     * @param name The name of the application
     * @return A task which will be completed with the loaded application or {@code null} if the app is unloaded
     */
    @Nonnull
    @CheckReturnValue
    Task<LoadedApplication> getApplicationAsync(@Nonnull String name);

    /**
     * Gets all currently loaded applications
     *
     * @return A task which will be completed with a list of all known and loaded application
     */
    @Nonnull
    @CheckReturnValue
    Task<List<LoadedApplication>> getApplicationsAsync();
}
