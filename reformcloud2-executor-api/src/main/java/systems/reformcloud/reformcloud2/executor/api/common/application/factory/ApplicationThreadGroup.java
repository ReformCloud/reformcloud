package systems.reformcloud.reformcloud2.executor.api.common.application.factory;

import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;

public final class ApplicationThreadGroup extends ThreadGroup {

    public ApplicationThreadGroup(LoadedApplication application) {
        super(application.getName());
    }
}
