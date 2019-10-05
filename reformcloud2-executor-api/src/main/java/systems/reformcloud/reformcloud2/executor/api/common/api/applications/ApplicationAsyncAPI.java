package systems.reformcloud.reformcloud2.executor.api.common.api.applications;

import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.List;

public interface ApplicationAsyncAPI extends ApplicationSyncAPI {

    Task<Boolean> loadApplicationAsync(InstallableApplication application);

    Task<Boolean> unloadApplicationAsync(LoadedApplication application);

    Task<Boolean> unloadApplicationAsync(String application);

    Task<LoadedApplication> getApplicationAsync(String name);

    Task<List<LoadedApplication>> getApplicationsAsync();
}
