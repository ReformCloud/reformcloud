package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

/**
 * This class represents an application which is loaded
 *
 * @see ApplicationLoader#getApplication(String)
 */
public interface LoadedApplication extends Nameable {

    TypeToken<DefaultLoadedApplication> TYPE = new TypeToken<DefaultLoadedApplication>() {
    };

    /**
     * @return The application loader which has loaded the application
     */
    @NotNull
    ApplicationLoader loader();

    /**
     * @return The current instance of the {@link ExecutorAPI}
     */
    @NotNull
    ExecutorAPI api();

    /**
     * @return The provided config of the application
     */
    @NotNull
    ApplicationConfig applicationConfig();

    /**
     * @return The current lifecycle status of the application
     */
    @NotNull
    ApplicationStatus applicationStatus();

    /**
     * @return The main class of the application
     */
    @Nullable
    Class<?> mainClass();

    /**
     * Updates the application status
     *
     * @param status The new status of the application
     */
    void setApplicationStatus(@NotNull ApplicationStatus status);

    /**
     * @return The name of the application
     * @see #applicationConfig()
     */
    @NotNull
    @Override
    default String getName() {
        return applicationConfig().getName();
    }
}
