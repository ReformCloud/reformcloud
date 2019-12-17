package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents an application which is loaded
 *
 * @see ApplicationLoader#getApplication(String)
 */
public interface LoadedApplication extends Nameable {

    TypeToken<DefaultLoadedApplication> TYPE = new TypeToken<DefaultLoadedApplication>() {};

    /**
     * @return The application loader which has loaded the application
     */
    @Nonnull
    ApplicationLoader loader();

    /**
     * @return The current instance of the {@link ExecutorAPI}
     */
    @Nonnull
    ExecutorAPI api();

    /**
     * @return The provided config of the application
     */
    @Nonnull
    ApplicationConfig applicationConfig();

    /**
     * @return The current lifecycle status of the application
     */
    @Nonnull
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
    void setApplicationStatus(@Nonnull ApplicationStatus status);

    /**
     * @see #applicationConfig()
     * @return The name of the application
     */
    @Nonnull
    @Override
    default String getName() {
        return applicationConfig().getName();
    }
}
