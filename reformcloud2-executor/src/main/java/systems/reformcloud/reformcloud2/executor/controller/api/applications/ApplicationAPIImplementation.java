package systems.reformcloud.reformcloud2.executor.controller.api.applications;

import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.applications.ApplicationSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
import java.util.List;

public class ApplicationAPIImplementation implements ApplicationAsyncAPI, ApplicationSyncAPI {

    private final ApplicationLoader applicationLoader;

    public ApplicationAPIImplementation(@Nonnull ApplicationLoader applicationLoader) {
        this.applicationLoader = applicationLoader;
    }

    @Nonnull
    @Override
    public Task<Boolean> loadApplicationAsync(@Nonnull InstallableApplication application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.doSpecificApplicationInstall(application)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> unloadApplicationAsync(@Nonnull LoadedApplication application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.doSpecificApplicationUninstall(application)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> unloadApplicationAsync(@Nonnull String application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.doSpecificApplicationUninstall(application)));
        return task;
    }

    @Nonnull
    @Override
    public Task<LoadedApplication> getApplicationAsync(@Nonnull String name) {
        Task<LoadedApplication> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.getApplication(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<LoadedApplication>> getApplicationsAsync() {
        Task<List<LoadedApplication>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.getApplications()));
        return task;
    }

    @Override
    public boolean loadApplication(@Nonnull InstallableApplication application) {
        return applicationLoader.doSpecificApplicationInstall(application);
    }

    @Override
    public boolean unloadApplication(@Nonnull LoadedApplication application) {
        return applicationLoader.doSpecificApplicationUninstall(application);
    }

    @Override
    public boolean unloadApplication(@Nonnull String application) {
        return applicationLoader.doSpecificApplicationUninstall(application);
    }

    @Override
    public LoadedApplication getApplication(@Nonnull String name) {
        return applicationLoader.getApplication(name);
    }

    @Override
    public List<LoadedApplication> getApplications() {
        return applicationLoader.getApplications();
    }
}
