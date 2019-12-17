package systems.reformcloud.reformcloud2.executor.api.common.application.factory;

import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;

import javax.annotation.Nonnull;

public final class ApplicationThreadGroup extends ThreadGroup {

    public ApplicationThreadGroup(@Nonnull LoadedApplication application) {
        super(application.getName());
    }
}
