package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface LoadedApplication extends Nameable {

    TypeToken<DefaultLoadedApplication> TYPE = new TypeToken<DefaultLoadedApplication>() {};

    @Nonnull
    ApplicationLoader loader();

    @Nonnull
    ExecutorAPI api();

    @Nonnull
    ApplicationConfig applicationConfig();

    @Nonnull
    ApplicationStatus applicationStatus();

    @Nullable
    Class<?> mainClass();

    void setApplicationStatus(ApplicationStatus status);

    @Nonnull
    @Override
    default String getName() {
        return applicationConfig().getName();
    }
}
