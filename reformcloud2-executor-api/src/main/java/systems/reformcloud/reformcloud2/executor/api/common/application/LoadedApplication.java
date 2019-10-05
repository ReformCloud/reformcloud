package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface LoadedApplication extends Nameable {

    TypeToken<DefaultLoadedApplication> TYPE = new TypeToken<DefaultLoadedApplication>() {};

    ApplicationLoader loader();

    ExecutorAPI api();

    ApplicationConfig applicationConfig();

    ApplicationStatus applicationStatus();

    @Nullable
    Class<?> mainClass();

    void setApplicationStatus(ApplicationStatus status);

    @Override
    default String getName() {
        return applicationConfig().getName();
    }
}
