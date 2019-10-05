package systems.reformcloud.reformcloud2.executor.api.common.application.basic;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationConfig;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationStatus;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;

public final class DefaultLoadedApplication implements LoadedApplication {

    public DefaultLoadedApplication(ApplicationLoader loader, ApplicationConfig application, Class<?> main) {
        this.loader = loader;
        this.application = application;
        this.main = main;
        this.applicationStatus = ApplicationStatus.INSTALLABLE;
    }

    private final ApplicationLoader loader;

    private final Class<?> main;

    private final ApplicationConfig application;

    private ApplicationStatus applicationStatus;

    @Override
    public ApplicationLoader loader() {
        return loader;
    }

    @Override
    public ExecutorAPI api() {
        return ExecutorAPI.getInstance();
    }

    @Override
    public ApplicationConfig applicationConfig() {
        return application;
    }

    @Override
    public ApplicationStatus applicationStatus() {
        return applicationStatus;
    }

    @Override
    public Class<?> mainClass() {
        return main;
    }

    @Override
    public void setApplicationStatus(ApplicationStatus status) {
        this.applicationStatus = status;
    }
}
